package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/8/6
 */
@Data
public class DeviceChannelVO {

    /**
     * 主键,自增
     */
    private Integer id;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道id
     */
    private String channelId;

    /**
     * 通道名称
     */
    private String name;

    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 设备归属
     */
    private String owner;

    /**
     * 行政区域
     */
    private String civilCode;

    /**
     * 虚拟组织所属的业务分组ID,业务分组根据特定的业务需求制定,一个业务分组包含一组特定的虚拟组织
     */
    private String businessGroupId;

    /**
     * 父设备/区域/系统ID
     */
    private String parentId;

    /**
     * 注册方式（1、符合IETFRFC3261标准的认证注册模式 2、基于口令的双向认证注册模式 3、基于数字证书的双向认证注册模式）
     */
    private Integer registerWay;

    /**
     * 保密属性（0、不涉密 1、涉密）
     */
    private Integer secrecy;

    /**
     * 安装地址
     */
    private String address;

    /**
     * 是否有子设备（1、有 0、没有）
     */
    private Integer parental;

    /**
     * 警区
     */
    private String block;

    /**
     * 信令安全模式（0、不采用2、S/MIME签名方式 3、S/MIME加密签名同时采用方式 4、数字摘要方式）
     */
    private Integer safetyWay;

    /**
     * 证书序列号
     */
    private String certNum;

    /**
     * 证书有效标识（0、无效 1、有效）
     */
    private Integer certifiable;

    /**
     * 证书无效原因码
     */
    private Integer errCode;

    /**
     * 证书终止有效期
     */
    private String endTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 密码
     */
    private String password;

    /**
     * 云台类型
     */
    private Integer ptzType;

    /**
     * 是否在线
     */
    private Boolean status;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 流id,存在表示正在直播
     */
    private String streamId;

    /**
     * 是否含有音频
     */
    private Boolean hasAudio;

    /**
     * 子设备数
     */
    private Integer subCount;

    /**
     * GCJ02坐标系经度
     */
    private Double longitudeGcj02;

    /**
     * GCJ02坐标系纬度
     */
    private Double latitudeGcj02;

    /**
     * WGS84坐标系经度
     */
    private Double longitudeWgs84;

    /**
     * WGS84坐标系纬度
     */
    private Double latitudeWgs84;

    /**
     * 分组id
     */
    private String groupId;

    /**
     * GPS更新时间
     */
    private LocalDateTime gpsTime;

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

    public DeviceChannelBO vo2bo(){
        DeviceChannelBO bo = new DeviceChannelBO();
        BeanUtils.copyProperties(this,bo);

        return bo;
    }

    public static DeviceChannelVO bo2vo(DeviceChannelBO bo){
        DeviceChannelVO vo = new DeviceChannelVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
