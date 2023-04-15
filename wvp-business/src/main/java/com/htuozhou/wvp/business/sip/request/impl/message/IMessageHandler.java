package com.htuozhou.wvp.business.sip.request.impl.message;

import com.htuozhou.wvp.business.bo.DeviceBO;
import org.dom4j.Element;

import javax.sip.RequestEvent;

/**
 * @author hanzai
 * @date 2023/4/15
 */
public interface IMessageHandler {

    void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element element);

}
