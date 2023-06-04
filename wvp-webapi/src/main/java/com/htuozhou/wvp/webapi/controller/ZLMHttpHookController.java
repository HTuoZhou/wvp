package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.webapi.vo.OnServerKeepAliveVO;
import com.htuozhou.wvp.webapi.vo.OnServerStartedVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

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

    @Autowired
    private ZLMProperties zlmProperties;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static final JSONObject ZLM_RES_SUCCESS = new JSONObject();

    static {
        ZLM_RES_SUCCESS.put("code", 0);
        ZLM_RES_SUCCESS.put("msg", "success");
    }

    @PostMapping("/on_server_keepalive")
    public JSONObject onServerKeepAlive(@RequestBody OnServerKeepAliveVO onServerKeepAliveVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 心跳上报", onServerKeepAliveVO.getMediaServerId());

        zlmService.setKeepAliveTime(onServerKeepAliveVO.getMediaServerId());

        threadPoolTaskScheduler.schedule(() -> zlmService.offline(onServerKeepAliveVO.getMediaServerId()),
                Instant.now().plusMillis((long) zlmProperties.getHookAliveInterval() * 3 * 1000));
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody OnServerStartedVO onServerStartedVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 启动上报", onServerStartedVO.getMediaServerId());

       zlmService.online(onServerStartedVO.getMediaServerId());

        return ZLM_RES_SUCCESS;
    }

}

