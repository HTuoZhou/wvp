package com.htuozhou.wvp.business.properties;

import com.htuozhou.wvp.business.bo.ZlmServerBO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Component
@ConfigurationProperties(prefix = "zlm")
@Getter
@Setter
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

    public ZlmServerBO properties2bo(ZlmServerBO bo) {
        if (Objects.isNull(bo)) {
            bo = new ZlmServerBO();
        }

        BeanUtils.copyProperties(this,bo);
        bo.setRtpEnable(this.getRtpEnable() ? 1 : 0);
        bo.setDefaultServer(1);

        return bo;
    }

}
