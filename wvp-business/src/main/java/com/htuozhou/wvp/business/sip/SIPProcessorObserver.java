package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.sip.request.ISIPRequestProcessor;
import com.htuozhou.wvp.business.sip.response.ISIPResponseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sip.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/4/5
 */
@Component
@Slf4j
public class SIPProcessorObserver implements ISIPProcessorObserver{

    private static Map<String, ISIPRequestProcessor> requestProcessorMap = new ConcurrentHashMap<>();

    private static Map<String, ISIPResponseProcessor> responseProcessorMap = new ConcurrentHashMap<>();

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
            log.warn("[Request Method:{}] 暂不支持", method);
            return;
        }
        requestProcessorMap.get(method).process(requestEvent);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}
