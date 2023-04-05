package com.htuozhou.wvp.business.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/4/5
 */
@Component
@ConfigurationProperties(prefix = "sip")
@Getter
@Setter
public class SIPProperties {

    private String ip;
    private Integer port;
    private String domain;
    private String id;
    private String password;
    private Integer registerTimeInterval;
    private Integer ptzSpeed;
    private Boolean alarm;

}
