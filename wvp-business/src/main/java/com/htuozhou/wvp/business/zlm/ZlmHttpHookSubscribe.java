package com.htuozhou.wvp.business.zlm;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.enumerate.ZLMHttpHookType;
import com.htuozhou.wvp.business.zlm.param.ZLMHttpHookParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/9/2
 */
@Component
public class ZlmHttpHookSubscribe {

    private Map<ZLMHttpHookType, Map<IZLMHttpHookSubscribe, Event>> subscribes = new ConcurrentHashMap<>();

    public void addSubscribe(IZLMHttpHookSubscribe subscribe, ZlmHttpHookSubscribe.Event event) {
        if (subscribe.getExpires() == null) {
            // 默认5分钟过期
            LocalDateTime expires = LocalDateTime.now().plusMinutes(5);
            subscribe.setExpires(expires);
        }
        subscribes.computeIfAbsent(subscribe.getHookType(), key -> new ConcurrentHashMap<>()).put(subscribe, event);
    }

    public void removeSubscribe(IZLMHttpHookSubscribe hookSubscribe) {
        Map<IZLMHttpHookSubscribe, Event> eventMap = subscribes.get(hookSubscribe.getHookType());
        if (eventMap == null) {
            return;
        }

        Set<Map.Entry<IZLMHttpHookSubscribe, Event>> entries = eventMap.entrySet();
        if (entries.size() > 0) {
            List<Map.Entry<IZLMHttpHookSubscribe, Event>> entriesToRemove = new ArrayList<>();
            for (Map.Entry<IZLMHttpHookSubscribe, ZlmHttpHookSubscribe.Event> entry : entries) {
                JSONObject content = entry.getKey().getContent();
                if (content == null || content.size() == 0) {
                    entriesToRemove.add(entry);
                    continue;
                }
                Boolean result = null;
                for (String s : content.keySet()) {
                    if (result == null) {
                        result = content.getString(s).equals(hookSubscribe.getContent().getString(s));
                    } else {
                        if (content.getString(s) == null) {
                            continue;
                        }
                        result = result && content.getString(s).equals(hookSubscribe.getContent().getString(s));
                    }
                }
                if (result) {
                    entriesToRemove.add(entry);
                }
            }

            if (!CollectionUtils.isEmpty(entriesToRemove)) {
                for (Map.Entry<IZLMHttpHookSubscribe, ZlmHttpHookSubscribe.Event> entry : entriesToRemove) {
                    eventMap.remove(entry.getKey());
                }
                if (eventMap.size() == 0) {
                    subscribes.remove(hookSubscribe.getHookType());
                }
            }

        }
    }

    public ZlmHttpHookSubscribe.Event sendNotify(ZLMHttpHookType hookType, JSONObject jsonObject) {
        ZlmHttpHookSubscribe.Event event = null;
        Map<IZLMHttpHookSubscribe, Event> eventMap = subscribes.get(hookType);
        if (eventMap == null) {
            return null;
        }
        for (IZLMHttpHookSubscribe key : eventMap.keySet()) {
            Boolean result = null;
            for (String s : key.getContent().keySet()) {
                if (result == null) {
                    result = key.getContent().getString(s).equals(jsonObject.getString(s));
                } else {
                    if (key.getContent().getString(s) == null) {
                        continue;
                    }
                    result = result && key.getContent().getString(s).equals(jsonObject.getString(s));
                }
            }
            if (null != result && result) {
                event = eventMap.get(key);
            }
        }
        return event;
    }

    public interface Event {
        void response(MediaServerBO mediaServerBO, ZLMHttpHookParam hookParam);
    }

}
