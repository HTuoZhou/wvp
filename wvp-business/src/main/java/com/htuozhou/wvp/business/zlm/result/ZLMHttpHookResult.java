package com.htuozhou.wvp.business.zlm.result;

/**
 * @author hanzai
 * @date 2023/9/4
 */
public class ZLMHttpHookResult {

    private Integer code;
    private String msg;


    public ZLMHttpHookResult() {
    }

    public ZLMHttpHookResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ZLMHttpHookResult success() {
        return new ZLMHttpHookResult(0, "success");
    }

    public static ZLMHttpHookResult fail() {
        return new ZLMHttpHookResult(-1, "fail");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ZLMHttpHookResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
