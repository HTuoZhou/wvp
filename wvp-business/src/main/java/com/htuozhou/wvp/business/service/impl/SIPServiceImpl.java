package com.htuozhou.wvp.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/4/14
 */
@Service
@Slf4j
public class SIPServiceImpl implements ISIPService {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Override
    public DeviceBO getDevice(String deviceId) {
        DevicePO po = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                .eq(DevicePO::getDeviceId, deviceId));
        return Objects.isNull(po) ? null : DeviceBO.po2bo(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateDevice(DeviceBO deviceBO) {
        deviceService.saveOrUpdate(deviceBO.bo2po());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(String deviceId) {
        deviceService.update(Wrappers.<DevicePO>lambdaUpdate()
        .set(DevicePO::getStatus,0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchDeviceChannel(List<DeviceChannelBO> bos,String deviceId) {
        deviceChannelService.remove(Wrappers.<DeviceChannelPO>lambdaQuery()
                .eq(DeviceChannelPO::getDeviceId,deviceId));

        List<DeviceChannelPO> pos = bos.stream().map(DeviceChannelBO::bo2po).collect(Collectors.toList());
        deviceChannelService.saveBatch(pos);
    }
}
