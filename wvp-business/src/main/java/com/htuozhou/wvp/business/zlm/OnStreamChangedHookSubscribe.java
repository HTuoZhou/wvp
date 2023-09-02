package com.htuozhou.wvp.business.zlm;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.enumerate.ZLMHttpHookType;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/9/2
 */
public class OnStreamChangedHookSubscribe implements IZLMHttpHookSubscribe{

    private ZLMHttpHookType hookType = ZLMHttpHookType.on_stream_changed;
    private JSONObject content;
    private LocalDateTime expires;

    @Override
    public ZLMHttpHookType getHookType() {
        return hookType;
    }

    @Override
    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    @Override
    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    @Override
    public LocalDateTime getExpires() {
        return expires;
    }
}
