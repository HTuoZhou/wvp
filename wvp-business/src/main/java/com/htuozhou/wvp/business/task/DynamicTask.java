package com.htuozhou.wvp.business.task;

import com.htuozhou.wvp.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Component
@Slf4j
public class DynamicTask {

    private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>();
    private final Map<String, Runnable> runnableMap = new ConcurrentHashMap<>();
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 循环执行的任务
     *
     * @param key      任务ID
     * @param task     任务
     * @param interval 间隔 毫秒
     * @return
     */
    public void startCron(String key, Runnable task, long interval) {
        ScheduledFuture<?> future = scheduledFutureMap.get(key);
        if (Objects.nonNull(future)) {
            if (future.isCancelled()) {
                log.debug("任务【{}】已存在,且已关闭", key);
            } else {
                log.debug("任务【{}】已存在,且已启动", key);
                return;
            }
        }

        future = threadPoolTaskScheduler.scheduleAtFixedRate(task, interval);
        log.debug("任务【{}】不存在,准备启动", key);
        scheduledFutureMap.put(key, future);
        runnableMap.put(key, task);
        log.debug("任务【{}】不存在,启动成功", key);
    }

    /**
     * 延时任务
     *
     * @param key   任务ID
     * @param task  任务
     * @param delay 延时s
     * @return
     */
    public void startDelay(String key, Runnable task, int delay) {
        ScheduledFuture<?> future = scheduledFutureMap.get(key);
        if (Objects.nonNull(future)) {
            if (future.isCancelled()) {
                log.debug("任务【{}】已存在,且已关闭", key);
            } else {
                log.debug("任务【{}】已存在,且已启动", key);
                return;
            }
        }

        future = threadPoolTaskScheduler.schedule(task, DateUtil.localDateTime2Instant(LocalDateTime.now(), delay));
        log.debug("任务【{}】不存在,准备启动", key);
        scheduledFutureMap.put(key, future);
        runnableMap.put(key, task);
        log.debug("任务【{}】不存在,启动成功", key);
    }

    public void cancel(String key){
        if (Objects.nonNull(scheduledFutureMap.get(key)) && !scheduledFutureMap.get(key).isCancelled() && !scheduledFutureMap.get(key).isDone()) {
            scheduledFutureMap.get(key).cancel(true);
            scheduledFutureMap.remove(key);
            runnableMap.remove(key);
        }
    }

    public boolean contains(String key) {
        return Objects.nonNull(scheduledFutureMap.get(key));
    }

    public Set<String> getAllKeys() {
        return scheduledFutureMap.keySet();
    }

    public Runnable get(String key) {
        return runnableMap.get(key);
    }

    /**
     * 每五分钟检查失效的任务,并移除
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        if (scheduledFutureMap.size() > 0) {
            for (String key : scheduledFutureMap.keySet()) {
                if (scheduledFutureMap.get(key).isDone() || scheduledFutureMap.get(key).isCancelled()) {
                    scheduledFutureMap.remove(key);
                    runnableMap.remove(key);
                }
            }
        }
    }

    public boolean isAlive(String key) {
        return Objects.nonNull(scheduledFutureMap.get(key)) &&
                !scheduledFutureMap.get(key).isDone() &&
                !scheduledFutureMap.get(key).isCancelled();
    }

}
