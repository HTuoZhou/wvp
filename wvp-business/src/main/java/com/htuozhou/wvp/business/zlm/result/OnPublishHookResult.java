package com.htuozhou.wvp.business.zlm.result;

/**
 * @author hanzai
 * @date 2023/9/4
 */
public class OnPublishHookResult extends ZLMHttpHookResult {

    public OnPublishHookResult() {
    }

    public OnPublishHookResult(Integer code, String msg) {
        setCode(code);
        setMsg(msg);
    }

    public static OnPublishHookResult success() {
        return new OnPublishHookResult(0, "success");
    }

    public static OnPublishHookResult fail() {
        return new OnPublishHookResult(-1, "fail");
    }

    @Override
    public String toString() {
        return "OnPublishHookResult{" +
                "code=" + getCode() +
                ", msg='" + getMsg() + '\'' +
                '}';
    }
}
