package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.properties.SIPProperties;
import gov.nist.javax.sip.SipProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;

/**
 * @author hanzai
 * @date 2023/4/21
 */
@Component
@Slf4j
public class SIPCommander {

    @Autowired
    private SIPRunner sipRunner;

    @Autowired
    private SIPProperties sipProperties;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SIPRequestHeaderProvider sipRequestHeaderProvider;

    /**
     * 查询设备信息
     *
     * @param deviceBO
     */
    public void deviceInfoQuery(DeviceBO deviceBO) throws Exception {
        StringBuffer catalogXml = new StringBuffer(200);
        String charset = deviceBO.getCharset();
        catalogXml.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("<CmdType>DeviceInfo</CmdType>\r\n");
        catalogXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        catalogXml.append("<DeviceID>" + deviceBO.getDeviceId() + "</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");

        SipProviderImpl tcpSipProvider = sipRunner.getTcpSipProvider();
        SipProviderImpl udpSipProvider = sipRunner.getUdpSipProvider();
        String time = Long.toString(System.currentTimeMillis());

        CallIdHeader callIdHeader = deviceBO.getTransport().equals("TCP") ? tcpSipProvider.getNewCallId()
                : udpSipProvider.getNewCallId();
        Request request = sipRequestHeaderProvider.createRequest(Request.MESSAGE, deviceBO, null, catalogXml.toString(), "z9hG4bK" + time, time, null, callIdHeader);
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER] [SIP ADDRESS:{}] 查询设备信息", deviceBO.getAddress());
    }

    /**
     * 查询设备通道信息
     *
     * @param deviceBO
     */
    public void catalogQuery(DeviceBO deviceBO) throws Exception {
        StringBuffer catalogXml = new StringBuffer(200);
        String charset = deviceBO.getCharset();
        catalogXml.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("  <CmdType>Catalog</CmdType>\r\n");
        catalogXml.append("  <SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        catalogXml.append("  <DeviceID>" + deviceBO.getDeviceId() + "</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");

        SipProviderImpl tcpSipProvider = sipRunner.getTcpSipProvider();
        SipProviderImpl udpSipProvider = sipRunner.getUdpSipProvider();
        String time = Long.toString(System.currentTimeMillis());

        CallIdHeader callIdHeader = deviceBO.getTransport().equals("TCP") ? tcpSipProvider.getNewCallId()
                : udpSipProvider.getNewCallId();
        Request request = sipRequestHeaderProvider.createRequest(Request.MESSAGE, deviceBO, null, catalogXml.toString(), "z9hG4bK" + time, time, null, callIdHeader);
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER] [SIP ADDRESS:{}] 查询设备通道信息", deviceBO.getAddress());
    }

    public void streamBye(String requestType, DeviceBO deviceBO, String channelId) throws Exception {
        SipProviderImpl tcpSipProvider = sipRunner.getTcpSipProvider();
        SipProviderImpl udpSipProvider = sipRunner.getUdpSipProvider();
        String time = Long.toString(System.currentTimeMillis());

        CallIdHeader callIdHeader = deviceBO.getTransport().equals("TCP") ? tcpSipProvider.getNewCallId()
                : udpSipProvider.getNewCallId();
        Request request = sipRequestHeaderProvider.createRequest(requestType, deviceBO, channelId, null, "z9hG4bK" + time, time, null, callIdHeader);
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER] [SIP ADDRESS:{}] 发送BYE\n{}", deviceBO.getAddress(), request);
    }
}
