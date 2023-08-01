package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.bean.ZLMHttpHookParam;
import com.htuozhou.wvp.business.service.IZLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@RestController
@RequestMapping("/zlm/index/hook")
@Slf4j
public class ZLMHttpHookController {

    @Autowired
    private IZLMService zlmService;

    private static final JSONObject ZLM_RES_SUCCESS = new JSONObject();

    static {
        ZLM_RES_SUCCESS.put("code", 0);
        ZLM_RES_SUCCESS.put("msg", "success");
    }

    @PostMapping("/on_server_keepalive")
    public JSONObject onServerKeepAlive(@RequestBody ZLMHttpHookParam param) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 心跳上报", param.getMediaServerId());

        zlmService.setKeepAliveTime(param.getMediaServerId());
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody ZLMHttpHookParam param) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 启动上报", param.getMediaServerId());

        zlmService.online(param.getMediaServerId());
        return ZLM_RES_SUCCESS;
    }

}

