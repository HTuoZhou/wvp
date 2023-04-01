package com.htuozhou.wvp.business.quartz.auto;

import com.htuozhou.wvp.business.constant.QuartzJobConstant;
import com.htuozhou.wvp.common.quartz.QuartzConstant;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author hanzai
 * @date 2023/2/16
 */
@Slf4j
public class Test1Job implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("定时任务执行，jobName：{}，jobGroup：{}",String.format(QuartzConstant.JOB_NAME_FMT, QuartzJobConstant.TEST1_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzJobConstant.TEST1_GROUP));
    }

}
