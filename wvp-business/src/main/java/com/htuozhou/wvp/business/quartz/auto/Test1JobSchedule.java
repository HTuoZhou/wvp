package com.htuozhou.wvp.business.quartz.auto;

import com.htuozhou.wvp.business.constant.QuartzJobConstant;
import com.htuozhou.wvp.common.quartz.QuartzConstant;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/2/16
 */
@Component
public class Test1JobSchedule {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(Test1Job.class)
                .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, QuartzJobConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.TEST1_GROUP))
                .setJobData(new JobDataMap())
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzJobConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.TEST1_GROUP))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(QuartzJobConstant.TEST1_INTERVAL).repeatForever())
                .build();
    }

    // @Bean
    // public Trigger trigger() {
    //     return TriggerBuilder.newTrigger()
    //             .forJob(jobDetail())
    //             .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzJobConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.TEST1_GROUP))
    //             .withSchedule(CronScheduleBuilder.cronSchedule(QuartzJobConstant.TEST1_CRON))
    //             .build();
    // }

}
