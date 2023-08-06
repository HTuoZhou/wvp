package com.htuozhou.wvp.business.easyexcel;

import com.htuozhou.wvp.common.dict.BaseDict;

/**
 * @author hanzai
 * @date 2023/2/14
 */
public enum ImportHeadDict implements BaseDict {

    // 用户信息导入头部
    USER_NAME_NULL("user.name.null", "第 {0} 行姓名不能为空"),
    USER_NICKNAME_NULL("user.nickname.null", "第 {0} 行昵称不能为空"),
    USER_SEX_NULL("user.sex.null", "第 {0} 行性别不能为空"),
    USER_AGE_NULL("user.age.null", "第 {0} 行年龄不能为空"),
    USER_NAME_REPEAT("user.name.repeat", "姓名 {0} 重复"),

    USER_NAME_EXIST("user.name.exist", "第 {0} 行姓名存在");

    private String code;
    private String value;

    ImportHeadDict(String code, String value) {
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
