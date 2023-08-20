package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.common.result.ErrorCallback;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public interface IInviteStreamService {
    InviteInfo getDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream);

    /**
     * 添加一个invite回调
     *
     * @param inviteSessionTypeDict
     * @param deviceId
     * @param channelId
     * @param stream
     * @param callback
     */
    void add(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream, ErrorCallback<Object> callback);

    /**
     * 调用一个invite回调
     *
     * @param inviteSessionTypeDict
     * @param deviceId
     * @param channelId
     * @param stream
     * @param resultCodeEnum
     */
    void call(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream, Integer code, String msg, Object data);

    void removeDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream);

}
