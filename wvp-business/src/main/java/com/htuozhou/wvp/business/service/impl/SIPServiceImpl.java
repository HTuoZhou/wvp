package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.common.constant.DynamicTaskConstant;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Autowired
    private DynamicTask dynamicTask;

    @Override
    public DeviceBO getDevice(String deviceId) {
        DevicePO po = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                .eq(DevicePO::getDeviceId, deviceId));
        return Objects.isNull(po) ? null : DeviceBO.po2bo(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(String deviceId) {
        deviceService.update(Wrappers.<DevicePO>lambdaUpdate()
                .set(DevicePO::getStatus, Boolean.FALSE));

        String key = String.format(DynamicTaskConstant.GB_DEVICE_STATUS, deviceId);
        dynamicTask.stop(key);
    }

    @Override
    public void refreshKeepAlive(DeviceBO bo) {
        String key = String.format(DynamicTaskConstant.GB_DEVICE_STATUS, bo.getDeviceId());
        dynamicTask.startDelay(key, () -> offline(bo.getDeviceId()), bo.getKeepAliveInterval() * 3);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDeviceChannel(List<DeviceChannelBO> bos, String deviceId) {
        if (CollectionUtil.isEmpty(bos)) {
            deviceChannelService.remove(Wrappers.<DeviceChannelPO>lambdaQuery()
                    .eq(DeviceChannelPO::getDeviceId, deviceId));
        } else {
            List<DeviceChannelPO> pos = deviceChannelService.list(Wrappers.<DeviceChannelPO>lambdaQuery()
                    .eq(DeviceChannelPO::getDeviceId, deviceId));
            if (CollectionUtil.isEmpty(pos)) {
                deviceChannelService.saveBatch(bos.stream().map(DeviceChannelBO::bo2po).collect(Collectors.toList()));
            } else {
                List<String> channelIds = bos.stream().map(DeviceChannelBO::getChannelId).collect(Collectors.toList());
                List<String> removeChannelIds = pos.stream()
                        .map(DeviceChannelPO::getChannelId)
                        .filter(channelId -> !channelIds.contains(channelId)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(removeChannelIds)) {
                    deviceChannelService.remove(Wrappers.<DeviceChannelPO>lambdaQuery()
                            .in(DeviceChannelPO::getChannelId, removeChannelIds));
                }

                List<DeviceChannelPO> deviceChannelPOS = new ArrayList<>();
                for (DeviceChannelBO bo : bos) {
                    DeviceChannelPO deviceChannelPO;
                    Optional<DeviceChannelPO> optional = pos.stream().filter(po -> Objects.equals(bo.getChannelId(), po.getChannelId())).findFirst();
                    if ((optional.isPresent())) {
                        deviceChannelPO = optional.get();
                        Integer id = deviceChannelPO.getId();
                        BeanUtils.copyProperties(bo, deviceChannelPO);
                        deviceChannelPO.setId(id);
                    } else {
                        deviceChannelPO = new DeviceChannelPO();
                        BeanUtils.copyProperties(bo, deviceChannelPO);
                    }
                    deviceChannelPOS.add(deviceChannelPO);
                }
                deviceChannelService.saveOrUpdateBatch(deviceChannelPOS);
            }
        }
    }
}
