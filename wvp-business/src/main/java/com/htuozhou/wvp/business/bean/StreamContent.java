package com.htuozhou.wvp.business.bean;

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
     * HTTP-FLV流地址
     */
    private String flv;

    /**
     * HTTPS-FLV流地址
     */
    private String https_flv;

    /**
     * Websocket-FLV流地址
     */
    private String ws_flv;

    /**
     * Websockets-FLV流地址
     */
    private String wss_flv;

    /**
     * HTTP-FMP4流地址
     */
    private String fmp4;

    /**
     * HTTPS-FMP4流地址
     */
    private String https_fmp4;

    /**
     * Websocket-FMP4流地址
     */
    private String ws_fmp4;

    /**
     * Websockets-FMP4流地址
     */
    private String wss_fmp4;

    /**
     * HLS流地址
     */
    private String hls;

    /**
     * HTTPS-HLS流地址
     */
    private String https_hls;

    /**
     * Websocket-HLS流地址
     */
    private String ws_hls;

    /**
     * Websockets-HLS流地址
     */
    private String wss_hls;

    /**
     * HTTP-TS流地址
     */
    private String ts;

    /**
     * HTTPS-TS流地址
     */
    private String https_ts;

    /**
     * Websocket-TS流地址
     */
    private String ws_ts;

    /**
     * Websockets-TS流地址
     */
    private String wss_ts;

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

    private double progress;

}
