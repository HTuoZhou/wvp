package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.ConfigInfoBO;
import com.htuozhou.wvp.business.bo.MediaServerLoadBO;
import com.htuozhou.wvp.business.bo.ResourceInfoBO;
import com.htuozhou.wvp.business.bo.SystemInfoBO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/7/29
 */
public interface ISystemService {

    /**
     * 获取系统信息
     *
     * @return
     */
    SystemInfoBO getSystemInfo();

    /**
     * 获取流媒体服务负载
     *
     * @return
     */
    List<MediaServerLoadBO> getMediaServerLoad();

    /**
     * 获取资源信息
     *
     * @return
     */
    ResourceInfoBO getResourceInfo();

    /**
     * 获取配置信息
     *
     * @return
     */
    ConfigInfoBO getConfigInfo();
}
