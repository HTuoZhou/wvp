package com.htuozhou.wvp.business.zlm;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.bean.MediaServerItem;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import com.htuozhou.wvp.common.utils.CommonUtil;
import com.htuozhou.wvp.common.utils.RedisUtil;
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
    private RedisUtil redisUtil;

    /**
     * 获取服务器配置
     */
    public MediaServerItem getServerConfig(MediaServerBO bo) {
        MediaServerItem mediaServerItem = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.GET_SERVER_CONFIG), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 获取服务器配置成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(result.getData()));
                mediaServerItem = JSON.parseObject(JSON.toJSONString(jsonArray.get(0)), MediaServerItem.class);
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取服务器配置失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return mediaServerItem;
    }

    /**
     * 设置服务器配置
     */
    public Boolean setServerConfig(MediaServerBO bo) {
        boolean res = Boolean.FALSE;
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
                    if (restartServer(bo)) {
                        res = Boolean.TRUE;
                    }
                } else {
                    log.info("[ZLM ADDRESS {}] 设置服务器配置成功,不存在配置变更", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                    res = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 设置服务器配置失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return res;
    }

    /**
     * 重启服务器,只有Daemon方式才能重启,否则是直接关闭
     */
    public Boolean restartServer(MediaServerBO bo) {
        boolean res = Boolean.FALSE;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.RESTART_SERVER), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 重启服务器成功,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result.getMsg());
                res = Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 重启服务器失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return res;
    }


    /**
     * 获取流列表
     *
     * @param bo
     * @param app
     * @param scheme
     * @param streamId
     * @return
     */
    public JSONArray getMediaList(MediaServerBO bo, String app, String scheme, String streamId) {
        JSONArray jsonArray = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("app", app);
            param.put("scheme", scheme);
            param.put("stream", streamId);
            param.put("vhost", ZLMConstant.VHOST);
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.GET_MEDIA_LIST), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 获取流列表成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                jsonArray = JSON.parseArray(JSON.toJSONString(result.getData()));
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取流列表失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return jsonArray;
    }

    /**
     * 获取rtp代理时的某路ssrc rtp信息
     *
     * @param bo
     * @param streamId
     * @return
     */
    public JSONObject getRtpInfo(MediaServerBO bo, String streamId) {
        JSONObject jsonObject = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.GET_RTP_INFO), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 获取rtp代理时的某路ssrc rtp信息成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                jsonObject = JSON.parseObject(JSON.toJSONString(result.getData()));
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取rtp代理时的某路ssrc rtp信息失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return jsonObject;
    }

    /**
     * 关闭GB28181 RTP接收端口
     *
     * @param bo
     * @param streamId
     * @return
     */
    public Boolean closeRtpServer(MediaServerBO bo, String streamId) {
        boolean res = Boolean.FALSE;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.CLOSE_RTP_SERVER), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 关闭GB28181 RTP接收端口成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result.getData()));
                if (Objects.equals(jsonObject.getInteger("hit"), 1)) {
                    res = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 关闭GB28181 RTP接收端口失败,请确认ZLM是否启动", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return res;
    }

    /**
     * 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口)
     *
     * @param bo
     * @param streamId
     * @param ssrc
     * @param port
     * @param reUsePort
     * @param tcpMode
     * @return
     */
    public Integer openRtpServer(MediaServerBO bo, String streamId, Integer ssrc, Integer port, Boolean reUsePort, Integer tcpMode) {
        int resPort = -1;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            param.put("ssrc", ssrc);
            param.put("port", port);
            param.put("re_use_port", reUsePort);
            param.put("tcp_mode", tcpMode);
            ZLMResult result = postForm(getZLMUrl(bo, ZLMConstant.OPEN_RTP_SERVER), param);
            if (Objects.nonNull(result) && result.getCode() == 0) {
                log.info("[ZLM ADDRESS {}] 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口)成功", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result.getData()));
                resPort = jsonObject.getInteger("port");
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口)", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
        }
        return resPort;
    }

    public Integer createRtpSever(MediaServerBO bo, String streamId, Integer ssrc, Integer port, Boolean reUsePort, Integer tcpMode) {
        int rtpServerPort = -1;
        JSONObject rtpInfo = getRtpInfo(bo, streamId);
        if (Objects.isNull(rtpInfo)) {
            return rtpServerPort;
        } else if (rtpInfo.getBoolean("exist")) {
            int localPort = rtpInfo.getInteger("local_port");
            if (localPort == 0) {
                // 此时说明rtpServer已经创建但是流还没有推上来
                // 此时关闭并且重新打开rtpServer
                if (closeRtpServer(bo, streamId)) {
                    return createRtpSever(bo, streamId, ssrc, port, reUsePort, tcpMode);
                }
            }
            return localPort;
        } else {
            return openRtpServer(bo, streamId, ssrc, port, reUsePort, tcpMode);
        }
    }

    private ZLMResult postForm(String url, Map<String, Object> param) {
        return JSONUtil.toBean(HttpUtil.post(url, param), ZLMResult.class);
    }

    private String getZLMUrl(MediaServerBO bo, String apiName) {
        return String.format(ZLMConstant.URL_FMT, bo.getIp(), bo.getHttpPort(), apiName);
    }

}
