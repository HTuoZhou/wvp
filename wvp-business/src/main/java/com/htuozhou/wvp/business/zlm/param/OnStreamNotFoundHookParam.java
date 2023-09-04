package com.htuozhou.wvp.business.zlm.param;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/3
 */
@Data
public class OnStreamNotFoundHookParam extends ZLMHttpHookParam {

    /**
     * TCP链接唯一ID
     */
    private String id;

    /**
     * 流应用名
     */
    private String app;

    /**
     * 流ID
     */
    private String stream;

    /**
     * 播放器ip
     */
    private String ip;

    /**
     * 播放url参数
     */
    private String params;

    /**
     * 播放器端口号
     */
    private Integer port;

    /**
     * 播放的协议，可能是rtsp、rtmp、http
     */
    private String schema;

    /**
     * 流虚拟主机
     */
    private String vhost;

}
