package com.htuozhou.wvp.business.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSRCInfo {
    private Integer port;
    private String ssrc;
    private String StreamId;
}
