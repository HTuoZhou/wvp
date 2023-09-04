package com.htuozhou.wvp.business.bean;

import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.zlm.param.OnStreamChangedHookParam;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Data
public class StreamContent {

    /**
     * 应用名
     */
    private String app;

    /**
     * 流ID
     */
    private String streamId;

    /**
     * IP
     */
    private String ip;

    /**
     * 流媒体服务ID
     */
    private String mediaServerId;

    /**
     * 流编码信息
     */
    private Object tracks;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    private Double progress;

    /**
     * HTTP-FLV流地址
     */
    private String flv;

    /**
     * HTTPS-FLV流地址
     */
    private String httpsFlv;

    /**
     * Websocket-FLV流地址
     */
    private String wsFlv;

    /**
     * Websockets-FLV流地址
     */
    private String wssFlv;

    /**
     * HTTP-FMP4流地址
     */
    private String fmp4;

    /**
     * HTTPS-FMP4流地址
     */
    private String httpsFmp4;

    /**
     * Websocket-FMP4流地址
     */
    private String wsFmp4;

    /**
     * Websockets-FMP4流地址
     */
    private String wssFmp4;

    /**
     * HLS流地址
     */
    private String hls;

    /**
     * HTTPS-HLS流地址
     */
    private String httpsHls;

    /**
     * Websocket-HLS流地址
     */
    private String wsHls;

    /**
     * Websockets-HLS流地址
     */
    private String wssHls;

    /**
     * HTTP-TS流地址
     */
    private String ts;

    /**
     * HTTPS-TS流地址
     */
    private String httpsTs;

    /**
     * Websocket-TS流地址
     */
    private String wsTs;

    /**
     * Websockets-TS流地址
     */
    private String wssTs;

    /**
     * RTMP流地址
     */
    private String rtmp;

    /**
     * RTMPS流地址
     */
    private String rtmps;

    /**
     * RTSP流地址
     */
    private String rtsp;

    /**
     * RTSPS流地址
     */
    private String rtsps;

    /**
     * RTC流地址
     */
    private String rtc;

    /**
     * RTCS流地址
     */
    private String rtcs;

    public static StreamContent convert(MediaServerBO mediaServerBO, OnStreamChangedHookParam param) {
        StreamContent streamContent = new StreamContent();
        streamContent.setApp(param.getApp());
        streamContent.setStreamId(param.getStream());
        streamContent.setMediaServerId(mediaServerBO.getMediaServerId());
        streamContent.setIp(mediaServerBO.getStreamIp());
        streamContent.setTracks(param.getTracks());

        streamContent.setFlv(CommonConstant.HTTP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_FLV_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setHttpsFlv(CommonConstant.HTTPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_FLV_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));
        streamContent.setWsFlv(CommonConstant.WS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_FLV_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setWssFlv(CommonConstant.WSSS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_FLV_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));

        streamContent.setFmp4(CommonConstant.HTTP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_MP4_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setHttpsFmp4(CommonConstant.HTTPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_MP4_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));
        streamContent.setWsFmp4(CommonConstant.WS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_MP4_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setWssFmp4(CommonConstant.WSSS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_MP4_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));

        streamContent.setHls(CommonConstant.HTTP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_HLS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setHttpsHls(CommonConstant.HTTPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_HLS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));
        streamContent.setWsHls(CommonConstant.WS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_HLS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setWssHls(CommonConstant.WSSS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_HLS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));

        streamContent.setTs(CommonConstant.HTTP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_TS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setHttpsTs(CommonConstant.HTTPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_TS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));
        streamContent.setWsTs(CommonConstant.WS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_TS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setWssTs(CommonConstant.WSSS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_TS_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));

        streamContent.setRtmp(CommonConstant.RTMP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTMP_RTSP_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getRtmpPort(), param.getApp(), param.getStream()));
        streamContent.setRtmps(CommonConstant.RTMPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTMP_RTSP_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getRtmpSslPort(), param.getApp(), param.getStream()));

        streamContent.setRtsp(CommonConstant.RTSP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTMP_RTSP_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getRtspPort(), param.getApp(), param.getStream()));
        streamContent.setRtsps(CommonConstant.RTSPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTMP_RTSP_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getRtspSslPort(), param.getApp(), param.getStream()));

        streamContent.setRtc(CommonConstant.HTTP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTC_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpPort(), param.getApp(), param.getStream()));
        streamContent.setRtcs(CommonConstant.HTTPS_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTC_FMT, mediaServerBO.getStreamIp(), mediaServerBO.getHttpSslPort(), param.getApp(), param.getStream()));

        return streamContent;
    }
}
