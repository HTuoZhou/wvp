package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface ISIPService {
    DeviceBO getDevice(String deviceId);

    void offline(String deviceId);

    void refreshKeepAlive(DeviceBO bo);

    void saveDeviceChannel(List<DeviceChannelBO> bos, String deviceId);

}
