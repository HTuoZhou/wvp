package com.htuozhou.wvp.common.result;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public class ApiFinalResult<T> {

    private Integer code;

    private String msg;

    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiFinalResult() {
    }

    public ApiFinalResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiFinalResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ApiFinalResult(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    public ApiFinalResult(ResultCodeEnum resultCodeEnum, T data) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
        this.data = data;
    }

    public static <T> ApiFinalResult<T> success(Integer code, String msg) {
        return new ApiFinalResult<>(code, msg);
    }

    public static <T> ApiFinalResult<T> success(Integer code, String msg, T data) {
        return new ApiFinalResult<>(code, msg, data);
    }

    public static <T> ApiFinalResult<T> success() {
        return new ApiFinalResult<>(ResultCodeEnum.SUCCESS);
    }

    public static <T> ApiFinalResult<T> success(T data) {
        return new ApiFinalResult<>(ResultCodeEnum.SUCCESS, data);
    }

    public static <T> ApiFinalResult<T> error(Integer code, String msg) {
        return new ApiFinalResult<>(code, msg);
    }

    public static <T> ApiFinalResult<T> error(Integer code, String msg, T data) {
        return new ApiFinalResult<>(code, msg, data);
    }

    public static <T> ApiFinalResult<T> error(ResultCodeEnum resultCodeEnum) {
        return new ApiFinalResult<>(resultCodeEnum);
    }

    public static <T> ApiFinalResult<T> error(ResultCodeEnum resultCodeEnum, T data) {
        return new ApiFinalResult<>(resultCodeEnum, data);
    }

}
