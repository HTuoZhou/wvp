package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bean.SSRCInfo;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.common.result.ErrorCallback;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public interface IPlayService {

    SSRCInfo play(MediaServerBO mediaServerBO, String deviceId, String channelId, Integer ssrc, ErrorCallback<Object> callback);

    Integer getPlaySsrc(String mediaServerId);

    Integer getPlayBackSsrc(String mediaServerId);

}
