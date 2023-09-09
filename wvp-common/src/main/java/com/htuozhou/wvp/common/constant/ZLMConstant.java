package com.htuozhou.wvp.common.constant;

/**
 * @author hanzai
 * @date 2023/4/1
 */
public class ZLMConstant {

    public static final String ADDRESS = "%s:%d";
    public static final String URL_FMT = "http://%s:%d/index/api/%s";
    public static final String HOOK_URL_FMT = "http://%s/zlm/index/hook";
    public static final String GET_SERVER_CONFIG = "getServerConfig";
    public static final String SET_SERVER_CONFIG = "setServerConfig";
    public static final String RESTART_SERVER = "restartServer";
    public static final String VHOST = "__defaultVhost__";
    public static final String GET_MEDIA_LIST = "getMediaList";
    public static final String GET_RTP_INFO = "getRtpInfo";
    public static final String CLOSE_RTP_SERVER = "closeRtpServer";
    public static final String OPEN_RTP_SERVER = "openRtpServer";
    public static final String GET_SNAP = "getSnap";

    public static final Integer STREAM_MAX_COUNT = 10;
    public static final String STREAM_LIVE_FLV_FMT = "%s:%d/%s/%s.live.flv";
    public static final String STREAM_LIVE_MP4_FMT = "%s:%d/%s/%s.live.mp4";
    public static final String STREAM_LIVE_HLS_FMT = "%s:%d/%s/%s.hls.m3u8";
    public static final String STREAM_LIVE_TS_FMT = "%s:%d/%s/%s.live.ts";
    public static final String STREAM_LIVE_RTMP_RTSP_FMT = "%s:%d/%s/%s";
    public static final String STREAM_LIVE_RTC_FMT = "%s:%d/index/api/webrtc?app=%s&stream=%s&type=play";

}
