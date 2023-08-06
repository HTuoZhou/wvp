package com.htuozhou.wvp.business.easyexcel;

import com.htuozhou.wvp.common.dict.BaseDict;

/**
 * @author hanzai
 * @date 2023/2/3
 * easyexcel文件名、sheet名
 */
public enum TemplateDict implements BaseDict {

    TEMPLATE_DECRIPTION("template.description", "说明：*为必填项"),

    // 用户信息导入导出模板
    USER_IMPORT_TEMPLATE_NAME("user.import.template", "用户信息导入模板"),
    USER_EXPORT_TEMPLATE_NAME("user.export.template", "用户信息导出模板"),
    USER_DETAIL("user.detail", "用户信息");

    private final String code;
    private final String value;

    TemplateDict(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultValue() {
        return value;
    }
}