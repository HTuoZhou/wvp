package com.htuozhou.wvp.common.constant;

/**
 * @author hanzai
 * @date 2023/2/16
 */
public class QuartzConstant {

    public static final String JOB_NAME_FMT = "JOB_%s";
    public static final String GROUP_NAME_FMT = "GROUP_%s";
    public static final String TRIGGER_NAME_FMT = "TRIGGER_%s";


    public static final String TEST1_JOB = "test1Job";
    public static final String TEST1_GROUP = "test1Group";
    public static final Integer TEST1_INTERVAL = 30;
    public static final String TEST1_CRON = "0/30 * * * * ? ";

    public static final String TEST2_JOB = "test2Job";
    public static final String TEST2_GROUP = "test2Group";
    public static final Integer TEST2_INTERVAL = 30;

    public static final String ZLM_STATUS_JOB = "zlmStatusJob";
    public static final String ZLM_STATUS_GROUP = "zlmStatusGroup";

    public static final String SYSTEM_INFO_JOB = "systemInfoJob";
    public static final String SYSTEM_INFO_GROUP = "systemInfoGroup";
    public static final Integer SYSTEM_INFO_INTERVAL = 3;

}
