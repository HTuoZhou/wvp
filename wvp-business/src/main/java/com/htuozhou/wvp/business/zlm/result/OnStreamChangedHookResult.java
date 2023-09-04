package com.htuozhou.wvp.business.zlm.result;

/**
 * @author hanzai
 * @date 2023/9/4
 */
public class OnStreamChangedHookResult extends ZLMHttpHookResult {

    public OnStreamChangedHookResult() {
    }

    public OnStreamChangedHookResult(Integer code, String msg) {
        setCode(code);
        setMsg(msg);
    }

    public static OnStreamChangedHookResult success() {
        return new OnStreamChangedHookResult(0, "success");
    }

    public static OnStreamChangedHookResult fail() {
        return new OnStreamChangedHookResult(-1, "fail");
    }

}
