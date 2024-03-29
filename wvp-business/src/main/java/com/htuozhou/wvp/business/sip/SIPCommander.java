package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.bean.SSRCInfo;
import com.htuozhou.wvp.business.bean.SSRCTransactionInfo;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.dict.InviteSessionTypeDict;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.IStreamSessionService;
import com.htuozhou.wvp.business.zlm.OnStreamChangedHookSubscribe;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribe;
import com.htuozhou.wvp.business.zlm.ZlmHttpHookSubscribeFactory;
import com.htuozhou.wvp.common.constant.SIPConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/21
 */
@Component
@Slf4j
public class SIPCommander {

    @Autowired
    private SIPRunner sipRunner;

    @Autowired
    private SIPProperties sipProperties;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SIPRequestHeaderProvider sipRequestHeaderProvider;

    @Autowired
    private ZlmHttpHookSubscribe zlmHttpHookSubscribe;

    @Autowired
    private IStreamSessionService streamSessionService;

    /**
     * 查询设备信息
     *
     * @param deviceBO
     */
    public void deviceInfoQuery(DeviceBO deviceBO) throws Exception {
        StringBuffer catalogXml = new StringBuffer(200);
        String charset = deviceBO.getCharset();
        catalogXml.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("<CmdType>DeviceInfo</CmdType>\r\n");
        catalogXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        catalogXml.append("<DeviceID>" + deviceBO.getDeviceId() + "</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");

        String time = Long.toString(System.currentTimeMillis());
        CallIdHeader callIdHeader = sipSender.getCallIdHeader(deviceBO.getIp(), deviceBO.getTransport());
        Request request = sipRequestHeaderProvider.createRequest(Request.MESSAGE, deviceBO, null, catalogXml.toString(), SIPConstant.BRANCH + time, time, null, callIdHeader, "MANSCDP+xml");
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER MESSAGE] [SIP ADDRESS:{}] 查询设备信息", deviceBO.getAddress());
    }

    /**
     * 查询设备通道信息
     *
     * @param deviceBO
     */
    public void catalogQuery(DeviceBO deviceBO) throws Exception {
        StringBuffer catalogXml = new StringBuffer(200);
        String charset = deviceBO.getCharset();
        catalogXml.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("  <CmdType>Catalog</CmdType>\r\n");
        catalogXml.append("  <SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        catalogXml.append("  <DeviceID>" + deviceBO.getDeviceId() + "</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");

        String time = Long.toString(System.currentTimeMillis());
        CallIdHeader callIdHeader = sipSender.getCallIdHeader(deviceBO.getIp(), deviceBO.getTransport());
        Request request = sipRequestHeaderProvider.createRequest(Request.MESSAGE, deviceBO, null, catalogXml.toString(), SIPConstant.BRANCH + time, time, null, callIdHeader, "MANSCDP+xml");
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER MESSAGE] [SIP ADDRESS:{}] 查询设备通道信息", deviceBO.getAddress());
    }

    /**
     * BYE
     *
     * @param requestType
     * @param deviceBO
     * @param channelId
     * @throws Exception
     */
    public void streamByeCmd(String requestType, DeviceBO deviceBO, String channelId) throws Exception {
        SSRCTransactionInfo ssrcTransaction = streamSessionService.getSSRCTransactionInfo(deviceBO.getDeviceId(), channelId);
        if (Objects.nonNull(ssrcTransaction)) {
            String time = Long.toString(System.currentTimeMillis());
            CallIdHeader callIdHeader = sipRunner.getSipFactory().createHeaderFactory().createCallIdHeader(ssrcTransaction.getCallId());
            Request request = sipRequestHeaderProvider.createRequest(requestType, deviceBO, channelId, null, SIPConstant.BRANCH + time, time, null, callIdHeader, null);
            sipSender.transmitRequest(sipProperties.getIp(), request);
            log.info("[SIP COMMANDER] [SIP ADDRESS:{}] BYE", deviceBO.getAddress());

            streamSessionService.remove(deviceBO.getDeviceId(), channelId);
        }
    }

    /**
     * 请求预览视频流
     *
     * @param mediaServerBO
     * @param ssrcInfo
     * @param deviceBO
     * @param channelId
     * @param event
     */
    public void playStreamCmd(MediaServerBO mediaServerBO, SSRCInfo ssrcInfo, DeviceBO deviceBO, String channelId,
                              ZlmHttpHookSubscribe.Event event) throws Exception {
        String streamId = ssrcInfo.getStreamId();
        String sdpIp = mediaServerBO.getSdpIp();

        if (Objects.nonNull(event)) {
            log.info("[国标设备点播,添加ON_STREAM_CHANGED订阅] deviceId:{},channelId:{}", deviceBO.getDeviceId(), channelId);
            OnStreamChangedHookSubscribe hookSubscribe = ZlmHttpHookSubscribeFactory.onStreamChanged("rtp", streamId, Boolean.TRUE, "rtsp", mediaServerBO.getMediaServerId());
            zlmHttpHookSubscribe.addSubscribe(hookSubscribe, (bo, param) -> {
                event.response(bo, param);
                zlmHttpHookSubscribe.removeSubscribe(hookSubscribe);
            });
        }

        StringBuilder content = new StringBuilder(200);
        content.append("v=0\r\n");
        content.append("o=").append(channelId).append(" 0 0 IN IP4 ").append(sdpIp).append("\r\n");
        content.append("s=Play\r\n");
        content.append("c=IN IP4 ").append(sdpIp).append("\r\n");
        content.append("t=0 0\r\n");

        if ("TCP-PASSIVE".equalsIgnoreCase(deviceBO.getStreamMode()) || "TCP-ACTIVE".equalsIgnoreCase(deviceBO.getStreamMode())) {
            content.append("m=video ").append(ssrcInfo.getPort()).append(" TCP/RTP/AVP 96 97 98 99\r\n");
        } else {
            content.append("m=video ").append(ssrcInfo.getPort()).append(" RTP/AVP 96 97 98 99\r\n");
        }
        content.append("a=recvonly\r\n");
        content.append("a=rtpmap:96 PS/90000\r\n");
        content.append("a=rtpmap:98 H264/90000\r\n");
        content.append("a=rtpmap:97 MPEG4/90000\r\n");
        content.append("a=rtpmap:99 H265/90000\r\n");
        if ("TCP-PASSIVE".equalsIgnoreCase(deviceBO.getStreamMode())) { // tcp被动模式
            content.append("a=setup:passive\r\n");
            content.append("a=connection:new\r\n");
        } else if ("TCP-ACTIVE".equalsIgnoreCase(deviceBO.getStreamMode())) { // tcp主动模式
            content.append("a=setup:active\r\n");
            content.append("a=connection:new\r\n");
        }

        content.append("y=").append(ssrcInfo.getSsrc()).append("\r\n");// ssrc
        // f字段:f= v/编码格式/分辨率/帧率/码率类型/码率大小a/编码格式/码率大小/采样率
        // content.append("f=v/2/5/25/1/4000a/1/8/1" + "\r\n"); // 未发现支持此特性的设备
        String time = Long.toString(System.currentTimeMillis());
        CallIdHeader callIdHeader = sipSender.getCallIdHeader(deviceBO.getIp(), deviceBO.getTransport());
        Request request = sipRequestHeaderProvider.createRequest(Request.INVITE, deviceBO, channelId, content.toString(), SIPConstant.BRANCH + time, time, null, callIdHeader, "sdp");
        sipSender.transmitRequest(sipProperties.getIp(), request);
        log.info("[SIP COMMANDER] [SIP ADDRESS:{}] 请求预览视频流", deviceBO.getAddress());

        SSRCTransactionInfo ssrcTransaction = new SSRCTransactionInfo();
        ssrcTransaction.setMediaServerId(mediaServerBO.getMediaServerId());
        ssrcTransaction.setInviteSessionTypeDict(InviteSessionTypeDict.PLAY);
        ssrcTransaction.setDeviceId(deviceBO.getDeviceId());
        ssrcTransaction.setChannelId(channelId);
        ssrcTransaction.setSsrc(ssrcInfo.getSsrc());
        ssrcTransaction.setCallId(callIdHeader.getCallId());
        streamSessionService.put(deviceBO.getDeviceId(), channelId, ssrcTransaction);
    }

}
