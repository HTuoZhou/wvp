package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bean.SSRCTransactionInfo;

/**
 * @author hanzai
 * @date 2023/9/5
 */
public interface IStreamSessionService {

    SSRCTransactionInfo getSSRCTransactionInfo(String deviceId, String channelId);

    void put(String deviceId, String channelId, SSRCTransactionInfo ssrcTransactionInfo);

    void remove(String deviceId, String channelId);

}
