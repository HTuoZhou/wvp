package com.htuozhou.wvp.business.sip.request.impl.message.impl.response;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import com.htuozhou.wvp.business.util.XmlUtils;
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

/**
 * @author hanzai
 * @date 2023/4/27
 */
@Component
@Slf4j
public class DeviceInfoResponseMessageHandler extends AbstractSIPRequestProcessor implements InitializingBean, IMessageHandler {

    private static final String cmdType = "DeviceInfo";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private ISIPService sipService;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addMessageHandler(cmdType, this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element rootElement) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();
        // log.info("[SIP MESSAGE RESPONSE] 收到 [SIP ADDRESS:{} DEVICE INFO] 请求",requestAddress);
        log.info("[SIP MESSAGE RESPONSE] 收到 [SIP ADDRESS:{} DEVICE INFO] 请求，请求内容\n{}", requestAddress, request);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        // log.info("[SIP MESSAGE RESPONSE] [SIP ADDRESS:{} DEVICE INFO] 回复200",requestAddress);
        log.info("[SIP MESSAGE RESPONSE] [SIP ADDRESS:{} DEVICE INFO] 回复200，回复内容\n{}",requestAddress,response);

        deviceBO.setName(XmlUtils.getText(rootElement, "DeviceName"));
        deviceBO.setManufacturer(XmlUtils.getText(rootElement, "Manufacturer"));
        deviceBO.setModel(XmlUtils.getText(rootElement, "Model"));
        deviceBO.setFirmware(XmlUtils.getText(rootElement, "Firmware"));
        sipService.saveOrUpdateDevice(deviceBO);
    }
}
