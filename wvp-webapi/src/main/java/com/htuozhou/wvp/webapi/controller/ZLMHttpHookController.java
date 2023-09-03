package com.htuozhou.wvp.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.*;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.enumerate.ZLMHttpHookType;
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribe;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private static final JSONObject ZLM_RES_SUCCESS = new JSONObject();

    static {
        ZLM_RES_SUCCESS.put("code", 0);
        ZLM_RES_SUCCESS.put("msg", "success");
    }

    @Autowired
    private IZLMService zlmService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ZlmHttpHookSubscribe zlmHttpHookSubscribe;

    @Autowired
    private IInviteStreamService inviteStreamService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private ZLMManager zlmManager;

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_server_started")
    public JSONObject onServerStarted(@RequestBody ZLMHttpHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_SERVER_STARTED(服务器启动)", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));

                zlmService.online(param.getMediaServerId());
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    /**
     * 服务器定时上报时间，上报间隔可配置，默认10s上报一次
     *
     * @param param
     * @return
     */
    @PostMapping("/on_server_keepalive")
    public JSONObject onServerKeepAlive(@RequestBody ZLMHttpHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS {}] ON_SERVER_KEEPALIVE(服务器定时上报)", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));

                zlmService.setKeepAliveTime(param.getMediaServerId());
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    /**
     * rtsp/rtmp/rtp推流鉴权事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_publish")
    public JSONObject onPublish(@RequestBody OnPublishHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_PUBLISH(rtsp/rtmp/rtp推流鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
                // 推流鉴权的处理
                if (!Objects.equals("rtp", param.getApp())) {
                    // 推流鉴权
                }
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });

        return ZLM_RES_SUCCESS;
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_stream_changed")
    public JSONObject onStreamChanged(@RequestBody OnStreamChangedHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(param));
                ZlmHttpHookSubscribe.Event subscribe = zlmHttpHookSubscribe.sendNotify(ZLMHttpHookType.on_stream_changed, jsonObject);
                if (subscribe != null) {
                    subscribe.response(bo, param);
                }
                if (param.isRegist()) {
                    log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_CHANGED(流注册),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
                } else {
                    log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_CHANGED(流注销),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

                    if (Objects.equals("rtp", param.getApp()) && Objects.equals("rtsp", param.getSchema())) {
                        InviteInfo inviteInfo = inviteStreamService.getStreamInviteInfo(InviteSessionTypeDict.PLAY, param.getStream());
                        inviteStreamService.removeInviteInfo(inviteInfo);
                        zlmManager.releaseSsrc(bo.getMediaServerId(), inviteInfo.getSsrcInfo().getSsrc());
                        deviceChannelService.update(Wrappers.<DeviceChannelPO>lambdaUpdate()
                                .set(DeviceChannelPO::getStreamId, null)
                                .eq(DeviceChannelPO::getDeviceId, inviteInfo.getDeviceId())
                                .eq(DeviceChannelPO::getChannelId, inviteInfo.getChannelId()));
                    }
                }
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    /**
     * 播放器鉴权事件，rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件； 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。 播放rtsp流时，
     * 如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_play")
    public JSONObject onPlay(@RequestBody OnplayHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_PLAY(播放器鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

                zlmService.online(param.getMediaServerId());
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。 一个直播流注册上线了，如果一直没人观看也会触发一次无人观看事件，触发时的协议schema是随机的，
     * 看哪种协议最晚注册(一般为hls)。 后续从有人观看转为无人观看，触发协议schema为最后一名观看者使用何种协议。 目前mp4/hls录制不当做观看人数(mp4录制可以通过配置文件mp4_as_player控制，
     * 但是rtsp/rtmp/rtp转推算观看人数，也会触发该事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_stream_none_reader")
    public JSONObject onStreamNoneReader(@RequestBody OnStreamNoneReader param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_NONE_READER(流无人观看),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    /**
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_stream_not_found")
    public JSONObject onStreamNotFound(@RequestBody OnStreamNotFoundHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_NOT_FOUND(流未找到),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_send_rtp_stopped")
    public JSONObject onSendRtpStopped(@RequestBody ZLMHttpHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_SEND_RTP_STOPPED", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

    @PostMapping("/on_rtp_server_timeout")
    public JSONObject onRtpServerTimeout(@RequestBody ZLMHttpHookParam param) {
        threadPoolTaskExecutor.execute(() -> {
            MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
            if (Objects.nonNull(bo)) {
                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_RTP_SERVER_TIMEOUT", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()));
            } else {
                log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
            }
        });
        return ZLM_RES_SUCCESS;
    }

}

