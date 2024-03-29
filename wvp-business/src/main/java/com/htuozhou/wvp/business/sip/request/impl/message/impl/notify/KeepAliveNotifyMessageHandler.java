package com.htuozhou.wvp.business.sip.request.impl.message.impl.notify;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.common.constant.DynamicTaskConstant;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.message.Response;
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
    private IDeviceService deviceService;

    @Autowired
    private ISIPService sipService;

    @Autowired
    private DynamicTask dynamicTask;

    @Override
    public void afterPropertiesSet() throws Exception {
        notifyMessageHandler.addMessageHandler(cmdType, this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element rootElement) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();
        log.info("[SIP REQUEST MESSAGE NOTIFY] 收到 [SIP ADDRESS:{}] 心跳", requestAddress);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        log.info("[SIP REQUEST MESSAGE NOTIFY] 心跳回复 [SIP ADDRESS:{}]", requestAddress);

        deviceBO.setStatus(Boolean.TRUE);
        deviceBO.setIp(request.getRemoteAddress().getHostAddress());
        deviceBO.setPort(request.getRemotePort());
        deviceBO.setAddress(String.join(":", request.getRemoteAddress().getHostAddress(), String.valueOf(request.getRemotePort())));
        deviceBO.setKeepAliveTime(LocalDateTime.now());
        deviceService.updateById(deviceBO.bo2po());

        String key = String.format(DynamicTaskConstant.GB_DEVICE_STATUS, deviceBO.getDeviceId());
        dynamicTask.cancel(key);
        dynamicTask.startDelay(key, () -> sipService.offline(deviceBO), deviceBO.getKeepAliveInterval() * 3L);
    }
}