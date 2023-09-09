package com.htuozhou.wvp.business.bean;

import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/5
 */
@Data
public class SSRCTransactionInfo {

    private String mediaServerId;
    private InviteSessionTypeDict inviteSessionTypeDict;
    private String deviceId;
    private String channelId;
    private String ssrc;
    private String callId;

}
