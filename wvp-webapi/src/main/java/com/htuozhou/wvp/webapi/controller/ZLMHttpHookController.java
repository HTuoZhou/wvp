package com.htuozhou.wvp.webapi.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.InviteInfo;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.enumerate.ZLMHttpHookType;
import com.htuozhou.wvp.business.service.IInviteStreamService;
import com.htuozhou.wvp.business.service.IPlayService;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribe;
import com.htuozhou.wvp.business.zlm.param.*;
import com.htuozhou.wvp.business.zlm.result.*;
import com.htuozhou.wvp.common.config.DeferredResultHolder;
import com.htuozhou.wvp.common.constant.DeferredResultConstant;
import com.htuozhou.wvp.common.constant.ZLMConstant;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.common.result.RequestMessage;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.persistence.po.DeviceChannelPO;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceChannelService;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

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

    @Autowired
    private ZlmHttpHookSubscribe zlmHttpHookSubscribe;

    @Autowired
    private IInviteStreamService inviteStreamService;

    @Autowired
    private IDeviceChannelService deviceChannelService;

    @Autowired
    private ZLMManager zlmManager;

    @Autowired
    private DeferredResultHolder resultHolder;

    @Autowired
    private IPlayService playService;

    @Autowired
    private IDeviceService deviceService;

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_server_started")
    public ZLMHttpHookResult onServerStarted(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_SERVER_STARTED(服务器启动),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

            zlmService.online(param.getMediaServerId());
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_SERVER_STARTED(服务器启动),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * 服务器定时上报时间，上报间隔可配置，默认10s上报一次
     *
     * @param param
     * @return
     */
    @PostMapping("/on_server_keepalive")
    public ZLMHttpHookResult onServerKeepAlive(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS {}] ON_SERVER_KEEPALIVE(服务器定时上报),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

            zlmService.setKeepAliveTime(param.getMediaServerId());
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS {}] ON_SERVER_KEEPALIVE(服务器定时上报),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * rtsp/rtmp/rtp推流鉴权事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_publish")
    public OnPublishHookResult onPublish(@RequestBody OnPublishHookParam param) {
        OnPublishHookResult result = OnPublishHookResult.success();

        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_PUBLISH(rtsp/rtmp/rtp推流鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
            // 推流鉴权的处理
            if (!Objects.equals("rtp", param.getApp())) {
                // 推流鉴权
            } else {
            }
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_PUBLISH(rtsp/rtmp/rtp推流鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result);
        return result;
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_stream_changed")
    public OnStreamChangedHookResult onStreamChanged(@RequestBody OnStreamChangedHookParam param) {
        OnStreamChangedHookResult result = OnStreamChangedHookResult.success();

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

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_STREAM_CHANGED,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result);
        return result;
    }

    /**
     * 播放器鉴权事件，rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件； 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。 播放rtsp流时，
     * 如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_play")
    public OnPlayHookResult onPlay(@RequestBody OnplayHookParam param) {
        OnPlayHookResult result = OnPlayHookResult.success();

        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_PLAY(播放器鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_PLAY(播放器鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result);
        return result;
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
    public OnStreamNoneReaderHookResult onStreamNoneReader(@RequestBody OnStreamNoneReaderParam param) {
        OnStreamNoneReaderHookResult result = OnStreamNoneReaderHookResult.success();

        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_NONE_READER(流无人观看),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_STREAM_NONE_READER(流无人观看),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result);
        return result;
    }

    /**
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_stream_not_found")
    public DeferredResult<ZLMHttpHookResult> onStreamNotFound(@RequestBody OnStreamNotFoundHookParam param) {
        DeferredResult<ZLMHttpHookResult> result = new DeferredResult<>();

        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_STREAM_NOT_FOUND(流未找到),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);

            if (Objects.equals("rtp", param.getApp())) {
                String stream = param.getStream();
                String deviceId = stream.split("_")[0];
                String channelId = stream.split("_")[1];

                log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] 预览流未找到,发起自动点播,deviceId:{},channelId:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), deviceId, channelId);

                String key = String.format(DeferredResultConstant.PLAY_CALLBACK, deviceId, channelId);
                result = new DeferredResult<>(DeferredResultConstant.PLAY_TIME_OUT * 1000);

                String uuid = IdUtil.randomUUID();
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setKey(key);
                requestMessage.setId(uuid);

                resultHolder.put(requestMessage.getKey(), requestMessage.getId(), result);

                result.onTimeout(() -> {
                    log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] 预览流未找到,发起自动点播,点播超时,deviceId:{},channelId:{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), deviceId, channelId);
                    requestMessage.setData(ApiFinalResult.error(ResultCodeEnum.GB_DEVICE_PLAY_TIMEOUT));
                    resultHolder.invokeAllResult(requestMessage);
                });

                boolean exist = resultHolder.exist(key, null);
                if (!exist) {
                    DevicePO devicePO = deviceService.getOne(Wrappers.<DevicePO>lambdaQuery()
                            .eq(DevicePO::getDeviceId, deviceId));
                    playService.play(bo, DeviceBO.po2bo(devicePO), channelId, null, uuid, (code, msg, data) -> {
                        requestMessage.setData(new ZLMHttpHookResult(code, msg));
                        resultHolder.invokeResult(requestMessage);
                    });
                }
            }
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_STREAM_NOT_FOUND(流未找到),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), result);
        return result;
    }

    /**
     * 流量统计事件，播放器或推流器断开时并且耗用流量超过特定阈值时会触发此事件，阈值通过配置文件general.flowThreshold配置；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_flow_report")
    public ZLMHttpHookResult onFlowReport(@RequestBody OnFlowReportHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_FLOW_REPORT(流量统计),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_FLOW_REPORT(流量统计),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * 访问http文件服务器上hls之外的文件时触发
     *
     * @param param
     * @return
     */
    @PostMapping("/on_http_access")
    public ZLMHttpHookResult onHttpAccess(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_HTTP_ACCESS(访问http文件服务器上hls之外的文件),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_HTTP_ACCESS(访问http文件服务器上hls之外的文件),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * 录制mp4完成后通知事件；此事件对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_record_mp4")
    public ZLMHttpHookResult onRecordMp4(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_RECORD_MP4(录制mp4完成),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_RECORD_MP4(录制mp4完成),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * 该rtsp流是否开启rtsp专用方式的鉴权事件，开启后才会触发on_rtsp_auth事件。
     * 需要指出的是rtsp也支持url参数鉴权，它支持两种方式鉴权
     *
     * @param param
     * @return
     */
    @PostMapping("/on_rtsp_realm")
    public ZLMHttpHookResult onRtspRealm(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_RTSP_REALM,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_RTSP_REALM,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * rtsp专用的鉴权事件，先触发on_rtsp_realm事件然后才会触发on_rtsp_auth事件
     *
     * @param param
     * @return
     */
    @PostMapping("/on_rtsp_auth")
    public ZLMHttpHookResult onRtspAuth(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_RTSP_AUTH,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_RTSP_AUTH,{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * shell登录鉴权，ZLMediaKit提供简单的telnet调试方式
     * 使用telnet 127.0.0.1 9000能进入MediaServer进程的shell界面
     *
     * @param param
     * @return
     */
    @PostMapping("/on_shell_login")
    public ZLMHttpHookResult onShellLogin(@RequestBody ZLMHttpHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_SHELL_LOGIN(shell登录鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_SHELL_LOGIN(shell登录鉴权),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

    /**
     * 调用openRtpServer 接口，rtp server 长时间未收到数据,执行此web hook,对回复不敏感
     *
     * @param param
     * @return
     */
    @PostMapping("/on_rtp_server_timeout")
    public ZLMHttpHookResult onRtpServerTimeout(@RequestBody OnRtpServerTimeOutHookParam param) {
        MediaServerBO bo = zlmService.getMediaServer(param.getMediaServerId());
        if (Objects.nonNull(bo)) {
            log.info("[ZLM HTTP HOOK] 收到 [ZLM ADDRESS：{}] ON_RTP_SERVER_TIMEOUT(rtp server 长时间未收到数据),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), param);
        } else {
            log.warn("[ZLM HTTP HOOK] [ZLM MEDIA SERVER ID {}] 不存在", param.getMediaServerId());
        }

        log.info("[ZLM HTTP HOOK] 回复 [ZLM ADDRESS：{}] ON_RTP_SERVER_TIMEOUT(rtp server 长时间未收到数据),{}", String.format(ZLMConstant.ADDRESS, bo.getIp(), bo.getHttpPort()), ZLMHttpHookResult.success());
        return ZLMHttpHookResult.success();
    }

}

