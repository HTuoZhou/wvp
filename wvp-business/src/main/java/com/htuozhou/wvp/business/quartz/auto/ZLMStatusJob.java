package com.htuozhou.wvp.business.quartz.auto;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.persistence.po.ZlmServerPO;
import com.htuozhou.wvp.persistence.service.IZlmServerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Slf4j
public class ZLMStatusJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        IZlmServerService zlmServerService = (IZlmServerService) jobExecutionContext.getJobDetail().getJobDataMap().get("zlmServerService");
        ZLMManager zlmManager = (ZLMManager) jobExecutionContext.getJobDetail().getJobDataMap().get("zlmManager");
        ZLMProperties zlmProperties = (ZLMProperties) jobExecutionContext.getJobDetail().getJobDataMap().get("zlmProperties");

        if (!zlmManager.getServerConfig()) {
            log.info("[ZLM] [ZLM ID:{}] 心跳检测离线",zlmProperties.getUniqueId());
            zlmServerService.update(Wrappers.<ZlmServerPO>lambdaUpdate()
                    .eq(ZlmServerPO::getUniqueId,zlmProperties.getUniqueId())
                    .set(ZlmServerPO::getStatus,0));
        } else {
            log.info("[ZLM UniqueId:{}] 心跳检测在线",zlmProperties.getUniqueId());
        }
    }
}
