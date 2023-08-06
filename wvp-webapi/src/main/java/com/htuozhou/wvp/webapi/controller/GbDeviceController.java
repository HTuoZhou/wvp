package com.htuozhou.wvp.webapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
     * @param deviceId
     * @return
     */
    @GetMapping("/getDevice/{deviceId}")
    public ApiFinalResult<GbDeviceVO> getDevice(@PathVariable("deviceId") String deviceId){
        return ApiFinalResult.success(GbDeviceVO.bo2vo(gbDeviceService.getDevice(deviceId)));
    }

    /**
     * 分页查询国标设备通道
     * @param pageReq
     * @return
     */
    @PostMapping("/channel/page")
    public ApiFinalResult<PageResp<DeviceChannelVO>> pageChannel(@RequestBody PageReq<DeviceChannelVO> pageReq){
        IPage<DeviceChannelBO> pageResp = gbDeviceService.pageChannel(pageReq.pageVo2Bo(DeviceChannelVO::vo2bo));
        return ApiFinalResult.success(PageResp.pageBo2Vo(pageResp, DeviceChannelVO::bo2vo));
    }

}
