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
    public InviteInfo getDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream) {
        String key = String.format(RedisConstant.INVITE_INFO, inviteSessionTypeDict.getType(), deviceId, channelId, stream);
        List<String> scanResult = redisUtil.scan(key);
        if (scanResult.size() != 1) {
            return null;
        }

        return (InviteInfo) redisUtil.get(scanResult.get(0));
    }

    /**
     * 添加一个invite回调
     *
     * @param inviteSessionTypeDict
     * @param deviceId
     * @param channelId
     * @param stream
     * @param callback
     */
    @Override
    public void add(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream, ErrorCallback<Object> callback) {
        String key = buildKey(inviteSessionTypeDict, deviceId, channelId, stream);
        List<ErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            callbacks = new CopyOnWriteArrayList<>();
            inviteErrorCallbackMap.put(key, callbacks);
        }
        callbacks.add(callback);
    }

    /**
     * 调用一个invite回调
     *
     * @param inviteSessionTypeDict
     * @param deviceId
     * @param channelId
     * @param stream
     * @param resultCodeEnum
     */
    @Override
    public void call(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream, Integer code, String msg, Object data) {
        String key = buildKey(inviteSessionTypeDict, deviceId, channelId, stream);
        List<ErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            return;
        }
        for (ErrorCallback<Object> callback : callbacks) {
            callback.run(code, msg, data);
        }
        inviteErrorCallbackMap.remove(key);
    }

    @Override
    public void removeDeviceInviteInfo(InviteSessionTypeDict inviteSessionTypeDict, String deviceId, String channelId, String stream) {
        String key = String.format(RedisConstant.INVITE_INFO, inviteSessionTypeDict.getType(), deviceId, channelId, stream);
        List<String> scanResult = redisUtil.scan(key);
        if (scanResult.size() > 0) {
            for (String s : scanResult) {
                InviteInfo inviteInfo = (InviteInfo) redisUtil.get(s);
                if (inviteInfo == null) {
                    continue;
                }
                redisUtil.delete(key);
                inviteErrorCallbackMap.remove(buildKey(inviteSessionTypeDict, deviceId, channelId, inviteInfo.getStream()));
            }
        }
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
