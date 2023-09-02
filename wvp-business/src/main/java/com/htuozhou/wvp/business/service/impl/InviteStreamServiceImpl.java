package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.result.ErrorCallback;
import com.htuozhou.wvp.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Service
@Slf4j
public class InviteStreamServiceImpl implements IInviteStreamService {

    private final Map<String, List<ErrorCallback<Object>>> inviteErrorCallbackMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public InviteInfo getDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId) {
        String key = String.format(RedisConstant.INVITE_INFO, inviteSessionTypeDict.getType(), deviceId, channelId);
        return (InviteInfo) redisUtil.get(key);
    }

    @Override
    public void removeDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId) {
        String key = String.format(RedisConstant.INVITE_INFO, inviteSessionTypeDict.getType(), deviceId, channelId);
        InviteInfo inviteInfo = (InviteInfo) redisUtil.get(key);
        redisUtil.delete(key);
        inviteErrorCallbackMap.remove(buildKey(inviteSessionTypeDict, deviceId, channelId, inviteInfo.getStreamId()));
    }

    @Override
    public void addDeviceInviteInfo(InviteInfo inviteInfo) {
        String key = String.format(RedisConstant.INVITE_INFO, inviteInfo.getInviteSessionTypeDict().getType(), inviteInfo.getDeviceId(), inviteInfo.getChannelId());
        redisUtil.set(key, inviteInfo);
    }

    @Override
    public void add(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String streamId, ErrorCallback<Object> callback) {
        String key = buildKey(inviteSessionTypeDict, deviceId, channelId, streamId);
        List<ErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            callbacks = new CopyOnWriteArrayList<>();
            inviteErrorCallbackMap.put(key, callbacks);
        }
        callbacks.add(callback);
    }

    @Override
    public void call(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String streamId, Integer code, String msg, Object data) {
        String key = buildKey(inviteSessionTypeDict, deviceId, channelId, streamId);
        List<ErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            return;
        }
        for (ErrorCallback<Object> callback : callbacks) {
            callback.run(code, msg, data);
        }
        inviteErrorCallbackMap.remove(key);
    }

    private String buildKey(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream) {
        String key = inviteSessionTypeDict.getType() + "_" + deviceId + "_" + channelId;
        // 如果ssrc为null那么可以实现一个通道只能一次操作，ssrc不为null则可以支持一个通道多次invite
        if (StrUtil.isNotBlank(stream)) {
            key += ("_" + stream);
        }
        return key;
    }
}
