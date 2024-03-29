package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.ResourceBaseInfo;
import com.htuozhou.wvp.business.bo.*;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.ISystemService;
import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.utils.RedisUtil;
import com.htuozhou.wvp.common.utils.SystemInfoUtil;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.po.MediaServerPO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Service
@Slf4j
public class SystemServiceImpl implements ISystemService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IMediaServerService zlmServerService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private SIPProperties sipProperties;

    /**
     * 获取系统信息
     *
     * @return
     */
    @Override
    public SystemInfoBO getSystemInfo() {
        String cpuKey = RedisConstant.SYSTEM_INFO_CPU;
        String memKey = RedisConstant.SYSTEM_INFO_MEM;
        String netKey = RedisConstant.SYSTEM_INFO_NET;
        String diskKey = RedisConstant.SYSTEM_INFO_DISK;

        SystemInfoBO bo = new SystemInfoBO();
        bo.setCpu(redisUtil.lGet(cpuKey, 0, -1));
        bo.setMem(redisUtil.lGet(memKey, 0, -1));
        bo.setNet(redisUtil.lGet(netKey, 0, -1));
        bo.setDisk(redisUtil.get(diskKey));
        bo.setNetTotal(SystemInfoUtil.getNetworkTotal());

        return bo;
    }

    /**
     * 获取流媒体服务负载
     *
     * @return
     */
    @Override
    public List<MediaServerLoadBO> getMediaServerLoad() {
        List<MediaServerLoadBO> loadBos = new ArrayList<>();

        List<MediaServerPO> pos = zlmServerService.list(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getStatus, 1));
        if (CollectionUtil.isEmpty(pos)) {
            return Collections.emptyList();
        }

        List<MediaServerBO> bos = pos.stream().map(MediaServerBO::po2bo).collect(Collectors.toList());
        for (MediaServerBO bo : bos) {
            MediaServerLoadBO loadBo = new MediaServerLoadBO();
            loadBo.setId(bo.getMediaServerId());
            loadBo.setPush(0);
            loadBo.setProxy(0);
            loadBo.setGbReceive(0);
            loadBo.setGbSend(0);
            loadBos.add(loadBo);
        }
        return loadBos;
    }

    @Override
    public ResourceInfoBO getResourceInfo() {
        ResourceInfoBO bo = new ResourceInfoBO();

        ResourceBaseInfo device;
        List<DevicePO> devicePOS = deviceService.list(Wrappers.<DevicePO>emptyWrapper());
        if (CollUtil.isEmpty(devicePOS)) {
            device = new ResourceBaseInfo(0, 0);
        } else {
            device = new ResourceBaseInfo(devicePOS.size(), (int) devicePOS.stream().filter(DevicePO::getStatus).count());
        }

        ResourceBaseInfo channel;
        List<DeviceChannelPO> deviceChannelPOS = deviceChannelService.list(Wrappers.<DeviceChannelPO>emptyWrapper());
        if (CollUtil.isEmpty(deviceChannelPOS)) {
            channel = new ResourceBaseInfo(0, 0);
        } else {
            channel = new ResourceBaseInfo(deviceChannelPOS.size(), (int) deviceChannelPOS.stream().filter(DeviceChannelPO::getStatus).count());
        }

        ResourceBaseInfo push = new ResourceBaseInfo(0, 0);
        ResourceBaseInfo proxy = new ResourceBaseInfo(0, 0);

        bo.setDevice(device);
        bo.setChannel(channel);
        bo.setPush(push);
        bo.setProxy(proxy);

        return bo;
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    @Override
    public ConfigInfoBO getConfigInfo() {
        ConfigInfoBO bo = new ConfigInfoBO();
        BeanUtils.copyProperties(sipProperties, bo);

        return bo;
    }
}
