package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bean.SSRCTransactionInfo;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import gov.nist.javax.sip.message.SIPResponse;

/**
 * @author hanzai
 * @date 2023/9/5
 */
public interface IStreamSessionService {

    SSRCTransactionInfo getSSRCTransactionInfo(String deviceId, String channelId);

    void put(String deviceId, String channelId, String ssrc, String mediaServerId, SIPResponse response, InviteSessionTypeDict inviteSessionTypeDict);

    void remove(String deviceId, String channelId);

}
