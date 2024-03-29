package com.htuozhou.wvp.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备表
 * </p>
 *
 * @author HTuoZhou
 * @since 2023-04-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wvp_device")
public class DevicePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备id
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设备ip
     */
    @TableField("ip")
    private String ip;

    /**
     * 设备端口
     */
    @TableField("port")
    private Integer port;

    /**
     * 设备地址
     */
    @TableField("address")
    private String address;

    /**
     * 设备名称
     */
    @TableField("name")
    private String name;

    /**
     * 生产厂商
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 型号
     */
    @TableField("model")
    private String model;

    /**
     * 固件版本
     */
    @TableField("firmware")
    private String firmware;

    /**
     * 传输协议（UDP/TCP）
     */
    @TableField("transport")
    private String transport;

    /**
     * 数据流传输模式（UDP/TCP-ACTIVE/TCP-PASSIVE）
     */
    @TableField("streamMode")
    private String streamMode;

    /**
     * 是否在线
     */
    @TableField("status")
    private Boolean status;

    /**
     * 流媒体服务器id
     */
    @TableField("media_server_id")
    private String mediaServerId;

    /**
     * 注册时间
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

    /**
     * 注册有效期
     */
    @TableField("expires")
    private Integer expires;

    /**
     * 心跳时间
     */
    @TableField("keep_alive_time")
    private LocalDateTime keepAliveTime;

    /**
     * 心跳间隔
     */
    @TableField("keep_alive_interval")
    private Integer keepAliveInterval;

    /**
     * 字符集（UTF-8/GB2312）
     */
    @TableField("charset")
    private String charset;

    /**
     * 设备密码
     */
    @TableField("password")
    private String password;

    /**
     * 通道数
     */
    @TableField("channels")
    private Integer channels;

    /**
     * SSRC校验
     */
    @TableField("ssrcCheck")
    private Boolean ssrcCheck;

    /**
     * 主码流
     */
    @TableField("primaryStream")
    private Boolean primaryStream;

    /**
     * 作为消息通道
     */
    @TableField("asMessageChannel")
    private Boolean asMessageChannel;

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
