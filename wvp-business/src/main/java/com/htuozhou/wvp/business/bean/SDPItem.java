package com.htuozhou.wvp.business.bean;

import javax.sdp.SessionDescription;

/**
 * @author hanzai
 * @date 2023/9/2
 */
public class SDPItem {

    private SessionDescription baseSdb;
    private String ssrc;

    private String mediaDescription;

    public static SDPItem getInstance(SessionDescription baseSdb, String ssrc, String mediaDescription) {
        SDPItem sdpItem = new SDPItem();
        sdpItem.setBaseSdb(baseSdb);
        sdpItem.setSsrc(ssrc);
        sdpItem.setMediaDescription(mediaDescription);
        return sdpItem;
    }


    public SessionDescription getBaseSdb() {
        return baseSdb;
    }

    public void setBaseSdb(SessionDescription baseSdb) {
        this.baseSdb = baseSdb;
    }

    public String getSsrc() {
        return ssrc;
    }

    public void setSsrc(String ssrc) {
        this.ssrc = ssrc;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

}
