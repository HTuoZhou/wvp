package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.bean.SSRCInfo;
import com.htuozhou.wvp.business.bean.StreamContent;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.dict.InviteSessionStatusDict;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.business.service.IPlayService;
import com.htuozhou.wvp.business.sip.SIPCommander;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.common.constant.DeferredResultConstant;
import com.htuozhou.wvp.common.constant.SIPConstant;
import com.htuozhou.wvp.common.result.ErrorCallback;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.common.utils.RedisUtil;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.MediaServerPO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sip.message.Request;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Service
@Slf4j
public class PlayServiceImpl implements IPlayService {

    @Autowired
    private IInviteStreamService inviteStreamService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZLMManager zlmManager;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private SIPProperties sipProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows(Exception.class)
    public void play(MediaServerBO mediaServerBO, DeviceBO deviceBO, String channelId, Integer ssrc, String uuid, ErrorCallback<Object> callback) {
        InviteInfo inviteInfo = inviteStreamService.getDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
        if (Objects.nonNull(inviteInfo)) {
            if (Objects.isNull(inviteInfo.getStreamContent())) {
                // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                log.info("[国标设备点播,已经请求中,等待结果],deviceBO.getDeviceId():{},channelId:{}", deviceBO.getDeviceId(), channelId);
                inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, callback);
            } else {
                StreamContent streamContent = inviteInfo.getStreamContent();
                String streamId = streamContent.getStreamId();
                if (StrUtil.isBlank(streamId)) {
                    log.error("[国标设备点播失败,{}],deviceBO.getDeviceId():{},,channelId:{}", ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), deviceBO.getDeviceId(), channelId);
                    callback.run(ResultCodeEnum.STREAM_ID_NOT_EXIST.getCode(), ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), null);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.STREAM_ID_NOT_EXIST.getCode(),
                            ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), null);
                }
                String mediaServerId = streamContent.getMediaServerId();
                MediaServerPO mediaServerPO = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                        .eq(MediaServerPO::getMediaServerId, mediaServerId));

                JSONArray jsonArray = zlmManager.getMediaList(MediaServerBO.po2bo(mediaServerPO), "rtp", null, streamId);
                if (jsonArray != null && jsonArray.size() > 0) {
                    log.info("[国标设备点播,流已存在,直接返回结果],deviceBO.getDeviceId():{},channelId:{}", deviceBO.getDeviceId(), channelId);
                    callback.run(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.SUCCESS.getCode(),
                            ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                } else {
                    // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                    log.info("[国标设备点播,流不存在],deviceBO.getDeviceId():{},channelId:{}", deviceBO.getDeviceId(), channelId);
                    inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, callback);
                    inviteStreamService.removeDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
                    deviceChannelService.update(Wrappers.<DeviceChannelPO>lambdaUpdate()
                            .set(DeviceChannelPO::getStreamId, null)
                            .eq(DeviceChannelPO::getDeviceId, deviceBO.getDeviceId())
                            .eq(DeviceChannelPO::getChannelId, channelId));
                }
            }
        }
        
        String streamId = String.format("%s_%s", deviceBO.getDeviceId(), channelId);

        if ((StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE) ||
                StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE)) &&
                deviceBO.getSsrcCheck()) {
            log.warn("[国标设备点播,开启SSRC校验时不支持TCP方式收流],deviceBO.getDeviceId():{},channelId:{}", deviceBO.getDeviceId(), channelId);
            ssrc = 0;
        }
        ssrc = Objects.nonNull(ssrc) ? ssrc : zlmManager.getPlaySsrc(mediaServerBO.getMediaServerId());
        int tcpMode = StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE) ? 2 :
                (StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE) ? 1 : 0);
        int rtpServerPort = mediaServerBO.getRtpEnable() ? zlmManager.createRtpSever(mediaServerBO, streamId, ssrc, 0, Boolean.FALSE, tcpMode) : mediaServerBO.getRtpProxyPort();
        if (rtpServerPort == -1) {
            log.error("[国标设备点播失败,{}] deviceBO.getDeviceId():{},channelId:{}", ResultCodeEnum.PORT_ASSIGN_ERROR.getMsg(), deviceBO.getDeviceId(), channelId);
            callback.run(ResultCodeEnum.PORT_ASSIGN_ERROR.getCode(), ResultCodeEnum.PORT_ASSIGN_ERROR.getMsg(), null);
            // inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.PORT_ASSIGN_ERROR.getCode(),
            //         ResultCodeEnum.PORT_ASSIGN_ERROR.getMsg(), null);
            zlmManager.releaseSsrc(mediaServerBO.getMediaServerId(), ssrc);
            return;
        }

        SSRCInfo ssrcInfo = new SSRCInfo(rtpServerPort, ssrc, streamId);
        inviteInfo = new InviteInfo(deviceBO.getDeviceId(), channelId, streamId, ssrcInfo, mediaServerBO.getSdpIp(), rtpServerPort,
                deviceBO.getStreamMode(), deviceBO.getPrimaryStream(), InviteSessionTypeDict.PLAY, InviteSessionStatusDict.READY, null);
        inviteStreamService.addDeviceInviteInfo(inviteInfo);
        dynamicTask.startDelay(uuid, () -> {
            // 执行超时任务时查询是否已经成功，成功了则不执行超时任务，防止超时任务取消失败的情况
            InviteInfo deviceInviteInfo = inviteStreamService.getDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
            if (Objects.isNull(deviceInviteInfo) || Objects.isNull(deviceInviteInfo.getStreamContent())) {
                log.error("[国标设备点播失败,{}] deviceBO.getDeviceId():{},channelId:{}", ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), deviceBO.getDeviceId(), channelId);
                callback.run(ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getCode(), ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), null);
                // inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getCode(),
                //         ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamService.removeDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
                deviceChannelService.update(Wrappers.<DeviceChannelPO>lambdaUpdate()
                        .set(DeviceChannelPO::getStreamId, null)
                        .eq(DeviceChannelPO::getDeviceId, deviceBO.getDeviceId())
                        .eq(DeviceChannelPO::getChannelId, channelId));
                zlmManager.releaseSsrc(mediaServerBO.getMediaServerId(), ssrcInfo.getSsrc());
                zlmManager.closeRtpServer(mediaServerBO, streamId);
                try {
                    sipCommander.streamBye(Request.BYE, deviceBO, channelId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, DeferredResultConstant.PLAY_TIME_OUT);

        sipCommander.playStreamCmd(mediaServerBO,ssrcInfo,deviceBO,channelId,(bo,param) -> {
            log.info("国标设备点播,收到on_stream_changed");
        });

    }
}
