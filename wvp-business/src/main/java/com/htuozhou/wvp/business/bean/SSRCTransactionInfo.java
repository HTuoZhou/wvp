package com.htuozhou.wvp.business.bean;

import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/5
 */
@Data
public class SSRCTransactionInfo {

    private String deviceId;
    private String channelId;
    private String mediaServerId;
    private String ssrc;
    private SIPTransactionInfo sipTransactionInfo;
    private InviteSessionTypeDict inviteSessionTypeDict;

}
