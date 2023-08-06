package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.bean.CivilCodeItem;
import com.htuozhou.wvp.common.constant.SIPConstant;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author hanzai
 * @date 2023/8/6
 */
@Component
@Order(3)
@Slf4j
public class CivilCodeRunner implements CommandLineRunner {

    private final Map<String, CivilCodeItem> civilCodeItemMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource(SIPConstant.CIVIL_CODE_PATH);
        InputStream inputStream = resource.getInputStream();
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
        int index = -1;
        String line;
        while ((line = inputStreamReader.readLine()) != null) {
            index++;
            if (index == 0) {
                continue;
            }
            String[] split = line.split(",");
            CivilCodeItem item = CivilCodeItem.getCivilCodeItem(split);
            civilCodeItemMap.put(item.getCode(), item);
        }
        inputStreamReader.close();
        inputStream.close();
    }

    public CivilCodeItem getParentCode(String code) {
        if (code.length() > 8) {
            return null;
        }
        if (code.length() == 8) {
            String parentCode = code.substring(0, 6);
            return civilCodeItemMap.get(parentCode);
        } else {
            CivilCodeItem item = civilCodeItemMap.get(code);
            if (item == null) {
                return null;
            }
            String parentCode = item.getParentCode();
            if (parentCode == null) {
                return null;
            }
            return civilCodeItemMap.get(parentCode);
        }

    }

}
