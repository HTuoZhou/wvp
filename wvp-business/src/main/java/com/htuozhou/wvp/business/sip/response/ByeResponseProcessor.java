package com.htuozhou.wvp.business.sip.response;

import com.htuozhou.wvp.business.sip.SIPProcessorObserver;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.message.Request;

/**
 * @author hanzai
 * @date 2023/9/2
 */
@Component
@Slf4j
public class ByeResponseProcessor extends AbstractSIPResponseProcessor implements InitializingBean,ISIPResponseProcessor {

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Override
    public void afterPropertiesSet() throws Exception {
        sipProcessorObserver.addResponseProcessor(Request.BYE,this);
    }

    @Override
    public void process(ResponseEvent responseEvent) {
        SIPResponse response = (SIPResponse) responseEvent.getResponse();
        ResponseEventExt responseEventExt = (ResponseEventExt) responseEvent;
        String requestAddress = responseEventExt.getRemoteIpAddress() + ":" + responseEventExt.getRemotePort();

        log.info("[SIP RESPONSE BYE] 收到 [SIP ADDRESS:{}]", requestAddress);
    }
}
