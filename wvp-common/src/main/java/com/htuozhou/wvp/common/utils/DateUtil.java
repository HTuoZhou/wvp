package com.htuozhou.wvp.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author hanzai
 * @date 2023/7/29
 */
public class DateUtil {

    /**
     * wvp内部统一时间格式
     */
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String ZONE_STR = "Asia/Shanghai";

    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_STR);

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN, Locale.getDefault()).withZone(ZONE_ID);

    public static String getNow() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        return formatter.format(nowDateTime);
    }

    public static Instant localDateTime2Instant(LocalDateTime localDateTime,Integer seconds){
        return localDateTime.plusSeconds(seconds).atZone(ZONE_ID).toInstant();
    }

}
