package com.htuozhou.wvp.business.dict;

import com.htuozhou.wvp.common.dict.BaseDict;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/8/14
 */
public enum InviteSessionStatusDict implements BaseDict {
    READY(0, "invite.session.status.ready", "已就绪"),
    OK(1, "invite.session.status.ok", "已完成");

    private final Integer type;
    private final String code;
    private final String value;

    InviteSessionStatusDict(Integer type, String code, String value) {
        this.type = type;
        this.code = code;
        this.value = value;
    }

    public static String getDefaultValue(Integer type) {
        for (InviteSessionStatusDict dict : InviteSessionStatusDict.values()) {
            if (Objects.equals(type, dict.getType())) {
                return dict.getDefaultValue();
            }
        }
        return InviteSessionStatusDict.READY.getDefaultValue();
    }

    public Integer getType() {
        return type;
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
