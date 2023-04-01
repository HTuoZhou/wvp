package com.htuozhou.wvp.business.easyexcel;

import com.htuozhou.wvp.common.dict.BaseDict;

/**
 * @author hanzai
 * @date 2023/2/8
 * 用户性别
 */
public enum UserSexDict implements BaseDict {

    USER_SEX_MALE("user.sex.male","男"),

    USER_SEX_FEMALE("user.sex.female","女");

    UserSexDict(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private String code;
    private String value;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultValue() {
        return value;
    }
}
