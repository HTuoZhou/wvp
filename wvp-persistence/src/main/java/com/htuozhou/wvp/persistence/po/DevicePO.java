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
@TableName("device")
public class DevicePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增
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
     * 设备厂商
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 设备型号
     */
    @TableField("model")
    private String model;

    /**
     * 设备固件版本
     */
    @TableField("firmware")
    private String firmware;

    /**
     * 传输协议（UDP/TCP）
     */
    @TableField("transport")
    private String transport;

    /**
     * 数据流传输模式（UDP:UDP传输/TCP-ACTIVE:TCP主动模式/TCP-PASSIVE:TCP被动模式）
     */
    @TableField("streamMode")
    private String streamMode;

    /**
     * 0、离线 1、在线
     */
    @TableField("status")
    private Integer status;

    /**
     * zlm服务id
     */
    @TableField("zlm_server_id")
    private String zlmServerId;

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
