package com.htuozhou.wvp.common.service.impl;

import com.htuozhou.wvp.common.service.I18nService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

/**
 * @author hanzai
 * @date 2023/2/3
 */
@Service
@Slf4j
public class I18nServiceImpl implements I18nService {

    @Autowired
    private ResourceBundleMessageSource messageSource;

    /**
     * 通过Code 获取对应的信息
     * @param i18nKey
     * @param defaultValue
     * @return
     */
    @Override
    public String getMessage(String i18nKey, String defaultValue) {
        return this.getMessage(i18nKey, null, defaultValue);
    }

    /**
     * 通过Code 及参数 获取对应格式化信息
     * @param i18nKey
     * @param params
     * @param defaultValue
     * @return
     */
    @Override
    public String getMessage(String i18nKey, Object[] params, String defaultValue) {
        String message = messageSource.getMessage(i18nKey, params, LocaleContextHolder.getLocale());
        // String message = messageSource.getMessage(i18nKey, params, Locale.US);
        return StringUtils.isEmpty(message) ? defaultValue : message;
    }
}
