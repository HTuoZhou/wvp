package com.htuozhou.wvp.business.zlm;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htuozhou.wvp.business.bean.MediaServerItem;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import com.htuozhou.wvp.common.exception.BusinessException;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.common.utils.CommonUtil;
import com.htuozhou.wvp.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

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

    @Autowired
    private SIPProperties sipProperties;

    /**
     * 获取服务器配置
     *
     * @param bo
     * @return
     */
    public MediaServerItem getServerConfig(MediaServerBO bo) {
        MediaServerItem result = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            log.info("[ZLM ADDRESS {}] 获取服务器配置,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.GET_SERVER_CONFIG), param);
            log.info("[ZLM ADDRESS {}] 获取服务器配置,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(jsonObject.get("data")));
                result = JSON.parseObject(JSON.toJSONString(jsonArray.get(0)), MediaServerItem.class);
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取服务器配置失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 设置服务器配置
     *
     * @param bo
     * @return
     */
    public Boolean setServerConfig(MediaServerBO bo) {
        boolean result = Boolean.FALSE;
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
        param.put("hook.on_server_exited", String.format("%s/on_server_started", hookPrefix));
        param.put("hook.on_shell_login", String.format("%s/on_shell_login", hookPrefix));
        param.put("hook.on_stream_changed", String.format("%s/on_stream_changed", hookPrefix));
        param.put("hook.on_stream_none_reader", String.format("%s/on_stream_none_reader", hookPrefix));
        param.put("hook.on_stream_not_found", String.format("%s/on_stream_not_found", hookPrefix));
        param.put("rtp_proxy.port_range", bo.getRtpPortRange());

        try {
            log.info("[ZLM ADDRESS {}] 设置服务器配置,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.SET_SERVER_CONFIG), param);
            log.info("[ZLM ADDRESS {}] 设置服务器配置,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                if (jsonObject.getInteger("changed") > 0) {
                    if (restartServer(bo)) {
                        result = Boolean.TRUE;
                    }
                } else {
                    result = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 设置服务器配置失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 重启服务器,只有Daemon方式才能重启,否则是直接关闭
     *
     * @param bo
     * @return
     */
    public Boolean restartServer(MediaServerBO bo) {
        boolean result = Boolean.FALSE;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            log.info("[ZLM ADDRESS {}] 重启服务器,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.RESTART_SERVER), param);
            log.info("[ZLM ADDRESS {}] 重启服务器,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                result = Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 重启服务器失败,{}}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
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
        JSONArray result = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("app", app);
            param.put("scheme", scheme);
            param.put("stream", streamId);
            param.put("vhost", ZLMConstant.VHOST);
            log.info("[ZLM ADDRESS {}] 获取流列表,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.GET_MEDIA_LIST), param);
            log.info("[ZLM ADDRESS {}] 获取流列表,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                result = JSON.parseArray(JSON.toJSONString(jsonObject.get("data")));
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取流列表失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 获取RTP推流信息
     *
     * @param bo
     * @param streamId
     * @return
     */
    public JSONObject getRtpInfo(MediaServerBO bo, String streamId) {
        JSONObject result = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            log.info("[ZLM ADDRESS {}] 获取RTP推流信息,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.GET_RTP_INFO), param);
            log.info("[ZLM ADDRESS {}] 获取RTP推流信息,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                result = jsonObject;
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取RTP推流信息失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 关闭RTP服务器
     *
     * @param bo
     * @param streamId
     * @return
     */
    public JSONObject closeRtpServer(MediaServerBO bo, String streamId) {
        JSONObject result = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            log.info("[ZLM ADDRESS {}] 关闭RTP服务器服务器配置,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.CLOSE_RTP_SERVER), param);
            log.info("[ZLM ADDRESS {}] 关闭RTP服务器服务器配置,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                result = jsonObject;
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 关闭RTP服务器失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 创建RTP服务器
     *
     * @param bo
     * @param streamId
     * @param ssrc
     * @param port
     * @param reUsePort
     * @param tcpMode
     * @return
     */
    public Integer openRtpServer(MediaServerBO bo, String streamId, String ssrc, Integer port, Boolean reUsePort, Integer tcpMode) {
        int result = -1;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("secret", bo.getSecret());
            param.put("stream_id", streamId);
            param.put("ssrc", Integer.valueOf(ssrc));
            param.put("port", port);
            param.put("re_use_port", reUsePort);
            param.put("tcp_mode", tcpMode);
            log.info("[ZLM ADDRESS {}] 创建RTP服务器服务器,请求的参数是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            JSONObject jsonObject = postForm(getZLMUrl(bo, ZLMConstant.OPEN_RTP_SERVER), param);
            log.info("[ZLM ADDRESS {}] 创建RTP服务器服务器,返回的结果是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), jsonObject);
            if (Objects.nonNull(jsonObject) && jsonObject.getInteger("code") == 0) {
                result = jsonObject.getInteger("port");
            }
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 创建RTP服务器失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 获取截图
     *
     * @param bo
     */
    public void getSnap(MediaServerBO bo, String deviceId, String channelId) {
        String requestUrl = getZLMUrl(bo, ZLMConstant.GET_SNAP) +
                "?secret=" + bo.getSecret() +
                "&url=" + String.format(CommonConstant.RTSP_PROTOCOL + String.format(ZLMConstant.STREAM_LIVE_RTMP_RTSP_FMT, bo.getStreamIp(), bo.getRtspPort(), "rtp", String.format("%s_%s", deviceId, channelId))) +
                "&timeout_sec=10&expire_sec=1";
        log.info("[ZLM ADDRESS {}] 获取截图,请求的地址是:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), requestUrl);
        byte[] bytes = downloadBytes(requestUrl);
        File snapPath = new File(CommonConstant.SNAP_PATH);
        if (!snapPath.exists()) {
            boolean mkdir = snapPath.mkdir();
        }
        File snapName = new File(snapPath + File.separator + String.format(CommonConstant.SNAP_NAME, deviceId, channelId));
        try (FileOutputStream os = new FileOutputStream(snapName)) {
            os.write(bytes);
        } catch (Exception e) {
            log.error("[ZLM ADDRESS {}] 获取截图失败,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), e.getLocalizedMessage());
        }
    }

    public Integer createRtpSever(MediaServerBO bo, String streamId, String ssrc, Integer port, Boolean reUsePort, Integer tcpMode) {
        int rtpServerPort = -1;
        JSONObject rtpInfo = getRtpInfo(bo, streamId);
        if (Objects.isNull(rtpInfo)) {
            return rtpServerPort;
        } else if (rtpInfo.getBoolean("exist")) {
            int localPort = rtpInfo.getInteger("local_port");
            if (localPort == 0) {
                // 此时说明rtpServer已经创建但是流还没有推上来
                // 此时关闭并且重新打开rtpServer
                JSONObject closeRtpServer = closeRtpServer(bo, streamId);
                if (closeRtpServer.getInteger("hit") == 1) {
                    return createRtpSever(bo, streamId, ssrc, port, reUsePort, tcpMode);
                }
            }
            return localPort;
        } else {
            return openRtpServer(bo, streamId, ssrc, port, reUsePort, tcpMode);
        }
    }

    public void initSsrc(String mediaServerId) {
        log.info("[ZLM] [ZLM MEDIA SERVER ID {}] INIT SSRC", mediaServerId);
        String ssrcPrefix = sipProperties.getDomain().substring(3, 8);
        String key = String.format(RedisConstant.SSRC_INFO, mediaServerId);
        List<String> ssrcList = new ArrayList<>();
        for (int i = 0; i < ZLMConstant.STREAM_MAX_COUNT; i++) {
            String ssrc = String.format("%s%04d", ssrcPrefix, i);
            ssrcList.add(ssrc);
        }
        redisUtil.delete(key);
        redisUtil.sSet(key, ssrcList.toArray());
    }

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     * @param mediaServerId
     * @return
     */
    public String getPlaySsrc(String mediaServerId) {
        log.info("[ZLM] [ZLM MEDIA SERVER ID {}] GET PLAY SSRC", mediaServerId);
        return "0" + getSN(mediaServerId);
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     *
     * @param mediaServerId
     * @return
     */
    public String getPlayBackSsrc(String mediaServerId) {
        log.info("[ZLM] [ZLM MEDIA SERVER ID {}] GET PLAYBACK SSRC", mediaServerId);
        return "1" + getSN(mediaServerId);
    }

    /**
     * 获取后四位数SN,随机数
     *
     * @param mediaServerId
     * @return
     */
    private String getSN(String mediaServerId) {
        String key = String.format(RedisConstant.SSRC_INFO, mediaServerId);
        long size = redisUtil.sGetSetSize(key);
        if (size == 0) {
            throw new BusinessException(ResultCodeEnum.SSRC_UN_USABLE);
        }
        // 在集合中移除并返回一个随机成员。
        String sn = (String) redisUtil.setPop(key);
        redisUtil.setRemove(key, sn);
        return sn;
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     * @param ssrc 需要重置的ssrc
     */
    public void releaseSsrc(String mediaServerId, String ssrc) {
        log.info("[ZLM] [ZLM MEDIA SERVER ID {}] RELEASE SSRC", mediaServerId);
        String key = String.format(RedisConstant.SSRC_INFO, mediaServerId);
        String sn = ssrc.substring(1);
        redisUtil.sSet(key, sn);
    }

    private JSONObject postForm(String url, Map<String, Object> param) {
        String s = HttpUtil.post(url, param);
        return JSON.parseObject(s);
    }

    private byte[] downloadBytes(String url) {
        return HttpUtil.downloadBytes(url);
    }

    private String getZLMUrl(MediaServerBO bo, String apiName) {
        return String.format(ZLMConstant.URL_FMT, bo.getIp(), bo.getHttpPort(), apiName);
    }

}
