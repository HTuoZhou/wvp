package com.htuozhou.wvp.business.zlm.param;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/9/3
 */
@Data
public class OnFlowReportHookParam extends ZLMHttpHookParam {

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
     * 客户端ip
     */
    private String ip;

    /**
     * 推流或播放url参数
     */
    private String params;

    /**
     * 客户端端口号
     */
    private Integer port;

    /**
     * 播放或推流的协议，可能是rtsp、rtmp、http
     */
    private String schema;

    /**
     * 流虚拟主机
     */
    private String vhost;

    /**
     * tcp链接维持时间，单位秒
     */
    private Integer duration;

    /**
     * true为播放器，false为推流器
     */
    private Boolean player;

    /**
     * 耗费上下行流量总和，单位字节
     */
    private Integer totalBytes;

}
