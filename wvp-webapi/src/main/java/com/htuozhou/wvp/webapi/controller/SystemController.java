package com.htuozhou.wvp.webapi.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.htuozhou.wvp.business.bo.MediaServerLoadBO;
import com.htuozhou.wvp.business.service.ISystemService;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.webapi.vo.MediaServerLoadVO;
import com.htuozhou.wvp.webapi.vo.ResourceInfoVO;
import com.htuozhou.wvp.webapi.vo.SystemInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@RestController
@RequestMapping("/webapi/system")
public class SystemController {

    @Autowired
    private ISystemService systemService;

    /**
     * 获取系统信息
     * @return
     */
    @GetMapping("/getSystemInfo")
    public ApiFinalResult<SystemInfoVO> getSystemInfo() {
        return ApiFinalResult.success(SystemInfoVO.bo2vo(systemService.getSystemInfo()));
    }

    /**
     * 获取流媒体服务负载
     * @return
     */
    @GetMapping("/getMediaServerLoad")
    public ApiFinalResult<List<MediaServerLoadVO>> getMediaServerLoad() {
        List<MediaServerLoadBO> bos = systemService.getMediaServerLoad();
        if (CollectionUtil.isEmpty(bos)) {
            return ApiFinalResult.success(Collections.emptyList());
        }
        return ApiFinalResult.success(bos.stream().map(MediaServerLoadVO::bo2vo).collect(Collectors.toList()));
    }

    /**
     * 获取资源信息
     * @return
     */
    @GetMapping("/getResourceInfo")
    public ApiFinalResult<ResourceInfoVO> getResourceInfo() {
        return ApiFinalResult.success(ResourceInfoVO.bo2vo(systemService.getResourceInfo()));
    }

}
