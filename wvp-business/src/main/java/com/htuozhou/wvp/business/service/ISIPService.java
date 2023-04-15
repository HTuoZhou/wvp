package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.DeviceBO;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface ISIPService {
    DeviceBO getDevice(String deviceId);

    void saveDevice(DeviceBO deviceBO);
}
