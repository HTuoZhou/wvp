package com.htuozhou.wvp.business.sip.response;

import javax.sip.ResponseEvent;

/**
 * @author hanzai
 * @date 2023/4/5
 */
public interface ISIPResponseProcessor {

    void process(ResponseEvent responseEvent);

}
