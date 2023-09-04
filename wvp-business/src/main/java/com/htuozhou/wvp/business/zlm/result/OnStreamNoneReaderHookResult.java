package com.htuozhou.wvp.business.zlm.result;

/**
 * @author hanzai
 * @date 2023/9/4
 */
public class OnStreamNoneReaderHookResult extends ZLMHttpHookResult {

    public OnStreamNoneReaderHookResult() {
    }

    public OnStreamNoneReaderHookResult(Integer code, String msg) {
        setCode(code);
        setMsg(msg);
    }

    public static OnStreamNoneReaderHookResult success() {
        return new OnStreamNoneReaderHookResult(0, "success");
    }

    public static OnStreamNoneReaderHookResult fail() {
        return new OnStreamNoneReaderHookResult(-1, "fail");
    }

}
