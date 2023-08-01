package com.htuozhou.wvp.business.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Component
@ConfigurationProperties(prefix = "zlm")
@Data
public class ZLMProperties {

    private String mediaServerId;
    private String secret;
    private String ip;
    private String streamIp;
    private String sdpIp;
    private String hookIp;
    private Integer httpPort;
    private Integer httpSslPort;
    private Integer rtspPort;
    private Integer rtspSslPort;
    private Integer rtmpPort;
    private Integer rtmpSslPort;
    private Boolean rtpEnable;
    private String rtpPortRange;
    private Integer rtpProxyPort;
    private Integer hookAliveInterval;

}
