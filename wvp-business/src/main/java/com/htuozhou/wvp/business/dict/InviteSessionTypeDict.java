package com.htuozhou.wvp.business.dict;

import com.htuozhou.wvp.common.dict.BaseDict;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public enum InviteSessionTypeDict implements BaseDict {
    PLAY(0, "invite.session.type.play", "点播"),
    PLAYBACK(1, "invite.session.type.playback", "回放"),
    DOWNLOAD(2, "invite.session.type.download", "下载");

    private final Integer type;
    private final String code;
    private final String value;

    InviteSessionTypeDict(Integer type, String code, String value) {
        this.type = type;
        this.code = code;
        this.value = value;
    }

    public static String getDefaultValue(Integer type) {
        for (InviteSessionTypeDict dict : InviteSessionTypeDict.values()) {
            if (Objects.equals(type, dict.getType())) {
                return dict.getDefaultValue();
            }
        }
        return InviteSessionTypeDict.PLAY.getDefaultValue();
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