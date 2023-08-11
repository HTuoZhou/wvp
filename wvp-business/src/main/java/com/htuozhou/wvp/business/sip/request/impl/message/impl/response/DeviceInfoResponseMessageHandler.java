package com.htuozhou.wvp.business.sip.request.impl.message.impl.response;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import com.htuozhou.wvp.common.utils.XmlUtil;
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
    private IDeviceService deviceService;

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
        log.info("[SIP MESSAGE RESPONSE] 收到 [SIP ADDRESS:{}] 设备信息\n{}", requestAddress, request);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        log.info("[SIP MESSAGE RESPONSE] 回复 [SIP ADDRESS:{}] 设备信息\n{}", requestAddress, response);

        deviceBO.setName(XmlUtil.getText(rootElement, "DeviceName"));
        deviceBO.setManufacturer(XmlUtil.getText(rootElement, "Manufacturer"));
        deviceBO.setModel(XmlUtil.getText(rootElement, "Model"));
        deviceBO.setFirmware(XmlUtil.getText(rootElement, "Firmware"));
        deviceBO.setChannels(Integer.valueOf(XmlUtil.getText(rootElement, "Channel")));
        deviceService.updateById(deviceBO.bo2po());
    }
}
