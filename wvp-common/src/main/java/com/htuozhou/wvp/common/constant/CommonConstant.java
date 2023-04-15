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

}
