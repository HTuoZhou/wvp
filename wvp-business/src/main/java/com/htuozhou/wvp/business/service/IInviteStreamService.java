package com.htuozhou.wvp.business.service;

import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.common.result.ErrorCallback;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public interface IInviteStreamService {

    void addInviteInfo(InviteInfo inviteInfo);

    void removeInviteInfo(InviteInfo inviteInfo);

    InviteInfo getDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId);

    InviteInfo getStreamInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String streamId);

    void add(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String streamId, ErrorCallback<Object> callback);

    void call(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String streamId, Integer code, String msg, Object data);

}
