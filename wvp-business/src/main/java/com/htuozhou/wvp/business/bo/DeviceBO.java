package com.htuozhou.wvp.business.bo;

import com.htuozhou.wvp.persistence.po.DevicePO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/4/14
 */
@Data
public class DeviceBO {

    /**
     * 主键,自增
     */
    private Integer id;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备ip
     */
    private String ip;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 设备地址
     */
    private String address;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String firmware;

    /**
     * 传输协议（UDP/TCP）
     */
    private String transport;

    /**
     * 数据流传输模式（UDP/TCP-ACTIVE/TCP-PASSIVE）
     */
    private String streamMode;

    /**
     * 0、离线 1、在线
     */
    private Integer status;

    /**
     * zlm id
     */
    private String zlmId;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 注册有效期
     */
    private Integer expires;

    /**
     * 心跳时间
     */
    private LocalDateTime keepAliveTime;

    /**
     * 心跳间隔
     */
    private Integer keepAliveInterval;

    /**
     * 字符集（UTF-8/GB2312）
     */
    private String charset;

    /**
     * 设备密码
     */
    private String password;

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

    public DevicePO bo2po(){
        DevicePO po = new DevicePO();
        BeanUtils.copyProperties(this,po);

        return po;
    }

    public static DeviceBO po2bo(DevicePO po){
        DeviceBO bo = new DeviceBO();
        BeanUtils.copyProperties(po,bo);

        return bo;
    }
}
