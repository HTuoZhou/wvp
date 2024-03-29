package com.htuozhou.wvp.business.zlm;

import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    private ZLMProperties zlmProperties;

    @Autowired
    private ZLMManager zlmManager;

    @Override
    public void run(String... args) throws Exception {
        MediaServerBO bo = Optional.ofNullable(zlmService.getDefaultMediaServer()).orElse(new MediaServerBO());
        BeanUtils.copyProperties(zlmProperties, bo);
        if (zlmManager.setServerConfig(bo)) {
            bo.setStatus(Boolean.TRUE);
            zlmService.saveOrUpdateMediaServer(bo);
            zlmManager.initSsrc(bo.getMediaServerId());
        }

        List<MediaServerBO> bos = zlmService.getMediaServerList();
        for (MediaServerBO mediaServerBO : bos) {
            if (mediaServerBO.getDefaultServer()) {
                continue;
            }
            if (!mediaServerBO.getStatus()) {
                continue;
            }
            zlmService.online(mediaServerBO.getMediaServerId());
            zlmManager.initSsrc(bo.getMediaServerId());
        }
    }
}
