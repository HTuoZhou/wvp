package com.htuozhou.wvp.business.service.impl;

import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.service.IStreamSessionService;
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

    @Override
    public void put(String deviceId, String channelId, String callId, String streamId, String ssrc, String mediaServerId, SIPResponse response, InviteSessionTypeDict inviteSessionTypeDict) {
    }

    @Override
    public void remove(String deviceId, String channelId, String streamId) {

    }
}
