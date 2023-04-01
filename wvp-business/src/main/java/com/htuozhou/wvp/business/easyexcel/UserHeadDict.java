package com.htuozhou.wvp.business.easyexcel;

import com.htuozhou.wvp.common.dict.BaseDict;

/**
 * @author hanzai
 * @date 2023/2/3
 * 用户信息导入模板各表头名
 */
public enum UserHeadDict implements BaseDict {

    USER_NAME("user.name", "姓名*"),
    USER_NICKNAME("user.nickname", "昵称*"),
    USER_SEX("user.sex", "性别*"),
    USER_AGE("user.age", "年龄*");

    UserHeadDict(String code, String value) {
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