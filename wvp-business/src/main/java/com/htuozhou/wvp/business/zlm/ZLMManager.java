package com.htuozhou.wvp.business.zlm;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.htuozhou.wvp.business.bean.MediaServerItem;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import com.htuozhou.wvp.common.utils.CommonUtil;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Component
@Slf4j
public class ZLMManager {

    @Autowired
    private Environment environment;

    @Autowired
    private IMediaServerService mediaServerService;

    /**
     * 获取服务器配置
     */
    public MediaServerItem getServerConfig(MediaServerBO bo) {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.GET_SERVER_CONFIG), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 获取服务器配置成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
            }
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(result.getData()));
            return JSON.parseObject(JSON.toJSONString(jsonArray.get(0)), MediaServerItem.class);
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取服务器配置失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return null;
    }

    /**
     * 设置服务器配置
     */
    public Boolean setServerConfig(MediaServerBO bo) {
        String hookPrefix;
        String hookIp = bo.getHookIp();
        if (CommonConstant.LINUX_OS && Objects.equals(bo.getHookIp(), CommonConstant.DEFAULT_LOCAL_IP)) {
            hookIp = CommonUtil.localIP();
        }
        hookPrefix = String.format(ZLMConstant.HOOK_URL_FMT, hookIp + ":" +
                environment.getProperty(CommonConstant.SERVER_PORT) +
                environment.getProperty(CommonConstant.SERVER_SERVLET_CONTEXT_PATH));

        Map<String, Object> param = new HashMap<>();
        param.put("secret", bo.getSecret());
        param.put("api.secret", bo.getSecret());
        param.put("general.mediaServerId", bo.getMediaServerId());
        param.put("hook.enable", "1");
        param.put("hook.alive_interval", bo.getHookAliveInterval());
        param.put("hook.on_flow_report", String.format("%s/on_flow_report", hookPrefix));
        param.put("hook.on_http_access", String.format("%s/on_http_access", hookPrefix));
        param.put("hook.on_play", String.format("%s/on_play", hookPrefix));
        param.put("hook.on_publish", String.format("%s/on_publish", hookPrefix));
        param.put("hook.on_record_mp4", String.format("%s/on_record_mp4", hookPrefix));
        param.put("hook.on_record_ts", String.format("%s/on_record_ts", hookPrefix));
        param.put("hook.on_rtp_server_timeout", String.format("%s/on_rtp_server_timeout", hookPrefix));
        param.put("hook.on_rtsp_auth", String.format("%s/on_rtsp_auth", hookPrefix));
        param.put("hook.on_rtsp_realm", String.format("%s/on_rtsp_realm", hookPrefix));
        param.put("hook.on_send_rtp_stopped", String.format("%s/on_send_rtp_stopped", hookPrefix));
        param.put("hook.on_server_keepalive", String.format("%s/on_server_keepalive", hookPrefix));
        param.put("hook.on_server_started", String.format("%s/on_server_started", hookPrefix));
        param.put("hook.on_shell_login", String.format("%s/on_shell_login", hookPrefix));
        param.put("hook.on_stream_changed", String.format("%s/on_stream_changed", hookPrefix));
        param.put("hook.on_stream_none_reader", String.format("%s/on_stream_none_reader", hookPrefix));
        param.put("hook.on_stream_not_found", String.format("%s/on_stream_not_found", hookPrefix));
        param.put("rtp_proxy.port_range", bo.getRtpPortRange());

        try {
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.SET_SERVER_CONFIG), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                if (result.getChanged() > 0) {
                    log.info("[ZLM ADDRESS {}] 设置服务器配置成功,存在配置变更,重启以保证配置生效", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                    restartServer(bo);
                } else {
                    log.info("[ZLM ADDRESS {}] 设置服务器配置成功,不存在配置变更", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                }
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 设置服务器配置失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
            return Boolean.FALSE;
        }
    }

    /**
     * 重启服务器,只有Daemon方式才能重启,否则是直接关闭
     */
    public void restartServer(MediaServerBO bo) {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.RESTART_SERVER), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 重启服务器成功,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result.getMsg());
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 重启服务器失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
    }

    public ZLMResult postForm(String url, Map<String, Object> param) {
        return JSONUtil.toBean(HttpUtil.post(url, param), ZLMResult.class);
    }

    private String getZLMUrl(MediaServerBO bo, String apiName) {
        return String.format(ZLMConstant.URL_FMT, bo.getIp(), bo.getHttpPort(), apiName);
    }

}
