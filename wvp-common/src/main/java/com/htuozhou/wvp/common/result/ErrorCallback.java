package com.htuozhou.wvp.common.result;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public interface ErrorCallback<T> {

    void run(Integer code, String msg, T data);

}
