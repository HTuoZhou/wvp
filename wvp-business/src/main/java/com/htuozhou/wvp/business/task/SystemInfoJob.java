package com.htuozhou.wvp.business.task;

import com.htuozhou.wvp.common.constant.QuartzConstant;
import com.htuozhou.wvp.persistence.service.cache.ISystemInfoCacheService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Slf4j
public class SystemInfoJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.debug("定时任务执行,jobName：{},jobGroup：{}",
                String.format(QuartzConstant.JOB_NAME_FMT, QuartzConstant.SYSTEM_INFO_JOB),
                String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.SYSTEM_INFO_GROUP));

        ISystemInfoCacheService systemInfoCacheService = (ISystemInfoCacheService) jobExecutionContext.getJobDetail()
                .getJobDataMap().get("systemInfoCacheService");

        systemInfoCacheService.setSystemInfo();
    }

}
