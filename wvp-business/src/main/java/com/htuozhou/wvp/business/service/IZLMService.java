package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.MediaServerBO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface IZLMService {

    MediaServerBO getDefaultMediaServer();

    MediaServerBO getMediaServer(String mediaServerId);

    void saveOrUpdateMediaServer(MediaServerBO bo);

    void online(String mediaServerId);

    void setKeepAliveTime(String mediaServerId);

    void offline(MediaServerBO mediaServerBO);

    /**
     * 获取流媒体服务列表
     *
     * @return
     */
    List<MediaServerBO> getMediaServerList();

    /**
     * 测试流媒体服务
     *
     * @param ip
     * @param port
     * @param secret
     * @return
     */
    MediaServerBO check(String ip, Integer port, String secret);

    /**
     * 编辑流媒体服务
     *
     * @param bo
     * @return
     */
    Boolean edit(MediaServerBO bo);

    /**
     * 删除流媒体服务
     *
     * @param id
     * @return
     */
    Boolean delete(Integer id);
}
