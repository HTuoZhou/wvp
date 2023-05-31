package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.persistence.po.ZlmServerPO;
import com.htuozhou.wvp.persistence.service.IZlmServerService;
import com.htuozhou.wvp.webapi.vo.OnServerKeepAliveVO;
import com.htuozhou.wvp.webapi.vo.OnServerStartedVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@RestController
@RequestMapping("/zlm/index/hook")
@Slf4j
public class ZLMHttpHookController {

    @Autowired
    private IZlmServerService zlmServerService;

    private static final JSONObject ZLM_RES_SUCCESS = new JSONObject();

    static {
        ZLM_RES_SUCCESS.put("code", 0);
        ZLM_RES_SUCCESS.put("msg", "success");
    }

    @PostMapping("/on_server_keepalive")
    public JSONObject onServerKeepAlive(@RequestBody OnServerKeepAliveVO onServerKeepAliveVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 心跳上报", onServerKeepAliveVO.getMediaServerId());

        zlmServerService.update(Wrappers.<ZlmServerPO>lambdaUpdate()
                .eq(ZlmServerPO::getMediaServerId,onServerKeepAliveVO.getMediaServerId())
                .set(ZlmServerPO::getHookAliveTime, LocalDateTime.now()));

        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody OnServerStartedVO onServerStartedVO) {
        log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 启动上报", onServerStartedVO.getMediaServerId());

        zlmServerService.update(Wrappers.<ZlmServerPO>lambdaUpdate()
                .eq(ZlmServerPO::getMediaServerId,onServerStartedVO.getMediaServerId())
                .set(ZlmServerPO::getStatus,1));

        return ZLM_RES_SUCCESS;
    }

}

