package com.htuozhou.wvp.business.bean;

import gov.nist.javax.sip.message.SIPResponse;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/5
 */
@Data
public class SIPTransactionInfo {

    private String callId;
    private String fromTag;
    private String toTag;
    private String viaBranch;

    public SIPTransactionInfo() {
    }

    public SIPTransactionInfo(SIPResponse response) {
        this.callId = response.getCallIdHeader().getCallId();
        this.fromTag = response.getFromTag();
        this.toTag = response.getToTag();
        this.viaBranch = response.getTopmostViaHeader().getBranch();
    }
}
