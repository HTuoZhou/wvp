package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.common.result.ErrorCallback;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public interface IPlayService {

    void play(MediaServerBO mediaServerBO, DeviceBO deviceBO, String channelId, String ssrc, String uuid, ErrorCallback<Object> callback);
}
