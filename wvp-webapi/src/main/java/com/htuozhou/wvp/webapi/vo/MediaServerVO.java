package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.MediaServerBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Data
public class MediaServerVO {

    /**
     * 主键,自增
     */
    private Integer id;

    /**
     * zlm流媒体服务器id
     */
    private String mediaServerId;

    /**
     * zlm流媒体服务器secret
     */
    private String secret;

    /**
     * zlm流媒体服务器ip
     */
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

    private Integer rtpEnable;

    private String rtpPortRange;

    private Integer rtpProxyPort;

    /**
     * 心跳时间
     */
    private LocalDateTime hookAliveTime;

    /**
     * zlm hook 心跳间隔时间（秒）
     */
    private Integer hookAliveInterval;

    /**
     * 是否在线（0、离线 1、在线）
     */
    private Integer status;

    /**
     * 是否默认服务器（0、否 1、是）
     */
    private Integer defaultServer;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标识（0、逻辑删除 1、物理删除）
     */
    private Integer deleted;

    /**
     * 版本号
     */
    private Integer version;

    public static MediaServerVO bo2vo(MediaServerBO bo){
        MediaServerVO vo = new MediaServerVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
