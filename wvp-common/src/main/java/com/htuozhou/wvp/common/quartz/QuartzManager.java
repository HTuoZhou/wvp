package com.htuozhou.wvp.common.quartz;

import com.htuozhou.wvp.common.exception.BusinessException;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/2/15
 */
@Component
@Slf4j
public class QuartzManager {

    @Autowired
    private Scheduler scheduler;

    public  void addJob(SimpleJobModel<?> jobModel) {
        if (existJob(jobModel.getJobName(), jobModel.getJobGroup())) {
            log.error("当前定时任务已存在，jobName：{}，jobGroup：{}",String.format(QuartzConstant.JOB_NAME_FMT, jobModel.getJobName()),String.format(QuartzConstant.GROUP_NAME_FMT, jobModel.getJobGroup()));
            throw new BusinessException(ResultCodeEnum.QUARTZ_JOB_EXIST);
        }

        JobDetail jobDetail = JobBuilder.newJob(jobModel.getJobCls())
                .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, jobModel.getJobName()), String.format(QuartzConstant.GROUP_NAME_FMT, jobModel.getJobGroup()))
                .setJobData(jobModel.getJobDataMap())
                .storeDurably()
                .build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, jobModel.getJobName()), String.format(QuartzConstant.GROUP_NAME_FMT, jobModel.getJobGroup()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(jobModel.getFrequency()).repeatForever())
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

    }

    public  void addJob(CronJobModel<?> jobModel) {
        JobDetail jobDetail = JobBuilder.newJob(jobModel.getJobCls())
                .withIdentity(String.format(QuartzConstant.JOB_NAME_FMT, jobModel.getJobName()), String.format(QuartzConstant.GROUP_NAME_FMT, jobModel.getJobGroup()))
                .setJobData(jobModel.getJobDataMap())
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.format(QuartzConstant.TRIGGER_NAME_FMT, jobModel.getJobName()), String.format(QuartzConstant.GROUP_NAME_FMT, jobModel.getJobGroup()))
                .withSchedule(CronScheduleBuilder.cronSchedule(jobModel.getCron()))
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }


    public void deleteJob(String jobName, String groupName) {
        JobKey jobKey = new JobKey(String.format(QuartzConstant.JOB_NAME_FMT, jobName), String.format(QuartzConstant.GROUP_NAME_FMT, groupName));
        TriggerKey triggerKey = new TriggerKey(String.format(QuartzConstant.TRIGGER_NAME_FMT, jobName), String.format(QuartzConstant.GROUP_NAME_FMT, groupName));
        try {
            //停止触发器
            scheduler.pauseTrigger(triggerKey);
            //停止任务
            scheduler.pauseJob(jobKey);
            //移除触发器
            scheduler.unscheduleJob(triggerKey);
            //删除任务
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean existJob(String jobName, String groupName) {
        JobKey jobKey = new JobKey(String.format(QuartzConstant.JOB_NAME_FMT, jobName), String.format(QuartzConstant.GROUP_NAME_FMT, groupName));
        try {
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
