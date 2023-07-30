package com.htuozhou.wvp.business.properties;

import java.util.Properties;

/**
 * @author hanzai
 * @date 2023/4/5
 */
public class DefaultProperties {

    public static Properties getProperties(String ip, boolean isDebug) {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "WVP_SIP");
        properties.setProperty("javax.sip.IP_ADDRESS", ip);
        properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "off");

        if (isDebug) {
            properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "false");
        }

        // 接收所有notify请求,即使没有订阅
        properties.setProperty("gov.nist.javax.sip.DELIVER_UNSOLICITED_NOTIFY", "true");
        properties.setProperty("gov.nist.javax.sip.AUTOMATIC_DIALOG_ERROR_HANDLING", "false");
        properties.setProperty("gov.nist.javax.sip.CANCEL_CLIENT_TRANSACTION_CHECKED", "false");
        // 为_NULL _对话框传递_终止的_事件
        properties.setProperty("gov.nist.javax.sip.DELIVER_TERMINATED_EVENT_FOR_NULL_DIALOG", "true");
        // 会话清理策略
        properties.setProperty("gov.nist.javax.sip.RELEASE_REFERENCES_STRATEGY", "Normal");
        // 处理由该服务器处理的基于底层TCP的保持生存超时
        properties.setProperty("gov.nist.javax.sip.RELIABLE_CONNECTION_KEEP_ALIVE_TIMEOUT", "60");
        // 获取实际内容长度,不使用header中的长度信息
        properties.setProperty("gov.nist.javax.sip.COMPUTE_CONTENT_LENGTH_FROM_MESSAGE_BODY", "true");

        /**
         * sip_server_log.log 和 sip_debug_log.log ERROR, INFO, WARNING, OFF, DEBUG, TRACE
         */
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "ERROR");

        return properties;
    }

}
