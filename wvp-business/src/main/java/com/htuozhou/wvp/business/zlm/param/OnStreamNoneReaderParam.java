package com.htuozhou.wvp.business.zlm.param;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/3
 */
@Data
public class OnStreamNoneReaderParam extends ZLMHttpHookParam {

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
