package com.htuozhou.wvp.business.sip;

import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sip.DialogTerminatedEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hanzai
 * @date 2023/4/12
 */
@Component
@Slf4j
public class SIPSubscribe {

    private Map<String, SIPSubscribe.Event> errorSubscribes = new ConcurrentHashMap<>();

    private Map<String, SIPSubscribe.Event> okSubscribes = new ConcurrentHashMap<>();

    private Map<String, Instant> okTimeSubscribes = new ConcurrentHashMap<>();

    private Map<String, Instant> errorTimeSubscribes = new ConcurrentHashMap<>();

    // 每5分钟执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        log.info("[定时任务] 清理过期的SIP订阅信息");
        Instant instant = Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(5));

        for (String key : okTimeSubscribes.keySet()) {
            if (okTimeSubscribes.get(key).isBefore(instant)) {
                okSubscribes.remove(key);
                okTimeSubscribes.remove(key);
            }
        }
        for (String key : errorTimeSubscribes.keySet()) {
            if (errorTimeSubscribes.get(key).isBefore(instant)) {
                errorSubscribes.remove(key);
                errorTimeSubscribes.remove(key);
            }
        }
    }

    public void addErrorSubscribe(String key, SIPSubscribe.Event event) {
        errorSubscribes.put(key, event);
        errorTimeSubscribes.put(key, Instant.now());
    }

    public void addOkSubscribe(String key, SIPSubscribe.Event event) {
        okSubscribes.put(key, event);
        okTimeSubscribes.put(key, Instant.now());
    }

    public SIPSubscribe.Event getErrorSubscribe(String key) {
        return errorSubscribes.get(key);
    }

    public void removeErrorSubscribe(String key) {
        if (key == null) {
            return;
        }
        errorSubscribes.remove(key);
        errorTimeSubscribes.remove(key);
    }

    public SIPSubscribe.Event getOkSubscribe(String key) {
        return okSubscribes.get(key);
    }

    public void removeOkSubscribe(String key) {
        if (key == null) {
            return;
        }
        okSubscribes.remove(key);
        okTimeSubscribes.remove(key);
    }

    public int getErrorSubscribesSize() {
        return errorSubscribes.size();
    }

    public int getOkSubscribesSize() {
        return okSubscribes.size();
    }

    public enum EventResultType {
        // 超时
        TIMEOUT,
        // 回复
        RESPONSE,
        // 事务已结束
        TRANSACTION_TERMINATED,
        // 会话已结束
        DIALOG_TERMINATED,
        // 设备未找到
        DEVICE_NOT_FOUND,
        // 命令发送失败
        CMD_SEND_FAIL
    }

    public interface Event {
        void response(EventResult eventResult);
    }

    public static class EventResult<EventObject> {
        public Integer statusCode;
        public EventResultType type;
        public String msg;
        public String callId;
        public EventObject event;

        public EventResult() {
        }

        public EventResult(EventObject event) {
            this.event = event;
            if (event instanceof ResponseEvent) {
                ResponseEvent responseEvent = (ResponseEvent) event;
                Response response = responseEvent.getResponse();
                this.type = EventResultType.RESPONSE;
                if (response != null) {
                    this.msg = response.getReasonPhrase();
                    this.statusCode = response.getStatusCode();
                }
                this.callId = ((CallIdHeader) response.getHeader(CallIdHeader.NAME)).getCallId();

            } else if (event instanceof TimeoutEvent) {
                TimeoutEvent timeoutEvent = (TimeoutEvent) event;
                this.type = EventResultType.TIMEOUT;
                this.msg = "消息超时未回复";
                this.statusCode = -1024;
                if (timeoutEvent.isServerTransaction()) {
                    this.callId = ((SIPRequest) timeoutEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
                } else {
                    this.callId = ((SIPRequest) timeoutEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
                }
            } else if (event instanceof TransactionTerminatedEvent) {
                TransactionTerminatedEvent transactionTerminatedEvent = (TransactionTerminatedEvent) event;
                this.type = EventResultType.TRANSACTION_TERMINATED;
                this.msg = "事务已结束";
                this.statusCode = -1024;
                if (transactionTerminatedEvent.isServerTransaction()) {
                    this.callId = ((SIPRequest) transactionTerminatedEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
                } else {
                    this.callId = ((SIPRequest) transactionTerminatedEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
                }
            } else if (event instanceof DialogTerminatedEvent) {
                DialogTerminatedEvent dialogTerminatedEvent = (DialogTerminatedEvent) event;
                this.type = EventResultType.DIALOG_TERMINATED;
                this.msg = "会话已结束";
                this.statusCode = -1024;
                this.callId = dialogTerminatedEvent.getDialog().getCallId().getCallId();
            }
        }
    }

}


