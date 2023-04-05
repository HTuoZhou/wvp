package com.htuozhou.wvp.business.quartz.auto;

import com.htuozhou.wvp.business.constant.QuartzJobConstant;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.common.quartz.QuartzConstant;
import com.htuozhou.wvp.persistence.service.IZlmServerService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Configuration
public class ZLMStatusJobSchedule {

    @Autowired
    private IZlmServerService zlmServerService;

    @Autowired
    private ZLMManager zlmManager;

    @Autowired
    private ZLMProperties zlmProperties;

    @Bean
    public JobDetail jobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("zlmServerService",zlmServerService);
        jobDataMap.put("zlmManager",zlmManager);
        jobDataMap.put("zlmProperties",zlmProperties);

        return JobBuilder.newJob(ZLMStatusJob.class)
                .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, QuartzJobConstant.ZLM_STATUS_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.ZLM_STATUS_GROUP))
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzJobConstant.ZLM_STATUS_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.ZLM_STATUS_GROUP))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(zlmProperties.getHookAliveInterval() * 3).repeatForever())
                .build();
    }

}
