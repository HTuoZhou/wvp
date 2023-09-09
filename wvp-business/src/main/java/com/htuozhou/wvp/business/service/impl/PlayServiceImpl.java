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
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.business.service.IPlayService;
import com.htuozhou.wvp.business.service.IStreamSessionService;
import com.htuozhou.wvp.business.sip.SIPCommander;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribe;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribeFactory;
import com.htuozhou.wvp.business.zlm.param.OnStreamChangedHookParam;
import com.htuozhou.wvp.common.constant.DeferredResultConstant;
import com.htuozhou.wvp.common.constant.SIPConstant;
import com.htuozhou.wvp.common.result.Callback;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
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
    private DynamicTask dynamicTask;

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private ZlmHttpHookSubscribe zlmHttpHookSubscribe;

    @Autowired
    private IStreamSessionService streamSessionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void play(MediaServerBO mediaServerBO, DeviceBO deviceBO, String channelId, String ssrc, String uuid, Callback<Object> callback) {
        InviteInfo inviteInfo = inviteStreamService.getInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
        if (Objects.nonNull(inviteInfo)) {
            if (Objects.isNull(inviteInfo.getStreamContent())) {
                log.info("[国标设备点播,请求中,等待结果],deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
                inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, callback);
                return;
            } else {
                StreamContent streamContent = inviteInfo.getStreamContent();
                String streamId = streamContent.getStreamId();
                JSONArray jsonArray = zlmManager.getMediaList(mediaServerBO, "rtp", null, streamId);
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    log.info("[国标设备点播,流已存在,直接返回结果],deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
                    callback.run(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.SUCCESS.getCode(),
                            ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                    return;
                } else {
                    log.info("[国标设备点播,流不存在],deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
                }
            }
        }

        String streamId = String.format("%s_%s", deviceBO.getDeviceId(), channelId);

        if ((StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE) ||
                StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE)) &&
                deviceBO.getSsrcCheck()) {
            log.warn("[国标设备点播,开启SSRC校验时不支持TCP方式收流],deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
            ssrc = "0";
        }
        ssrc = StrUtil.isNotBlank(ssrc) ? ssrc : zlmManager.getPlaySsrc(mediaServerBO.getMediaServerId());
        int tcpMode = StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE) ? 2 :
                (StrUtil.equalsIgnoreCase(deviceBO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE) ? 1 : 0);
        int rtpServerPort = mediaServerBO.getRtpEnable() ? zlmManager.createRtpSever(mediaServerBO, streamId, ssrc, 0, Boolean.FALSE, tcpMode) : mediaServerBO.getRtpProxyPort();
        if (rtpServerPort == -1) {
            log.error("[国标设备点播失败,{}] deviceId:{},channelId:{}", ResultCodeEnum.PORT_ASSIGN_ERROR.getMsg(), deviceBO.getDeviceId(), channelId);
            callback.run(ResultCodeEnum.PORT_ASSIGN_ERROR.getCode(), ResultCodeEnum.PORT_ASSIGN_ERROR.getMsg(), null);
            zlmManager.releaseSsrc(mediaServerBO.getMediaServerId(), ssrc);
            return;
        }

        log.info("[国标设备点播],deviceId:{},channelId:{},streamId:{},ssrc:{},rtpServerPort:{}", deviceBO.getDeviceId(), channelId, streamId, ssrc, rtpServerPort);
        SSRCInfo ssrcInfo = new SSRCInfo(rtpServerPort, ssrc, streamId);
        inviteInfo = new InviteInfo(deviceBO.getDeviceId(), channelId, streamId, ssrcInfo, mediaServerBO.getSdpIp(), rtpServerPort,
                deviceBO.getStreamMode(), deviceBO.getPrimaryStream(), InviteSessionTypeDict.PLAY, InviteSessionStatusDict.READY, null);
        inviteStreamService.addInviteInfo(inviteInfo);
        inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, callback);

        dynamicTask.startDelay(uuid, () -> {
            // 执行超时任务时查询是否已经成功，成功了则不执行超时任务，防止超时任务取消失败的情况
            if (Objects.isNull(inviteStreamService.getInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId).getStreamContent())) {
                log.error("[国标设备点播失败,{}] deviceId:{},channelId:{}", ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), deviceBO.getDeviceId(), channelId);
                callback.run(ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getCode(), ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getCode(),
                        ResultCodeEnum.RECEIVE_STREAM_TIMEOUT.getMsg(), null);
                try {
                    log.error("[国标设备点播失败,BYE] deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
                    sipCommander.streamByeCmd(Request.BYE, deviceBO, channelId);
                } catch (Exception e) {
                    log.error("[国标设备点播失败,BYE失败] deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
                    callback.run(ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getCode(), ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getMsg(), null);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getCode(),
                            ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getMsg(), null);
                }
                inviteStreamService.removeInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, streamId);
                zlmManager.releaseSsrc(mediaServerBO.getMediaServerId(), ssrcInfo.getSsrc());
                zlmManager.closeRtpServer(mediaServerBO, streamId);
                zlmHttpHookSubscribe.removeSubscribe(ZlmHttpHookSubscribeFactory.onStreamChanged("rtp", streamId, Boolean.TRUE, "rtsp", mediaServerBO.getMediaServerId()));
            }
        }, DeferredResultConstant.PLAY_TIME_OUT);

        try {
            sipCommander.playStreamCmd(mediaServerBO, ssrcInfo, deviceBO, channelId, (bo, param) -> {
                OnStreamChangedHookParam onStreamChangedHookParam = (OnStreamChangedHookParam) param;
                log.info("[国标设备点播,收到ON_STREAM_CHANGED订阅] deviceId:{},channelId:{},onStreamChangedHookParam:{}", deviceBO.getDeviceId(), channelId, onStreamChangedHookParam);
                dynamicTask.cancel(uuid);

                StreamContent streamContent = StreamContent.convert(mediaServerBO, onStreamChangedHookParam);
                InviteInfo deviceInviteInfo = inviteStreamService.getInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId);
                deviceInviteInfo.setInviteSessionStatusDict(InviteSessionStatusDict.OK);
                deviceInviteInfo.setStreamContent(streamContent);

                callback.run(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.SUCCESS.getCode(),
                        ResultCodeEnum.SUCCESS.getMsg(), streamContent);

                deviceChannelService.update(Wrappers.<DeviceChannelPO>lambdaUpdate()
                        .set(DeviceChannelPO::getStreamId, streamContent.getStreamId())
                        .eq(DeviceChannelPO::getDeviceId, deviceBO.getDeviceId())
                        .eq(DeviceChannelPO::getChannelId, channelId));
                inviteStreamService.addInviteInfo(deviceInviteInfo);
                zlmManager.getSnap(bo, deviceBO.getDeviceId(), channelId);
            });
        } catch (Exception e) {
            dynamicTask.cancel(uuid);

            log.error("[国标设备点播,请求预览视频流失败] deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
            callback.run(ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getCode(), ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getMsg(), null);
            inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, null, ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getCode(),
                    ResultCodeEnum.SIP_COMMAND_SEND_ERROR.getMsg(), null);

            inviteStreamService.removeInviteInfo(InviteSessionTypeDict.PLAY, deviceBO.getDeviceId(), channelId, streamId);
            zlmManager.releaseSsrc(mediaServerBO.getMediaServerId(), ssrcInfo.getSsrc());
            zlmManager.closeRtpServer(mediaServerBO, streamId);
            zlmHttpHookSubscribe.removeSubscribe(ZlmHttpHookSubscribeFactory.onStreamChanged("rtp", streamId, Boolean.TRUE, "rtsp", mediaServerBO.getMediaServerId()));
        }
    }
}
