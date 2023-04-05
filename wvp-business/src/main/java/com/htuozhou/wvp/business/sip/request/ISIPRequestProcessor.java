package com.htuozhou.wvp.business.sip.request;

import javax.sip.RequestEvent;

/**
 * @author hanzai
 * @date 2023/4/5
 */
public interface ISIPRequestProcessor {

    void process(RequestEvent requestEvent);

}
