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

    void saveOrUpdateDevice(DeviceBO deviceBO);

    void offline(String deviceId);

    void saveBatchDeviceChannel(List<DeviceChannelBO> bos,String deviceId);

}
