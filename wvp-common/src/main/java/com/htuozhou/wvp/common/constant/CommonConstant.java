package com.htuozhou.wvp.common.constant;

/**
 * @author hanzai
 * @date 2023/4/1
 */
public class CommonConstant {

    /**
     * 项目访问端口号
     */
    public static final String SERVER_PORT = "server.port";

    /**
     * 项目访问路径
     */
    public static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";

    /**
     * 项目应用名称
     */
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";

    /**
     * 默认本地地址
     */
    public static final String DEFAULT_LOCAL_IP = "127.0.0.1";

    /**
     * 当前服务器操作系统是否为linux
     */
    public static final boolean LINUX_OS = System.getProperty("os.name").toLowerCase().contains("linux");

    public static final String NULL_STR = "null";

    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTPS_PROTOCOL = "https://";
    public static final String WS_PROTOCOL = "ws://";
    public static final String WSSS_PROTOCOL = "wss://";
    public static final String RTMP_PROTOCOL = "rtmp://";
    public static final String RTMPS_PROTOCOL = "rtmps://";
    public static final String RTSP_PROTOCOL = "rtsp://";
    public static final String RTSPS_PROTOCOL = "rtsps://";

    public static final String SNAP_PATH = "snap";
    public static final String SNAP_NAME = "%s_%s_snap.jpg";

}
