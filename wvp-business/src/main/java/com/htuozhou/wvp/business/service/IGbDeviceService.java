package com.htuozhou.wvp.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.htuozhou.wvp.business.bean.BaseTree;
import com.htuozhou.wvp.business.bean.StreamContent;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.common.page.PageReq;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/8/5
 */
public interface IGbDeviceService {

    /**
     * 分页查询国标设备
     *
     * @param pageReq
     * @return
     */
    IPage<DeviceBO> page(PageReq<DeviceBO> pageReq);

    /**
     * 查询国标设备
     *
     * @param deviceId
     * @return
     */
    DeviceBO getDevice(String deviceId);

    /**
     * 分页查询国标设备通道
     *
     * @param pageReq
     * @return
     */
    IPage<DeviceChannelBO> pageChannel(PageReq<DeviceChannelBO> pageReq);

    /**
     * 删除国标设备
     *
     * @param deviceId
     * @return
     */
    Boolean delete(String deviceId);

    /**
     * 查询国标设备通道树
     *
     * @param deviceId
     * @param parentId
     * @return
     */
    List<BaseTree<DeviceChannelBO>> tree(String deviceId, String parentId);

    /**
     * 国标设备通道点播
     *
     * @param deviceId
     * @param channelId
     * @return
     */
    DeferredResult<ApiFinalResult<StreamContent>> play(String deviceId, String channelId);

    /**
     * 国标设备通道切换音频
     *
     * @param id
     * @return
     */
    Boolean switchAudio(Integer id);
}
