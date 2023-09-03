package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.bean.SDPItem;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;

/**
 * @author hanzai
 * @date 2023/9/2
 */
public class SIPUtil {

    public static SDPItem parseSDP(String sdpStr) throws SdpParseException {
        // jainSip不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        int mediaDescriptionIndex = sdpStr.indexOf("f=");
        // 检查是否有y字段
        SessionDescription sdp;
        String ssrc = null;
        String mediaDescription = null;
        if (mediaDescriptionIndex == 0 && ssrcIndex == 0) {
            sdp = SdpFactory.getInstance().createSessionDescription(sdpStr);
        } else {
            String lines[] = sdpStr.split("\\r?\\n");
            StringBuilder sdpBuffer = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("y=")) {
                    ssrc = line.substring(2);
                } else if (line.trim().startsWith("f=")) {
                    mediaDescription = line.substring(2);
                } else {
                    sdpBuffer.append(line.trim()).append("\r\n");
                }
            }
            sdp = SdpFactory.getInstance().createSessionDescription(sdpBuffer.toString());
        }
        return SDPItem.getInstance(sdp, ssrc, mediaDescription);
    }

}
