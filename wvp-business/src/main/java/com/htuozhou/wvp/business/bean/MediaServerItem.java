package com.htuozhou.wvp.business.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/1
 */
@Data
public class MediaServerItem {

    @JSONField(name = "general.mediaServerId")
    private String mediaServerId;

    @JSONField(name = "api.secret")
    private String secret;

    @JSONField(name = "http.sslport")
    private Integer httpSslPort;

    @JSONField(name = "rtsp.port")
    private Integer rtspPort;

    @JSONField(name = "rtsp.sslport")
    private Integer rtspSslPort;

    @JSONField(name = "rtmp.port")
    private Integer rtmpPort;

    @JSONField(name = "rtmp.sslport")
    private Integer rtmpSslPort;

    @JSONField(name = "rtp_proxy.port_range")
    private String rtpPortRange;

    @JSONField(name = "rtp_proxy.port")
    private Integer rtpProxyPort;

    @JSONField(name = "hook.alive_interval")
    private Integer hookAliveInterval;

}
