package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface ISIPService {

    List<DeviceBO> list();

    DeviceBO getDevice(String deviceId);

    void offline(DeviceBO deviceBO);

    void saveDeviceChannel(List<DeviceChannelBO> bos, String deviceId);

}
