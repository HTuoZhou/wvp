package com.htuozhou.wvp.common.exception;

import com.htuozhou.wvp.common.result.ResultCodeEnum;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public class BusinessException extends RuntimeException {

    private Integer code;

    private String msg;

    private Object data;

    public BusinessException() {
        this.code = ResultCodeEnum.FAIL.getCode();
        this.msg = ResultCodeEnum.FAIL.getMsg();
    }

    public BusinessException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BusinessException(Object data) {
        this.code = ResultCodeEnum.FAIL.getCode();
        this.msg = ResultCodeEnum.FAIL.getMsg();
        this.data = data;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, Object data) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
