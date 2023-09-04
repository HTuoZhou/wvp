package com.htuozhou.wvp.business.zlm.param;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/2
 */
@Data
public class OnPublishHookParam extends ZLMHttpHookParam {

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
     * 推流器ip
     */
    private String ip;

    /**
     * 推流url参数
     */
    private String params;

    /**
     * 推流器端口号
     */
    private int port;

    /**
     * 推流的协议，可能是rtsp、rtmp
     */
    private String schema;

    /**
     * 流虚拟主机
     */
    private String vhost;

}
