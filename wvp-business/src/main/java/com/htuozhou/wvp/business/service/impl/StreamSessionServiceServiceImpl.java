package com.htuozhou.wvp.business.service.impl;

import com.htuozhou.wvp.business.bean.SIPTransactionInfo;
import com.htuozhou.wvp.business.bean.SSRCTransactionInfo;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.service.IStreamSessionService;
import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.utils.RedisUtil;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hanzai
 * @date 2023/9/5
 */
@Service
@Slf4j
public class StreamSessionServiceServiceImpl implements IStreamSessionService {

    @Autowired
    private RedisUtil redisUtil;

    public SSRCTransactionInfo getSSRCTransactionInfo(String deviceId, String channelId) {
        String key = String.format(RedisConstant.STREAM_SESSION_INFO, deviceId, channelId);
        return (SSRCTransactionInfo) redisUtil.get(key);
    }

    @Override
    public void put(String deviceId, String channelId, String ssrc, String mediaServerId, SIPResponse response, InviteSessionTypeDict inviteSessionTypeDict) {
        SSRCTransactionInfo ssrcTransaction = new SSRCTransactionInfo();
        ssrcTransaction.setDeviceId(deviceId);
        ssrcTransaction.setChannelId(channelId);
        ssrcTransaction.setSipTransactionInfo(new SIPTransactionInfo(response));
        ssrcTransaction.setSsrc(ssrc);
        ssrcTransaction.setMediaServerId(mediaServerId);
        ssrcTransaction.setInviteSessionTypeDict(inviteSessionTypeDict);

        redisUtil.set(String.format(RedisConstant.STREAM_SESSION_INFO, deviceId, channelId), ssrcTransaction);
    }

    @Override
    public void remove(String deviceId, String channelId) {
        String key = String.format(RedisConstant.STREAM_SESSION_INFO, deviceId, channelId);
        redisUtil.delete(key);
    }
}
