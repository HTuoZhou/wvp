package com.htuozhou.wvp.persistence.service.cache.impl;

import com.htuozhou.wvp.common.constant.RedisConstant;
import com.htuozhou.wvp.common.utils.DateUtil;
import com.htuozhou.wvp.common.utils.RedisUtil;
import com.htuozhou.wvp.common.utils.SystemInfoUtil;
import com.htuozhou.wvp.persistence.service.cache.ISystemInfoCacheService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Service
@Slf4j
public class SystemInfoCacheServiceImpl implements ISystemInfoCacheService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设置系统信息
     */
    @Override
    @SneakyThrows(Exception.class)
    public void setSystemInfo() {
        double cpuInfo = SystemInfoUtil.getCpuInfo();
        addCpuInfo(cpuInfo);

        double memInfo = SystemInfoUtil.getMemInfo();
        addMemInfo(memInfo);

        Map<String, Double> networkInterfaces = SystemInfoUtil.getNetworkInterfaces();
        addNetInfo(networkInterfaces);

        List<Map<String, Object>> diskInfo =SystemInfoUtil.getDiskInfo();
        addDiskInfo(diskInfo);
    }

    private void addCpuInfo(double cpuInfo) {
        String key = RedisConstant.SYSTEM_INFO_CPU;
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("time", DateUtil.getNow());
        infoMap.put("data", String.valueOf(cpuInfo));
        redisUtil.lSet(key, infoMap);
        // 每秒一个,最多只存30个
        Long size = redisUtil.lGetListSize(key);
        if (size != null && size >= 30) {
            for (int i = 0; i < size - 30; i++) {
                redisUtil.lRemove(key);
            }
        }
    }

    private void addMemInfo(double memInfo) {
        String key = RedisConstant.SYSTEM_INFO_MEM;
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put("time", DateUtil.getNow());
        infoMap.put("data", String.valueOf(memInfo));
        redisUtil.lSet(key, infoMap);
        // 每秒一个,最多只存30个
        Long size = redisUtil.lGetListSize(key);
        if (size != null && size >= 30) {
            for (int i = 0; i < size - 30; i++) {
                redisUtil.lRemove(key);
            }
        }
    }

    private void addNetInfo(Map<String, Double> networkInterfaces) {
        String key = RedisConstant.SYSTEM_INFO_NET;
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("time", DateUtil.getNow());
        for (String netKey : networkInterfaces.keySet()) {
            infoMap.put(netKey, networkInterfaces.get(netKey));
        }
        redisUtil.lSet(key, infoMap);
        // 每秒一个,最多只存30个
        Long size = redisUtil.lGetListSize(key);
        if (size != null && size >= 30) {
            for (int i = 0; i < size - 30; i++) {
                redisUtil.lRemove(key);
            }
        }
    }

    private void addDiskInfo(List<Map<String, Object>> diskInfo) {
        String key = RedisConstant.SYSTEM_INFO_DISK;
        redisUtil.set(key, diskInfo);
    }

}
