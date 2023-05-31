package com.htuozhou.wvp.business.sip.request.impl;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.SIPProcessorObserver;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.ISIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.header.FromHeader;
import javax.sip.message.Request;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/4/15
 */
@Component
@Slf4j
public class MessageRequestProcessor extends AbstractSIPRequestProcessor implements InitializingBean, ISIPRequestProcessor {

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private ISIPService sipService;

    private static Map<String, IMessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    public void addMessageHandler(String name, IMessageHandler handler) {
        messageHandlerMap.put(name, handler);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sipProcessorObserver.addRequestProcessor(Request.MESSAGE,this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void process(RequestEvent requestEvent) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();

        Element rootElement = getRootElement(requestEvent);
        String name = rootElement.getName();
        IMessageHandler messageHandler = messageHandlerMap.get(name);
        if (Objects.isNull(messageHandler)) {
            log.warn("[SIP MESSAGE :{}] 暂不支持", name);
            return;
        }

        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        AddressImpl address = (AddressImpl) fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        String deviceId = uri.getUser();
        DeviceBO deviceBO = sipService.getDevice(deviceId);
        if (Objects.isNull(deviceBO) || Objects.equals(deviceBO.getStatus(),0)) {
            log.warn("[设备 {}]不存在或离线]",deviceId);
            return;
        }

        messageHandlerMap.get(name).handForDevice(requestEvent,sipService.getDevice(deviceId),rootElement);
    }
}
