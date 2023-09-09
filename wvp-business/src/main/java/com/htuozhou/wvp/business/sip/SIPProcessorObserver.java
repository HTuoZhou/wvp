package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.sip.request.ISIPRequestProcessor;
import com.htuozhou.wvp.business.sip.response.ISIPResponseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/4/5
 */
@Component
@Slf4j
public class SIPProcessorObserver implements ISIPProcessorObserver {

    private static Map<String, ISIPRequestProcessor> requestProcessorMap = new ConcurrentHashMap<>();
    private static Map<String, ISIPResponseProcessor> responseProcessorMap = new ConcurrentHashMap<>();
    @Autowired
    private SIPSubscribe sipSubscribe;

    public void addRequestProcessor(String method, ISIPRequestProcessor processor) {
        requestProcessorMap.put(method, processor);
    }

    public void addResponseProcessor(String method, ISIPResponseProcessor processor) {
        responseProcessorMap.put(method, processor);
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        String method = requestEvent.getRequest().getMethod();
        ISIPRequestProcessor sipRequestProcessor = requestProcessorMap.get(method);
        if (Objects.isNull(sipRequestProcessor)) {
            log.warn("[SIP REQUEST {}] 暂不支持", method);
            return;
        }
        requestProcessorMap.get(method).process(requestEvent);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        CSeqHeader cSeqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
        String method = cSeqHeader.getMethod();
        ISIPResponseProcessor sipResponseProcessor = responseProcessorMap.get(method);

        if (Objects.isNull(sipResponseProcessor)) {
            log.warn("[SIP RESPONSE {}] 暂不支持", method);
            return;
        }

        responseProcessorMap.get(method).process(responseEvent);
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        log.info("[SIP TIMEOUT :{}]", timeoutEvent);
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        log.info("[SIP IOException :{}]", exceptionEvent);
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        log.info("[SIP TransactionTerminated :{}]", transactionTerminatedEvent);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        log.info("[SIP DialogTerminated :{}]", dialogTerminatedEvent);
    }
}
