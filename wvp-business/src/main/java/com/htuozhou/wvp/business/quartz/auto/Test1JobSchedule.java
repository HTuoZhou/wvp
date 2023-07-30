package com.htuozhou.wvp.business.quartz.auto;

import org.springframework.context.annotation.Configuration;

/**
 * @author hanzai
 * @date 2023/2/16
 */
@Configuration
public class Test1JobSchedule {
    // @Bean
    // public JobDetail jobDetail() {
    //     return JobBuilder.newJob(Test1Job.class)
    //             .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, QuartzConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.TEST1_GROUP))
    //             .setJobData(new JobDataMap())
    //             .storeDurably()
    //             .build();
    // }
    //
    // @Bean
    // public Trigger trigger() {
    //     return TriggerBuilder.newTrigger()
    //             .forJob(jobDetail())
    //             .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.TEST1_GROUP))
    //             .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(QuartzConstant.TEST1_INTERVAL).repeatForever())
    //             .build();
    // }

    // @Bean
    // public Trigger trigger() {
    //     return TriggerBuilder.newTrigger()
    //             .forJob(jobDetail())
    //             .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, QuartzConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.TEST1_GROUP))
    //             .withSchedule(CronScheduleBuilder.cronSchedule(QuartzConstant.TEST1_CRON))
    //             .build();
    // }

}
