package com.htuozhou.wvp.business.zlm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.htuozhou.wvp.business.constant.ZLMConstant;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.utils.CommonUtil;
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
    private ZLMProperties zlmProperties;

    /**
     * 获取服务器配置
     */
    public boolean getServerConfig() {
        try {
            ZLMResult result = postForm(ZLMConstant.GET_SERVER_CONFIG,null);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM MEDIA SERVER ID:{}] 获取服务器配置成功", zlmProperties.getMediaServerId());
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("[ZLM MEDIA SERVER ID:{}] 获取服务器配置失败,请确认ZLM是否启动", zlmProperties.getMediaServerId());
        }

        return Boolean.FALSE;
    }

    /**
     * 设置服务器配置
     */
    public void setServerConfig() {
        String hookPrefix;
        String hookIp = zlmProperties.getHookIp();
        if (CommonConstant.LINUX_OS && Objects.equals(zlmProperties.getHookIp(), CommonConstant.DEFAULT_LOCAL_IP)) {
            hookIp = CommonUtil.localIP();
        }
        hookPrefix = String.format(ZLMConstant.HOOK_URL_FMT, hookIp + ":" +
                environment.getProperty(CommonConstant.SERVER_PORT) +
                environment.getProperty(CommonConstant.SERVER_SERVLET_CONTEXT_PATH));

        Map<String, Object> param = new HashMap<>();
        param.put("api.secret", zlmProperties.getSecret());
        param.put("general.mediaServerId", zlmProperties.getMediaServerId());
        param.put("hook.alive_interval", zlmProperties.getHookAliveInterval());
        param.put("hook.enable", "1");
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

        try {
            ZLMResult result = postForm(ZLMConstant.SET_SERVER_CONFIG, param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                if (result.getChanged() > 0) {
                    log.info("[ZLM MEDIA SERVER ID:{}] 设置服务器配置成功,存在配置变更,重启以保证配置生效", zlmProperties.getMediaServerId());
                    restartServer();
                } else {
                    log.info("[ZLM MEDIA SERVER ID:{}] 设置服务器配置成功,不存在配置变更", zlmProperties.getMediaServerId());
                }
            }
        } catch (Exception e) {
            log.error("[ZLM MEDIA SERVER ID:{}] 设置服务器配置失败,请确认ZLM是否启动", zlmProperties.getMediaServerId());
        }
    }

    /**
     * 重启服务器，只有Daemon方式才能重启，否则是直接关闭
     */
    public void restartServer() {
        try {
            ZLMResult result = postForm(ZLMConstant.RESTART_SERVER, null);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM MEDIA SERVER ID:{}] 重启服务器成功,{}", zlmProperties.getMediaServerId(), result.getMsg());
            }
        } catch (Exception e) {
            log.error("[ZLM MEDIA SERVER ID:{}] 重启服务器失败,请确认ZLM是否启动", zlmProperties.getMediaServerId());
        }
    }

    public ZLMResult postForm(String apiName, Map<String, Object> param) {
        if (CollectionUtil.isEmpty(param)) {
            param = new HashMap<>();
        }
        param.put("secret", zlmProperties.getSecret());

        return JSONUtil.toBean(HttpUtil.post(getZLMUrl(apiName), param),ZLMResult.class);
    }

    private String getZLMUrl(String apiName) {
        return String.format(ZLMConstant.URL_FMT, zlmProperties.getIp(), zlmProperties.getHttpPort(), apiName);
    }

}
