package com.htuozhou.wvp.business.zlm;

import com.alibaba.fastjson.JSONObject;

/**
 * @author hanzai
 * @date 2023/9/5
 */
public class ZlmHttpHookSubscribeFactory {

    public static OnStreamChangedHookSubscribe onStreamChanged(String app, String streamId, boolean regist, String scheam, String mediaServerId) {
        OnStreamChangedHookSubscribe hookSubscribe = new OnStreamChangedHookSubscribe();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app", app);
        jsonObject.put("stream", streamId);
        jsonObject.put("regist", regist);
        jsonObject.put("schema", scheam);
        jsonObject.put("mediaServerId", mediaServerId);
        hookSubscribe.setContent(jsonObject);

        return hookSubscribe;
    }

}
