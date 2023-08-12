package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.htuozhou.wvp.business.bean.BaseTree;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
     *
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
     *
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
     * 删除国标设备
     *
     * @param deviceId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String deviceId) {
        deviceService.remove(Wrappers.<DevicePO>lambdaQuery().eq(DevicePO::getDeviceId, deviceId));
        deviceChannelService.remove(Wrappers.<DeviceChannelPO>lambdaQuery().eq(DeviceChannelPO::getDeviceId, deviceId));
        return Boolean.TRUE;
    }

    /**
     * 分页查询国标设备通道
     *
     * @param pageReq
     * @return
     */
    @Override
    public IPage<DeviceChannelBO> pageChannel(PageReq<DeviceChannelBO> pageReq) {
        Integer pageNum = pageReq.getPageNum();
        Integer pageSize = pageReq.getPageSize();
        DeviceChannelBO queryParam = pageReq.getQueryParam();

        Page<DeviceChannelPO> page = deviceChannelService.page(new Page<>(pageNum, pageSize), Wrappers.<DeviceChannelPO>lambdaQuery()
                .eq(DeviceChannelPO::getDeviceId, queryParam.getDeviceId())
                .and(StrUtil.isNotBlank(queryParam.getParentChannelId()), wrapper -> wrapper.eq(DeviceChannelPO::getParentId, queryParam.getParentChannelId()).or().eq(DeviceChannelPO::getCivilCode, queryParam.getParentChannelId()))
                .like(StrUtil.isNotBlank(queryParam.getChannelId()), DeviceChannelPO::getChannelId, queryParam.getChannelId())
                .gt((Objects.nonNull(queryParam.getChannelType()) && queryParam.getChannelType()), DeviceChannelPO::getSubCount, 0)
                .eq((Objects.nonNull(queryParam.getChannelType()) && !queryParam.getChannelType()), DeviceChannelPO::getSubCount, 0)
                .eq(Objects.nonNull(queryParam.getStatus()), DeviceChannelPO::getStatus, queryParam.getStatus()));
        return page.convert(DeviceChannelBO::po2vo);
    }

    /**
     * 查询国标设备树
     *
     * @param pageReq
     * @return
     */
    @Override
    public List<BaseTree<DeviceChannelBO>> tree(String deviceId, String parentId) {
        List<DeviceChannelPO> pos = deviceChannelService.list(Wrappers.<DeviceChannelPO>lambdaQuery().eq(DeviceChannelPO::getDeviceId, deviceId)
                .and(wrapper -> wrapper.eq(DeviceChannelPO::getParentId, parentId).or().eq(DeviceChannelPO::getCivilCode, parentId)));
        return  deviceChannel2Tree(pos, parentId);
    }

    private List<BaseTree<DeviceChannelBO>> deviceChannel2Tree(List<DeviceChannelPO> pos, String parentId) {
        if (CollUtil.isEmpty(pos)) {
            return Collections.emptyList();
        }

        List<BaseTree<DeviceChannelBO>> treeNodes = new ArrayList<>();
        for (DeviceChannelPO po : pos) {
            BaseTree<DeviceChannelBO> treeNode = new BaseTree<>();
            treeNode.setId(po.getChannelId());
            treeNode.setDeviceId(po.getDeviceId());
            treeNode.setName(po.getName());
            treeNode.setPid(parentId);
            treeNode.setChild(DeviceChannelBO.po2vo(po));
            treeNode.setParent(Boolean.FALSE);

            if (po.getChannelId().length() <= 8) {
                treeNode.setParent(Boolean.TRUE);
            }else {
                if (po.getChannelId().length() != 20) {
                    treeNode.setParent(po.getParental() == 1);
                }else {
                    int type = Integer.parseInt(po.getChannelId().substring(10, 13));
                    if (type == 215 || type == 216 || type == 200) {
                        treeNode.setParent(true);
                    }
                }
            }
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }
}
