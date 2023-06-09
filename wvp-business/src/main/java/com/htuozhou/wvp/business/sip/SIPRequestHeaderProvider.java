package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author hanzai
 * @date 2023/4/21
 */
@Component
@Slf4j
public class SIPRequestHeaderProvider {

    @Autowired
    private SIPRunner sipRunner;

    @Autowired
    private SIPProperties sipProperties;

    @Autowired
    private Environment environment;

    public Request createMessageRequest(DeviceBO deviceBO, String content, String branch, String fromTag, String toTag, CallIdHeader callIdHeader) throws Exception {
        Request request = null;
        // sipuri
        SipURI requestURI = sipRunner.getSipFactory().createAddressFactory().createSipURI(deviceBO.getDeviceId(), deviceBO.getAddress());
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipRunner.getSipFactory().createHeaderFactory().createViaHeader(sipProperties.getIp(), sipProperties.getPort(), deviceBO.getTransport(), branch);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = sipRunner.getSipFactory().createAddressFactory().createSipURI(sipProperties.getId(), sipProperties.getDomain());
        Address fromAddress = sipRunner.getSipFactory().createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipRunner.getSipFactory().createHeaderFactory().createFromHeader(fromAddress, fromTag);
        // to
        SipURI toSipURI = sipRunner.getSipFactory().createAddressFactory().createSipURI(deviceBO.getDeviceId(), deviceBO.getAddress());
        Address toAddress = sipRunner.getSipFactory().createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipRunner.getSipFactory().createHeaderFactory().createToHeader(toAddress, toTag);

        // Forwards
        MaxForwardsHeader maxForwards = sipRunner.getSipFactory().createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = sipRunner.getSipFactory().createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);

        request = sipRunner.getSipFactory().createMessageFactory().createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);

        request.addHeader(sipRunner.getSipFactory().createHeaderFactory().createUserAgentHeader(Collections.singletonList(environment.getProperty(CommonConstant.SPRING_APPLICATION_NAME))));

        ContentTypeHeader contentTypeHeader = sipRunner.getSipFactory().createHeaderFactory().createContentTypeHeader("Application", "MANSCDP+xml");
        request.setContent(content, contentTypeHeader);
        return request;
    }


}
