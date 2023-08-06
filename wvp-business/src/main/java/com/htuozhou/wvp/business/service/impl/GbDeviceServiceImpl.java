package com.htuozhou.wvp.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.service.IGbDeviceService;
import com.htuozhou.wvp.common.page.PageReq;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hanzai
 * @date 2023/8/5
 */
@Service
@Slf4j
public class GbDeviceServiceImpl implements IGbDeviceService {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    /**
     * 分页查询国标设备
     * @param pageReq
     * @return
     */
    @Override
    public IPage<DeviceBO> page(PageReq<DeviceBO> pageReq) {
        Integer pageNum = pageReq.getPageNum();
        Integer pageSize = pageReq.getPageSize();

        Page<DevicePO> page = deviceService.page(new Page<>(pageNum, pageSize), Wrappers.<DevicePO>emptyWrapper());
        return page.convert(DeviceBO::po2bo);
    }

    /**
     * 查询国标设备
     * @param deviceId
     * @return
     */
    @Override
    public DeviceBO getDevice(String deviceId) {
        DevicePO po = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                .eq(DevicePO::getDeviceId, deviceId));

        return DeviceBO.po2bo(po);
    }

    /**
     * pageReq
     * @param pageReq
     * @return
     */
    @Override
    public IPage<DeviceChannelBO> pageChannel(PageReq<DeviceChannelBO> pageReq) {
        Integer pageNum = pageReq.getPageNum();
        Integer pageSize = pageReq.getPageSize();
        DeviceChannelBO queryParam = pageReq.getQueryParam();

        Page<DeviceChannelPO> page = deviceChannelService.page(new Page<>(pageNum, pageSize), Wrappers.<DeviceChannelPO>lambdaQuery()
                .eq(DeviceChannelPO::getDeviceId, queryParam.getDeviceId()));
        return page.convert(DeviceChannelBO::po2vo);
    }
}
