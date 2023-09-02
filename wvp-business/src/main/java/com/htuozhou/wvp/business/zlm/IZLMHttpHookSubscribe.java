package com.htuozhou.wvp.business.zlm;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.enumerate.ZLMHttpHookType;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/9/2
 */
public interface IZLMHttpHookSubscribe {

    /**
     * 获取hook类型
     * @return hook类型
     */
    ZLMHttpHookType getHookType();

    /**
     * 获取hook的具体内容
     * @return hook的具体内容
     */
    JSONObject getContent();

    /**
     * 设置过期时间
     * @param localDateTime 过期时间
     */
    void setExpires(LocalDateTime localDateTime);

    /**
     * 获取过期时间
     * @return 过期时间
     */
    LocalDateTime getExpires();
    
}
