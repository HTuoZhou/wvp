package com.htuozhou.wvp.business.sip.request.impl.message;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.util.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;

import javax.sip.RequestEvent;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/4/15
 */@Slf4j

public abstract class AbstractMessageHandler extends AbstractSIPRequestProcessor implements IMessageHandler {

    public Map<String, IMessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    public void addMessageHandler(String cmdType, IMessageHandler messageHandler) {
        messageHandlerMap.put(cmdType, messageHandler);
    }

    @Override
    public void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element rootElement) {
        String cmdType = XmlUtils.getText(rootElement, "CmdType");
        IMessageHandler messageHandler = messageHandlerMap.get(cmdType);
        if (Objects.isNull(messageHandler)) {
            log.warn("[SIP MESSAGE {} :{}] 暂不支持", rootElement.getName().toUpperCase(),cmdType.toUpperCase());
            return;
        }

        messageHandlerMap.get(cmdType).handForDevice(requestEvent,deviceBO,rootElement);
    }


}
