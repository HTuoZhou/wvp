package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.MediaServerBO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface IZLMService {

    void saveZlmServer();

    void online(String mediaServerId);

    void setKeepAliveTime(String mediaServerId);

    void offline(String mediaServerId);

    /**
     * 获取流媒体服务列表
     * @return
     */
    List<MediaServerBO> getMediaServerList();
}
