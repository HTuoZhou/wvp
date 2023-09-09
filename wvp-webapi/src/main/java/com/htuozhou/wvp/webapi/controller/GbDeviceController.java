package com.htuozhou.wvp.webapi.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.htuozhou.wvp.business.bean.BaseTree;
import com.htuozhou.wvp.business.bean.StreamContent;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.service.IGbDeviceService;
import com.htuozhou.wvp.common.page.PageReq;
import com.htuozhou.wvp.common.page.PageResp;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.webapi.vo.DeviceChannelVO;
import com.htuozhou.wvp.webapi.vo.GbDeviceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hanzai
 * @date 2023/8/5
 */
@RestController
@RequestMapping("/webapi/gbDevice")
public class GbDeviceController {

    @Autowired
    private IGbDeviceService gbDeviceService;

    /**
     * 分页查询国标设备
     *
     * @param pageReq
     * @return
     */
    @PostMapping("/page")
    public ApiFinalResult<PageResp<GbDeviceVO>> page(@RequestBody PageReq<GbDeviceVO> pageReq) {
        IPage<DeviceBO> pageResp = gbDeviceService.page(pageReq.pageVo2Bo(GbDeviceVO::vo2bo));
        return ApiFinalResult.success(PageResp.pageBo2Vo(pageResp, GbDeviceVO::bo2vo));
    }

    /**
     * 查询国标设备
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/getDevice/{deviceId}")
    public ApiFinalResult<GbDeviceVO> getDevice(@PathVariable("deviceId") String deviceId) {
        return ApiFinalResult.success(GbDeviceVO.bo2vo(gbDeviceService.getDevice(deviceId)));
    }

    /**
     * 删除国标设备
     *
     * @param deviceId
     * @return
     */
    @DeleteMapping("/delete/{deviceId}")
    public ApiFinalResult<Boolean> delete(@PathVariable("deviceId") String deviceId) {
        return ApiFinalResult.success(gbDeviceService.delete(deviceId));
    }

    /**
     * 分页查询国标设备通道
     *
     * @param pageReq
     * @return
     */
    @PostMapping("/channel/page")
    public ApiFinalResult<PageResp<DeviceChannelVO>> pageChannel(@RequestBody PageReq<DeviceChannelVO> pageReq) {
        IPage<DeviceChannelBO> pageResp = gbDeviceService.pageChannel(pageReq.pageVo2Bo(DeviceChannelVO::vo2bo));
        return ApiFinalResult.success(PageResp.pageBo2Vo(pageResp, DeviceChannelVO::bo2vo));
    }

    /**
     * 查询国标设备通道树
     *
     * @param deviceId
     * @param parentId
     * @return
     */
    @GetMapping("/channel/tree/{deviceId}/{parentId}")
    public ApiFinalResult<List<BaseTree<DeviceChannelVO>>> tree(@PathVariable("deviceId") String deviceId, @PathVariable("parentId") String parentId) {
        List<BaseTree<DeviceChannelBO>> bos = gbDeviceService.tree(deviceId, parentId);
        if (CollUtil.isEmpty(bos)) {
            return ApiFinalResult.success(Collections.emptyList());
        }

        List<BaseTree<DeviceChannelVO>> vos = new ArrayList<>();
        for (BaseTree<DeviceChannelBO> bo : bos) {
            BaseTree<DeviceChannelVO> vo = new BaseTree<>();
            vo.setId(bo.getId());
            vo.setDeviceId(bo.getDeviceId());
            vo.setName(bo.getName());
            vo.setPid(bo.getPid());
            vo.setChild(DeviceChannelVO.bo2vo(bo.getChild()));
            vo.setParent(bo.getParent());

            vos.add(vo);
        }
        return ApiFinalResult.success(vos);
    }

    /**
     * 国标设备通道点播
     *
     * @param deviceId
     * @param channelId
     * @return
     */
    @GetMapping("/channel/play/{deviceId}/{channelId}")
    public DeferredResult<ApiFinalResult<StreamContent>> play(@PathVariable("deviceId") String deviceId, @PathVariable("channelId") String channelId) {
        return gbDeviceService.play(deviceId, channelId);
    }

    /**
     * 国标设备通道切换音频
     *
     * @param id
     * @return
     */
    @PutMapping("/channel/switchAudio/{id}")
    public ApiFinalResult<Boolean> switchAudio(@PathVariable("id") Integer id) {
        return ApiFinalResult.success(gbDeviceService.switchAudio(id));
    }

}
