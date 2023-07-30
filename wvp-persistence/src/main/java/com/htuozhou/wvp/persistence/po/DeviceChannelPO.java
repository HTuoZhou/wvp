package com.htuozhou.wvp.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备通道表
 * </p>
 *
 * @author HTuoZhou
 * @since 2023-06-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wvp_device_channel")
public class DeviceChannelPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 通道id
     */
    @TableField("channel_id")
    private String channelId;

    /**
     * 通道名称
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
     * 设备归属
     */
    @TableField("owner")
    private String owner;

    /**
     * 行政区域
     */
    @TableField("civil_code")
    private String civilCode;

    /**
     * 警区
     */
    @TableField("block")
    private String block;

    /**
     * 安装地址
     */
    @TableField("address")
    private String address;

    /**
     * 父级id
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 信令安全模式（0、不采用2、S/MIME签名方式 3、S/MIME加密签名同时采用方式 4、数字摘要方式）
     */
    @TableField("safety_way")
    private Integer safetyWay;

    /**
     * 注册方式（1、符合IETFRFC3261标准的认证注册模式 2、基于口令的双向认证注册模式 3、基于数字证书的双向认证注册模式）
     */
    @TableField("register_way")
    private Integer registerWay;

    /**
     * 证书序列号
     */
    @TableField("cert_num")
    private String certNum;

    /**
     * 证书有效标识（0、无效 1、有效）
     */
    @TableField("certifiable")
    private Integer certifiable;

    /**
     * 证书无效原因码
     */
    @TableField("err_code")
    private Integer errCode;

    /**
     * 证书终止有效期
     */
    @TableField("end_time")
    private String endTime;

    /**
     * 保密属性（0、不涉密 1、涉密）
     */
    @TableField("secrecy")
    private Integer secrecy;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 端口号
     */
    @TableField("port")
    private Integer port;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 云台类型
     */
    @TableField("ptz_type")
    private Integer ptzType;

    /**
     * 0、离线 1、在线
     */
    @TableField("status")
    private Integer status;

    /**
     * 经度
     */
    @TableField("longitude")
    private Double longitude;

    /**
     * 经度
     */
    @TableField("latitude")
    private Double latitude;

    /**
     * 流id,存在表示正在直播
     */
    @TableField("stream_id")
    private String streamId;

    /**
     * 设备id
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 是否有子设备（1、有 0、没有）
     */
    @TableField("parental")
    private Integer parental;

    /**
     * 是否含有音频（1、有 0、没有）
     */
    @TableField("has_audio")
    private Integer hasAudio;

    /**
     * 子设备数
     */
    @TableField("sub_count")
    private Integer subCount;

    /**
     * GCJ02坐标系经度
     */
    @TableField("longitude_gcj02")
    private Double longitudeGcj02;

    /**
     * GCJ02坐标系纬度
     */
    @TableField("latitude_gcj02")
    private Double latitudeGcj02;

    /**
     * WGS84坐标系经度
     */
    @TableField("longitude_wgs84")
    private Double longitudeWgs84;

    /**
     * WGS84坐标系纬度
     */
    @TableField("latitude_wgs84")
    private Double latitudeWgs84;

    /**
     * 分组id
     */
    @TableField("group_id")
    private String groupId;

    /**
     * GPS更新时间
     */
    @TableField("gpsTime")
    private LocalDateTime gpsTime;

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
