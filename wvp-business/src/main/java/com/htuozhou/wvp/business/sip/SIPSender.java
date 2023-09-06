package com.htuozhou.wvp.business.sip;

import cn.hutool.core.util.StrUtil;
import com.htuozhou.wvp.common.constant.CommonConstant;
import gov.nist.javax.sip.SipProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sip.header.CallIdHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.Collections;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/12
 */
@Component
@Slf4j
public class SIPSender {

    @Autowired
    private Environment environment;

    @Autowired
    private SIPSubscribe sipSubscribe;

    @Autowired
    private SIPRunner sipRunner;

    public void transmitRequest(String ip, Message message) throws Exception {
        transmitRequest(ip, message, null, null);
    }

    public void transmitRequest(String ip, Message message, SIPSubscribe.Event okEvent, SIPSubscribe.Event errorEvent) throws Exception {
        ViaHeader viaHeader = (ViaHeader) message.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (Objects.nonNull(viaHeader)) {
            transport = viaHeader.getTransport();
        }

        UserAgentHeader userAgentHeader = (UserAgentHeader) message.getHeader(UserAgentHeader.NAME);
        if (Objects.isNull(userAgentHeader)) {
            message.addHeader(sipRunner
                    .getSipFactory()
                    .createHeaderFactory()
                    .createUserAgentHeader(Collections.singletonList(environment.getProperty(CommonConstant.SPRING_APPLICATION_NAME))));
        }

        CallIdHeader callIdHeader = (CallIdHeader) message.getHeader(CallIdHeader.NAME);
        if (Objects.nonNull(okEvent)) {
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), (okeventResult) -> {
                okEvent.response(okeventResult);
                sipSubscribe.removeOkSubscribe(okeventResult.callId);
            });
        }
        if (Objects.nonNull(errorEvent)) {
            sipSubscribe.addErrorSubscribe(callIdHeader.getCallId(), (errorEventResult) -> {
                errorEvent.response(errorEventResult);
                sipSubscribe.removeErrorSubscribe(errorEventResult.callId);
            });
        }

        if ("TCP".equals(transport)) {
            SipProviderImpl tcpSipProvider = sipRunner.getTcpSipProvider(ip);
            if (message instanceof Request) {
                tcpSipProvider.sendRequest((Request) message);
            } else if (message instanceof Response) {
                tcpSipProvider.sendResponse((Response) message);
            }

        } else if ("UDP".equals(transport)) {
            SipProviderImpl udpSipProvider = sipRunner.getUdpSipProvider(ip);
            if (message instanceof Request) {
                udpSipProvider.sendRequest((Request) message);
            } else if (message instanceof Response) {
                udpSipProvider.sendResponse((Response) message);
            }
        }
    }

    public CallIdHeader getCallIdHeader(String ip, String transport) {
        if (StrUtil.isBlank(transport)) {
            return sipRunner.getUdpSipProvider().getNewCallId();
        }

        SipProviderImpl sipProvider;
        if (StrUtil.isBlank(ip)) {
            sipProvider = transport.equalsIgnoreCase("TCP") ? sipRunner.getTcpSipProvider()
                    : sipRunner.getUdpSipProvider();
        } else {
            sipProvider = transport.equalsIgnoreCase("TCP") ? sipRunner.getTcpSipProvider(ip)
                    : sipRunner.getUdpSipProvider(ip);
        }

        if (Objects.isNull(sipProvider)) {
            sipProvider = sipRunner.getUdpSipProvider();
        }
        return sipProvider.getNewCallId();
    }
}
