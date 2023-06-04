package com.htuozhou.wvp.business.sip.request.impl.message.impl.notify;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.message.Response;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/4/15
 */
@Component
@Slf4j
public class KeepAliveNotifyMessageHandler extends AbstractSIPRequestProcessor implements InitializingBean, IMessageHandler {

    private static final String cmdType = "Keepalive";

    @Autowired
    private NotifyMessageHandler notifyMessageHandler;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private ISIPService sipService;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Override
    public void afterPropertiesSet() throws Exception {
        notifyMessageHandler.addMessageHandler(cmdType,this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element rootElement) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();
        // log.info("[SIP MESSAGE NOTIFY] 收到 [SIP ADDRESS:{} KEEPALIVE] 请求",requestAddress);
        log.info("[SIP MESSAGE NOTIFY] 收到 [SIP ADDRESS:{} KEEPALIVE] 请求，请求内容\n{}",requestAddress,request);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        // log.info("[SIP MESSAGE NOTIFY] [SIP ADDRESS:{} KEEPALIVE] 回复200",requestAddress);
        log.info("[SIP MESSAGE NOTIFY] [SIP ADDRESS:{} KEEPALIVE] 回复200，回复内容\n{}",requestAddress,response);

        deviceBO.setStatus(1);
        deviceBO.setIp(request.getRemoteAddress().getHostAddress());
        deviceBO.setPort(request.getRemotePort());
        deviceBO.setAddress(String.join(":",request.getRemoteAddress().getHostAddress(), String.valueOf(request.getRemotePort())));
        deviceBO.setKeepAliveTime(LocalDateTime.now());
        sipService.saveOrUpdateDevice(deviceBO);

        threadPoolTaskScheduler.schedule(() -> sipService.offline(deviceBO.getDeviceId()),
                Instant.now().plusMillis((long) deviceBO.getKeepAliveInterval() * 3 * 1000));
    }
}