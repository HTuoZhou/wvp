package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.bean.SSRCInfo;
import com.htuozhou.wvp.business.bean.StreamContent;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.business.service.IPlayService;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.constant.SIPConstant;
import com.htuozhou.wvp.common.exception.BusinessException;
import com.htuozhou.wvp.common.result.ErrorCallback;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.common.utils.RedisUtil;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.po.MediaServerPO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SSRCInfo play(MediaServerBO mediaServerBO, String deviceId, String channelId, Integer ssrc, ErrorCallback<Object> callback) {
        InviteInfo inviteInfo = inviteStreamService.getDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceId, channelId, null);
        if (Objects.nonNull(inviteInfo)) {
            if (Objects.isNull(inviteInfo.getStreamContent())) {
                log.info("[国标设备点播,已经请求中,等待结果],deviceId:{},channelId:{}", deviceId, channelId);
                inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceId, channelId, null, callback);
                return inviteInfo.getSsrcInfo();
            } else {
                StreamContent streamContent = inviteInfo.getStreamContent();
                String streamId = streamContent.getStreamId();
                if (StrUtil.isBlank(streamId)) {
                    log.info("[国标设备点播,{}],deviceId:{},,channelId:{}", ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), deviceId, channelId);
                    callback.run(ResultCodeEnum.STREAM_ID_NOT_EXIST.getCode(), ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), null);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceId, channelId, null, ResultCodeEnum.STREAM_ID_NOT_EXIST.getCode(),
                            ResultCodeEnum.STREAM_ID_NOT_EXIST.getMsg(), null);
                    return inviteInfo.getSsrcInfo();
                }
                String mediaServerId = streamContent.getMediaServerId();
                MediaServerPO mediaServerPO = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                        .eq(MediaServerPO::getMediaServerId, mediaServerId));

                JSONArray jsonArray = zlmManager.getMediaList(MediaServerBO.po2bo(mediaServerPO), "rtp", null, streamId);
                if (jsonArray != null && jsonArray.size() > 0) {
                    log.info("[国标设备点播,已存在，直接返回]， deviceId: {}, channelId: {}", deviceId, channelId);
                    callback.run(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                    inviteStreamService.call(InviteSessionTypeDict.PLAY, deviceId, channelId, null, ResultCodeEnum.SUCCESS.getCode(),
                            ResultCodeEnum.SUCCESS.getMsg(), streamContent);
                    return inviteInfo.getSsrcInfo();
                } else {
                    log.info("[国标设备点播,不存在],deviceId:{},,channelId:{}", deviceId, channelId);
                    inviteStreamService.add(InviteSessionTypeDict.PLAY, deviceId, channelId, null, callback);
                    inviteStreamService.removeDeviceInviteInfo(InviteSessionTypeDict.PLAY, deviceId, channelId, null);
                    deviceChannelService.update(Wrappers.<DeviceChannelPO>lambdaUpdate()
                            .set(DeviceChannelPO::getStreamId, null)
                            .eq(DeviceChannelPO::getDeviceId, deviceId)
                            .eq(DeviceChannelPO::getChannelId, channelId));
                }
            }
        }

        DevicePO devicePO = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                .eq(DevicePO::getDeviceId, deviceId));
        String streamId = String.format("%s_%s", deviceId, channelId);
        if ((StrUtil.equalsIgnoreCase(devicePO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE)) ||
                StrUtil.equalsIgnoreCase(devicePO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE) && devicePO.getSsrcCheck()) {
            log.warn("[国标设备点播,开启SSRC校验时不支持TCP方式收流],deviceId:{},channelId:{}", deviceId, channelId);
            ssrc = 0;
        }
        ssrc = Objects.nonNull(ssrc) ? ssrc : getPlaySsrc(mediaServerBO.getMediaServerId());
        int tcpMode = StrUtil.equalsIgnoreCase(devicePO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_ACTIVE) ? 2 :
                (StrUtil.equalsIgnoreCase(devicePO.getStreamMode(), SIPConstant.STREAM_MODE_TCP_PASSIVE) ? 1 : 0);
        int rtpServerPort = mediaServerBO.getRtpEnable() ? mediaServerBO.getRtpProxyPort() : zlmManager.createRtpSever(mediaServerBO, streamId, ssrc, 0, Boolean.FALSE, tcpMode);

        return null;
    }

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     * @param mediaServerId
     * @return
     */
    public Integer getPlaySsrc(String mediaServerId) {
        return Integer.valueOf(("0" + getSN(mediaServerId)));
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     *
     * @param mediaServerId
     * @return
     */
    public Integer getPlayBackSsrc(String mediaServerId) {
        return Integer.valueOf(("1" + getSN(mediaServerId)));
    }

    /**
     * 获取后四位数SN,随机数
     *
     * @param mediaServerId
     * @return
     */
    private String getSN(String mediaServerId) {
        String sn = null;
        String key = String.format(RedisConstant.SSRC_INFO, mediaServerId);
        long size = redisUtil.sGetSetSize(key);
        if (size == 0) {
            throw new BusinessException(ResultCodeEnum.SSRC_UN_USABLE);
        } else {
            // 在集合中移除并返回一个随机成员。
            sn = (String) redisUtil.setPop(key);
            redisUtil.setRemove(key, sn);
        }
        return sn;
    }
}
