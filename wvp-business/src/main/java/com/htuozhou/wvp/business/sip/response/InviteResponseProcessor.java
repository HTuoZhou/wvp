package com.htuozhou.wvp.business.sip.response;

import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.sip.SIPProcessorObserver;
import com.htuozhou.wvp.business.sip.SIPSender;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.SIPClientTransactionImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * @author hanzai
 * @date 2023/9/2
 */
@Component
@Slf4j
public class InviteResponseProcessor extends AbstractSIPResponseProcessor implements InitializingBean, ISIPResponseProcessor {

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SIPProperties sipProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        sipProcessorObserver.addResponseProcessor(Request.INVITE, this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void process(ResponseEvent responseEvent) {
        SIPResponse response = (SIPResponse) responseEvent.getResponse();
        ResponseEventExt responseEventExt = (ResponseEventExt) responseEvent;
        String responseAddress = responseEventExt.getRemoteIpAddress() + ":" + responseEventExt.getRemotePort();

        log.info("[SIP RESPONSE INVITE] 收到 [SIP ADDRESS:{}]", responseAddress);

        int statusCode = response.getStatusCode();
        if (statusCode == Response.OK) {
            SIPClientTransactionImpl transaction = (SIPClientTransactionImpl) responseEventExt.getOriginalTransaction();
            Request request = transaction.createAck();
            sipSender.transmitRequest(sipProperties.getIp(), request);
            log.info("[SIP RESPONSE INVITE] 回复ACK [SIP ADDRESS:{}]", responseAddress);
        }
    }
}
