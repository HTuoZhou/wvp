package com.htuozhou.wvp.business.properties;

import com.htuozhou.wvp.persistence.po.ZlmServerPO;
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

    private String uniqueId;
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

    public ZlmServerPO properties2po(ZlmServerPO po) {
        if (Objects.isNull(po)) {
            po = new ZlmServerPO();
        }

        BeanUtils.copyProperties(this,po);
        po.setRtpEnable(this.getRtpEnable() ? 1 : 0);
        po.setDefaultServer(1);

        return po;
    }

}
