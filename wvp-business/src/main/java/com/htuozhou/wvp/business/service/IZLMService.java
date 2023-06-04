package com.htuozhou.wvp.business.service;

/**
 * @author hanzai
 * @date 2023/4/14
 */
public interface IZLMService {

    void saveZlmServer();

    void online(String mediaServerId);

    void setKeepAliveTime(String mediaServerId);

    void offline(String mediaServerId);

}
