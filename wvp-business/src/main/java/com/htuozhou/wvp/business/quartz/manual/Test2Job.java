package com.htuozhou.wvp.business.quartz.manual;

import com.htuozhou.wvp.common.constant.QuartzConstant;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author hanzai
 * @date 2023/2/16
 */
@Slf4j
public class Test2Job implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("定时任务执行，jobName：{}，jobGroup：{}",String.format(QuartzConstant.JOB_NAME_FMT, QuartzConstant.TEST2_JOB),String.format(QuartzConstant.GROUP_NAME_FMT, QuartzConstant.TEST2_GROUP));
    }

}
