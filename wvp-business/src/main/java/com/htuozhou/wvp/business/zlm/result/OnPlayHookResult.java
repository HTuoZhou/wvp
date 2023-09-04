package com.htuozhou.wvp.business.zlm.result;

/**
 * @author hanzai
 * @date 2023/9/4
 */
public class OnPlayHookResult extends ZLMHttpHookResult {

    public OnPlayHookResult() {
    }

    public OnPlayHookResult(Integer code, String msg) {
        setCode(code);
        setMsg(msg);
    }

    public static OnPlayHookResult success() {
        return new OnPlayHookResult(0, "success");
    }

    public static OnPlayHookResult fail() {
        return new OnPlayHookResult(-1, "fail");
    }

}
