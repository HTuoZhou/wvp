package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.common.constant.DynamicTaskConstant;
import com.htuozhou.wvp.webapi.vo.OnServerKeepAliveVO;
import com.htuozhou.wvp.webapi.vo.OnServerStartedVO;
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

    @Autowired
    private ZLMProperties zlmProperties;

    @Autowired
    private DynamicTask dynamicTask;

    private static final JSONObject ZLM_RES_SUCCESS = new JSONObject();

    static {
        ZLM_RES_SUCCESS.put("code", 0);
        ZLM_RES_SUCCESS.put("msg", "success");
    }

    @PostMapping("/on_server_keepalive")
    public JSONObject onServerKeepAlive(@RequestBody OnServerKeepAliveVO onServerKeepAliveVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 心跳上报", onServerKeepAliveVO.getMediaServerId());

        zlmService.setKeepAliveTime(onServerKeepAliveVO.getMediaServerId());

        String key = String.format(DynamicTaskConstant.ZLM_STATUS, onServerKeepAliveVO.getMediaServerId());
        dynamicTask.startDelay(key, () -> zlmService.offline(onServerKeepAliveVO.getMediaServerId()), zlmProperties.getHookAliveInterval() + 5);
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody OnServerStartedVO onServerStartedVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 启动上报", onServerStartedVO.getMediaServerId());

        zlmService.online(onServerStartedVO.getMediaServerId());

        String key = String.format(DynamicTaskConstant.ZLM_STATUS, onServerStartedVO.getMediaServerId());
        dynamicTask.startDelay(key, () -> zlmService.offline(onServerStartedVO.getMediaServerId()), zlmProperties.getHookAliveInterval() + 5);
        return ZLM_RES_SUCCESS;
    }

}

