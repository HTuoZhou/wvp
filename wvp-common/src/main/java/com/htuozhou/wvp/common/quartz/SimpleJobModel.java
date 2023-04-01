package com.htuozhou.wvp.common.quartz;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * @author hanzai
 * @date 2023/2/15
 */
@Data
public class SimpleJobModel<T extends Job> {

    private Class<T> jobCls;

    private String jobGroup;

    private String jobName;

    private JobDataMap jobDataMap;

    private int frequency;

}
