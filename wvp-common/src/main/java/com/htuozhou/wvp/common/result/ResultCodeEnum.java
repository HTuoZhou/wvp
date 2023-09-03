package com.htuozhou.wvp.common.result;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public enum ResultCodeEnum {

    SUCCESS(2000000, "请求成功"),

    FAIL(2000001, "请求失败"),

    PARAMETER(2000002, "参数校验异常"),

    IMPORT_TEMPLATE_ERROR(200003, "导入模板不正确"),

    IMPORT_DATA_ERROR(200004, "导入数据不正确"),

    ZLM_CONNECT_EXIST(200005, "当前连接已存在"),

    ZLM_CONNECT_ERROR(200006, "当前连接失败"),

    ZLM_ID_EXIST(200007, "流媒体服务ID已存在"),
    ZLM_UN_USABLE(200008, "没有可用的ZLM"),
    TCP_ACTIVE_NOT_SUPPORT(200009, "单端口收流时不支持TCP主动方式收流"),
    GB_DEVICE_PLAY_TIMEOUT(200010, "国标设备点播超时"),
    STREAM_ID_NOT_EXIST(200011, "流ID不存在"),
    PORT_ASSIGN_ERROR(200012, "端口分配失败"),
    RECEIVE_STREAM_TIMEOUT(200013, "收流超时"),
    SSRC_UN_USABLE(200014, "没有可用的SSRC"),
    SIP_COMMAND_SEND_ERROR(200015, "SIP命令发送失败"),
    ;

    private final Integer code;

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