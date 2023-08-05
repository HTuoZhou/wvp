package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.bean.ZLMHttpHookParam;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)){
            log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ADDRESS {}] 心跳上报", String.format(ZLMConstant.ADDRESS,bo.getIp(),bo.getHttpPort()));

            zlmService.setKeepAliveTime(param.getMediaServerId());
        } else {
            log.error("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}]不存在",param.getMediaServerId());
        }
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)){
            log.info("[ZLM HTTP HOOK] 收到 [ZLM MEDIA SERVER ID：{}] 启动上报", String.format(ZLMConstant.ADDRESS,bo.getIp(),bo.getHttpPort()));

            zlmService.online(param.getMediaServerId());
        }else {
            log.error("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}]不存在",param.getMediaServerId());
        }
        return ZLM_RES_SUCCESS;
    }

}

