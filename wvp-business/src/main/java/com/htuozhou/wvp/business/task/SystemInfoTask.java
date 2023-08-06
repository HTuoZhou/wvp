package com.htuozhou.wvp.business.task;

import com.htuozhou.wvp.common.constant.QuartzConstant;
import com.htuozhou.wvp.persistence.service.cache.ISystemInfoCacheService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Configuration
public class SystemInfoTask {

    @Autowired
    private ISystemInfoCacheService systemInfoCacheService;

    @Bean
    public JobDetail jobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("systemInfoCacheService", systemInfoCacheService);
        return JobBuilder.newJob(SystemInfoJob.class)
                .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, QuartzConstant.SYSTEM_INFO_JOB),
                        String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.SYSTEM_INFO_GROUP))
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzConstant.SYSTEM_INFO_JOB),
                        String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.SYSTEM_INFO_GROUP))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(QuartzConstant.SYSTEM_INFO_INTERVAL).repeatForever())
                .build();
    }

}
