package com.htuozhou.wvp.common.result;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public enum ResultCodeEnum {

    /**
     * 请求成功
     */
    SUCCESS(2000000, "请求成功"),

    /**
     * 请求失败
     */
    FAIL(2000001, "请求失败"),

    /**
     * 参数校验异常
     */
    PARAMETER(2000002, "参数校验异常"),

    /**
     * 导入模板不正确
     */
    IMPORT_TEMPLATE_ERROR(200003,"导入模板不正确"),

    /**
     * 导入数据不正确
     */
    IMPORT_DATA_ERROR(200004,"导入数据不正确"),

    ZLM_CONNECT_EXIST(200005,"当前连接已存在"),

    ZLM_CONNECT_ERROR(200006,"当前连接失败"),

    ZLM_ID_EXIST(200007,"流媒体服务ID已存在");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述信息
     */
    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}