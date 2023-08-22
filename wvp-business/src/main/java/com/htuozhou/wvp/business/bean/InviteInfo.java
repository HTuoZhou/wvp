package com.htuozhou.wvp.business.bean;

import com.htuozhou.wvp.business.dict.InviteSessionStatusDict;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteInfo {

    private String deviceId;

    private String channelId;

    private String streamId;

    private SSRCInfo ssrcInfo;

    private String receiveIp;

    private Integer receivePort;

    private String streamMode;

    private Boolean primaryStream;

    private InviteSessionTypeDict inviteSessionTypeDict;

    private InviteSessionStatusDict inviteSessionStatusDict;

    private StreamContent streamContent;

}
