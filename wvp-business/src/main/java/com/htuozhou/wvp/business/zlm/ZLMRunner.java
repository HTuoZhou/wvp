package com.htuozhou.wvp.business.zlm;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.persistence.po.ZlmServerPO;
import com.htuozhou.wvp.persistence.service.IZlmServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Component
@Slf4j
public class ZLMRunner implements CommandLineRunner {

    @Autowired
    private ZLMProperties zlmProperties;

    @Autowired
    private IZlmServerService zlmServerService;

    @Autowired
    private ZLMManager zlmManager;

    @Override
    public void run(String... args) throws Exception {
        ZlmServerPO zlmServerPO = zlmServerService.getOne(Wrappers.<ZlmServerPO>lambdaQuery()
                .eq(ZlmServerPO::getDefaultServer, 1));
        zlmServerService.saveOrUpdate(zlmProperties.properties2po(zlmServerPO));

        zlmManager.setServerConfig();
    }
}
