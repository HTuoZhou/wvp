package com.htuozhou.wvp.business.sip.request.impl;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.constant.SIPConstant;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.*;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.ISIPRequestProcessor;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hanzai
 * @date 2023/4/5
 */
@Component
@Slf4j
public class RegisterRequestProcessor extends AbstractSIPRequestProcessor implements InitializingBean, ISIPRequestProcessor {

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SIPProperties sipProperties;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private ISIPService sipService;

    @Override
    public void afterPropertiesSet() throws Exception {
        sipProcessorObserver.addRequestProcessor(Request.REGISTER,this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void process(RequestEvent requestEvent) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();

        int expires = request.getExpires().getExpires();
        String type = (expires != 0) ? "注册" : "注销";

        log.info("[SIP REQUEST] 收到 [SIP ADDRESS:{} REGISTER] {}请求",requestAddress,type);
        log.info("[SIP REQUEST] 收到 [SIP ADDRESS:{} REGISTER] {}请求，请求内容\n{}",requestAddress,type,request);

        // 请求未认证
        AuthorizationHeader authorizationHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        if (Objects.isNull(authorizationHeader)) {
            Response response = getMessageFactory().createResponse(Response.UNAUTHORIZED, request);
            new DigestServerAuthenticationHelper().generateChallenge(getHeaderFactory(), response, sipProperties.getDomain());
            sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
            log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER] {}请求未认证",requestAddress,type);
            log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER}] {}请求未认证，需要回复401，回复内容\n{}",requestAddress,type,response);

            return;
        }

        // 请求密码不正确
        if (!new DigestServerAuthenticationHelper().doAuthenticatePlainTextPassword(request,sipProperties.getPassword())) {
            Response response = getMessageFactory().createResponse(Response.FORBIDDEN, request);
            sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
            log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER] {}请求密码不正确",requestAddress,type);
            log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER}] {}请求密码不正确，需要回复403，回复内容\n{}",requestAddress,type,response);

            return;
        }

        // 请求已认证且密码正确
        Response response = getMessageFactory().createResponse(Response.OK, request);
        SIPDateHeader sipDateHeader = new SIPDateHeader();
        WvpSIPDate wvpSipDate = new WvpSIPDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        sipDateHeader.setDate(wvpSipDate);
        response.addHeader(sipDateHeader);
        response.addHeader(request.getHeader(ContactHeader.NAME));
        response.addHeader(request.getExpires());
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER}] {}请求已认证且密码正确",requestAddress,type);
        log.info("[SIP REQUEST] [SIP ADDRESS:{} REGISTER}] {}请求已认证且密码正确，需要回复200，回复内容\n{}",requestAddress,type,response);

        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        AddressImpl address = (AddressImpl) fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        String deviceId = uri.getUser();
        DeviceBO deviceBO = Optional.ofNullable(sipService.getDevice(deviceId)).orElse(new DeviceBO());
        if (expires == 0) {
            deviceBO.setStatus(0);
        } else {
            ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
            String transport = reqViaHeader.getTransport();
            deviceBO.setStatus(1);
            deviceBO.setDeviceId(deviceId);
            deviceBO.setIp(request.getRemoteAddress().getHostAddress());
            deviceBO.setPort(request.getRemotePort());
            deviceBO.setAddress(String.join(":",request.getRemoteAddress().getHostAddress(), String.valueOf(request.getRemotePort())));
            deviceBO.setTransport(transport.equalsIgnoreCase(SIPConstant.TRANSPORT_UDP) ? SIPConstant.TRANSPORT_UDP : SIPConstant.TRANSPORT_TCP);
            deviceBO.setStreamMode(SIPConstant.STREAM_MODE_TCP_PASSIVE);
            deviceBO.setPassword(sipProperties.getPassword());
            deviceBO.setCharset(SIPConstant.CHARSET_GB2312);
            deviceBO.setRegisterTime(LocalDateTime.now());
            deviceBO.setExpires(expires);
            deviceBO.setKeepAliveInterval(SIPConstant.KEEP_ALIVE_INTERVAL);

            // 查询设备信息
            sipCommander.deviceInfoQuery(deviceBO);
        }

        sipService.saveDevice(deviceBO);
    }
}
