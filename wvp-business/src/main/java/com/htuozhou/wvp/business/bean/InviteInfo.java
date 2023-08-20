package com.htuozhou.wvp.business.bean;

import com.htuozhou.wvp.business.dict.InviteSessionStatusDict;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Data
public class InviteInfo {

    private String deviceId;

    private String channelId;

    private String stream;

    private SSRCInfo ssrcInfo;

    private String receiveIp;

    private Integer receivePort;

    private String streamMode;

    private InviteSessionTypeDict inviteSessionTypeDict;

    private InviteSessionStatusDict inviteSessionStatusDict;

    private StreamContent streamContent;

}
