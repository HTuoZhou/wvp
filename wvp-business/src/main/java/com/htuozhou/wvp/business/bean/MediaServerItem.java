package com.htuozhou.wvp.business.bean;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/1
 */
@Data
public class MediaServerItem {

    /**
     *  "api.apiDebug": "1",
     *  "api.defaultSnap": "./www/logo.png",
     *  "api.secret": "50c343b8-463e-4981-94c5-b1fed371e877",
     *  "api.snapRoot": "./www/snap/",
     *  "cluster.origin_url": "",
     *  "cluster.retry_count": "3",
     *  "cluster.timeout_sec": "15",
     *  "ffmpeg.bin": "/usr/bin/ffmpeg",
     *  "ffmpeg.cmd": "%s -re -i %s -c:a aac -strict -2 -ar 44100 -ab 48k -c:v libx264 -f flv %s",
     *  "ffmpeg.log": "./ffmpeg/ffmpeg.log",
     *  "ffmpeg.restart_sec": "0",
     *  "ffmpeg.snap": "%s -rtsp_transport tcp -i %s -y -f mjpeg -t 0.001 %s",
     *  "general.check_nvidia_dev": "1",
     *  "general.enableVhost": "0",
     *  "general.enable_ffmpeg_log": "0",
     *  "general.flowThreshold": "1024",
     *  "general.maxStreamWaitMS": "15000",
     *  "general.mediaServerId": "FQ3TF8yT83wh5Wvz",
     *  "general.mergeWriteMS": "0",
     *  "general.resetWhenRePlay": "1",
     *  "general.streamNoneReaderDelayMS": "20000",
     *  "general.unready_frame_cache": "100",
     *  "general.wait_add_track_ms": "3000",
     *  "general.wait_track_ready_ms": "10000",
     *  "hls.broadcastRecordTs": "0",
     *  "hls.deleteDelaySec": "10",
     *  "hls.fileBufSize": "65536",
     *  "hls.segDur": "2",
     *  "hls.segKeep": "0",
     *  "hls.segNum": "3",
     *  "hls.segRetain": "5",
     *  "hook.alive_interval": "60",
     *  "hook.enable": "1",
     *  "hook.on_flow_report": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_flow_report",
     *  "hook.on_http_access": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_http_access",
     *  "hook.on_play": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_play",
     *  "hook.on_publish": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_publish",
     *  "hook.on_record_mp4": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_record_mp4",
     *  "hook.on_record_ts": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_record_ts",
     *  "hook.on_rtp_server_timeout": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_rtp_server_timeout",
     *  "hook.on_rtsp_auth": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_rtsp_auth",
     *  "hook.on_rtsp_realm": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_rtsp_realm",
     *  "hook.on_send_rtp_stopped": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_send_rtp_stopped",
     *  "hook.on_server_exited": "",
     *  "hook.on_server_keepalive": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_server_keepalive",
     *  "hook.on_server_started": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_server_started",
     *  "hook.on_shell_login": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_shell_login",
     *  "hook.on_stream_changed": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_stream_changed",
     *  "hook.on_stream_none_reader": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_stream_none_reader",
     *  "hook.on_stream_not_found": "http://192.168.31.193:7452/wvp/zlm/index/hook/on_stream_not_found",
     *  "hook.retry": "1",
     *  "hook.retry_delay": "3.000000",
     *  "hook.timeoutSec": "20",
     *  "http.allow_cross_domains": "1",
     *  "http.allow_ip_range": "127.0.0.1,172.16.0.0-172.31.255.255,192.168.0.0-192.168.255.255",
     *  "http.charSet": "utf-8",
     *  "http.dirMenu": "1",
     *  "http.forbidCacheSuffix": "",
     *  "http.forwarded_ip_header": "",
     *  "http.keepAliveSecond": "15",
     *  "http.maxReqSize": "40960",
     *  "http.notFound": "<html><head><title>404 Not Found</title></head><body bgcolor=\"white\"><center><h1>您访问的资源不存在！</h1></center><hr><center>ZLMediaKit(git hash:b9af556/2023-07-28T14:51:13+08:00,branch:master,build time:2023-07-28T07:06:33)</center></body></html>",
     *  "http.port": "80",
     *  "http.rootPath": "./www",
     *  "http.sendBufSize": "65536",
     *  "http.sslport": "443",
     *  "http.virtualPath": "",
     *  "multicast.addrMax": "239.255.255.255",
     *  "multicast.addrMin": "239.0.0.0",
     *  "multicast.udpTTL": "64",
     *  "protocol.add_mute_audio": "1",
     *  "protocol.auto_close": "0",
     *  "protocol.continue_push_ms": "3000",
     *  "protocol.enable_audio": "1",
     *  "protocol.enable_fmp4": "1",
     *  "protocol.enable_hls": "1",
     *  "protocol.enable_hls_fmp4": "0",
     *  "protocol.enable_mp4": "0",
     *  "protocol.enable_rtmp": "1",
     *  "protocol.enable_rtsp": "1",
     *  "protocol.enable_ts": "1",
     *  "protocol.fmp4_demand": "0",
     *  "protocol.hls_demand": "0",
     *  "protocol.hls_save_path": "./www",
     *  "protocol.modify_stamp": "2",
     *  "protocol.mp4_as_player": "0",
     *  "protocol.mp4_max_second": "3600",
     *  "protocol.mp4_save_path": "./www",
     *  "protocol.rtmp_demand": "0",
     *  "protocol.rtsp_demand": "0",
     *  "protocol.ts_demand": "0",
     *  "record.appName": "record",
     *  "record.fastStart": "0",
     *  "record.fileBufSize": "65536",
     *  "record.fileRepeat": "0",
     *  "record.sampleMS": "500",
     *  "rtc.externIP": "",
     *  "rtc.port": "0",
     *  "rtc.preferredCodecA": "PCMU,PCMA,opus,mpeg4-generic",
     *  "rtc.preferredCodecV": "H264,H265,AV1,VP9,VP8",
     *  "rtc.rembBitRate": "0",
     *  "rtc.tcpPort": "0",
     *  "rtc.timeoutSec": "15",
     *  "rtmp.handshakeSecond": "15",
     *  "rtmp.keepAliveSecond": "15",
     *  "rtmp.modifyStamp": "0",
     *  "rtmp.port": "1935",
     *  "rtmp.sslport": "19350",
     *  "rtp.audioMtuSize": "600",
     *  "rtp.h264_stap_a": "1",
     *  "rtp.lowLatency": "0",
     *  "rtp.rtpMaxSize": "10",
     *  "rtp.videoMtuSize": "1400",
     *  "rtp_proxy.dumpDir": "",
     *  "rtp_proxy.gop_cache": "1",
     *  "rtp_proxy.h264_pt": "98",
     *  "rtp_proxy.h265_pt": "99",
     *  "rtp_proxy.opus_pt": "100",
     *  "rtp_proxy.port": "10000",
     *  "rtp_proxy.port_range": "30000-30500",
     *  "rtp_proxy.ps_pt": "96",
     *  "rtp_proxy.timeoutSec": "15",
     *  "rtsp.authBasic": "0",
     *  "rtsp.directProxy": "1",
     *  "rtsp.handshakeSecond": "15",
     *  "rtsp.keepAliveSecond": "15",
     *  "rtsp.lowLatency": "0",
     *  "rtsp.port": "554",
     *  "rtsp.rtpTransportType": "-1",
     *  "rtsp.sslport": "332",
     *  "shell.maxReqSize": "1024",
     *  "shell.port": "9000",
     *  "srt.latencyMul": "4",
     *  "srt.pktBufSize": "8192",
     *  "srt.port": "9000",
     *  "srt.timeoutSec": "5"
     */

    private String mediaServerId;
    private String hookAliveInterval;

}
