package com.htuozhou.wvp.business.sip.request.impl.message.impl.notify;

import com.htuozhou.wvp.business.sip.request.impl.MessageRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.AbstractMessageHandler;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/4/15
 */
@Component
@Slf4j
public class NotifyMessageHandler extends AbstractMessageHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private MessageRequestProcessor messageRequestProcessor;

    private static final String rootElement = "Notify";

    @Override
    public void afterPropertiesSet() throws Exception {
        messageRequestProcessor.addMessageHandler(rootElement, this);
    }
}
