package com.htuozhou.wvp.business.zlm.param;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/3
 */
@Data
public class OnRtpServerTimeOutHookParam extends ZLMHttpHookParam {

    private Integer local_port;
    private String stream_id;
    private Integer tcp_mode;
    private Boolean re_use_port;
    private String ssrc;

}
