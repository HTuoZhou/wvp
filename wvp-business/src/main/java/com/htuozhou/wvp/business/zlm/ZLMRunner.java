package com.htuozhou.wvp.business.zlm;

import com.htuozhou.wvp.business.service.IZLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Component
@Order(1)
@Slf4j
public class ZLMRunner implements CommandLineRunner {

    @Autowired
    private IZLMService zlmService;

    @Autowired
    private ZLMManager zlmManager;

    @Override
    public void run(String... args) throws Exception {
        zlmService.saveZlmServer();

        zlmManager.setServerConfig();
    }
}
