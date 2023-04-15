package com.htuozhou.wvp.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/14
 */
@Service
@Slf4j
public class SIPServiceImpl implements ISIPService {

    @Autowired
    private IDeviceService deviceService;

    @Override
    public DeviceBO getDevice(String deviceId) {
        DevicePO po = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                .eq(DevicePO::getDeviceId, deviceId));
        return Objects.isNull(po) ? null : DeviceBO.po2bo(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDevice(DeviceBO deviceBO) {
        deviceService.saveOrUpdate(deviceBO.bo2po());
    }
}
