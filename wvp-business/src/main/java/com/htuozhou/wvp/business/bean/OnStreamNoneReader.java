package com.htuozhou.wvp.business.bean;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/3
 */
@Data
public class OnStreamNoneReader extends ZLMHttpHookParam {

    /**
     * 流应用名
     */
    private String app;

    /**
     * 流ID
     */
    private String stream;

    /**
     * 播放的协议，可能是rtsp、rtmp、http
     */
    private String schema;

    /**
     * 流虚拟主机
     */
    private String vhost;

}
