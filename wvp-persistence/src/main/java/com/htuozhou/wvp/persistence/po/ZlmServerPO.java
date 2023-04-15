package com.htuozhou.wvp.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * zlm服务表
 * </p>
 *
 * @author HTuoZhou
 * @since 2023-04-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("zlm_server")
public class ZlmServerPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * zlm服务器唯一id
     */
    @TableField("unique_id")
    private String uniqueId;

    /**
     * zlm服务器secret
     */
    @TableField("secret")
    private String secret;

    /**
     * zlm服务器ip
     */
    @TableField("ip")
    private String ip;

    /**
     * 返回流地址ip
     */
    @TableField("stream_ip")
    private String streamIp;

    @TableField("sdp_ip")
    private String sdpIp;

    @TableField("hook_ip")
    private String hookIp;

    @TableField("http_port")
    private Integer httpPort;

    @TableField("http_ssl_port")
    private Integer httpSslPort;

    @TableField("rtsp_port")
    private Integer rtspPort;

    @TableField("rtsp_ssl_port")
    private Integer rtspSslPort;

    @TableField("rtmp_port")
    private Integer rtmpPort;

    @TableField("rtmp_ssl_port")
    private Integer rtmpSslPort;

    @TableField("rtp_enable")
    private Integer rtpEnable;

    @TableField("rtp_port_range")
    private String rtpPortRange;

    @TableField("rtp_proxy_port")
    private Integer rtpProxyPort;

    /**
     * 心跳时间
     */
    @TableField("hook_alive_time")
    private LocalDateTime hookAliveTime;

    /**
     * zlm hook 心跳间隔时间（秒）
     */
    @TableField("hook_alive_interval")
    private Integer hookAliveInterval;

    /**
     * 是否在线（0、离线 1、在线）
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否默认服务器（0、否 1、是）
     */
    @TableField("default_server")
    private Integer defaultServer;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识（0、逻辑删除 1、物理删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 版本号
     */
    @TableField("version")
    @Version
    private Integer version;


}
