package com.htuozhou.wvp.common.service;

/**
 * @author hanzai
 * @date 2023/2/3
 */
public interface I18nService {

    /**
     * 通过Code 获取对应的信息
     * @param i18nKey
     * @param defaultValue
     * @return
     */
    String getMessage(String i18nKey, String defaultValue);

    /**
     * 通过Code 及参数 获取对应格式化信息
     * @param i18nKey
     * @param params
     * @param defaultValue
     * @return
     */
    String getMessage(String i18nKey, Object[] params, String defaultValue);

}
