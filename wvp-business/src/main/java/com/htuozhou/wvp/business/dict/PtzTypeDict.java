package com.htuozhou.wvp.business.dict;

import com.htuozhou.wvp.common.dict.BaseDict;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/8/7
 */
public enum PtzTypeDict implements BaseDict {

    UN_KNOW(0, "ptz.type.unknow", "未知"),
    BALL(1, "ptz.type.ball", "球机"),
    HEMISPHERE(2, "ptz.type.hemisphere", "半球"),
    FIXED_GUN(3, "ptz.type.fixed.gun", "固定枪击"),
    REMOTE_CONTROL_GUN(4, "ptz.type.remote.control.gun", "遥控枪击");

    private final Integer type;
    private final String code;
    private final String value;

    PtzTypeDict(Integer type, String code, String value) {
        this.type = type;
        this.code = code;
        this.value = value;
    }

    public static String getDefaultValue(Integer type) {
        for (PtzTypeDict dict : PtzTypeDict.values()) {
            if (Objects.equals(type, dict.getType())) {
                return dict.getDefaultValue();
            }
        }
        return PtzTypeDict.UN_KNOW.getDefaultValue();
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
