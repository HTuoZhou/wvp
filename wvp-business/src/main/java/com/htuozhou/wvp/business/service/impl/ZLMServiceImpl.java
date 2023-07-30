package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.common.constant.DynamicTaskConstant;
import com.htuozhou.wvp.persistence.po.MediaServerPO;
import com.htuozhou.wvp.persistence.service.IMediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/4/14
 */
@Service
@Slf4j
public class ZLMServiceImpl implements IZLMService {

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZLMProperties zlmProperties;

    @Autowired
    private ZLMManager zlmManager;

    @Autowired
    private DynamicTask dynamicTask;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveZlmServer() {
        MediaServerPO po = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getDefaultServer, 1));
        MediaServerBO bo = Objects.isNull(po) ? new MediaServerBO() : MediaServerBO.po2bo(po);
        zlmProperties.properties2bo(bo);

        mediaServerService.saveOrUpdate(bo.bo2po());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void online(String mediaServerId) {
        mediaServerService.update(Wrappers.<MediaServerPO>lambdaUpdate()
                .eq(MediaServerPO::getMediaServerId,mediaServerId)
                .set(MediaServerPO::getStatus, 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setKeepAliveTime(String mediaServerId) {
        mediaServerService.update(Wrappers.<MediaServerPO>lambdaUpdate()
                .eq(MediaServerPO::getMediaServerId,mediaServerId)
                .set(MediaServerPO::getStatus, 1)
                .set(MediaServerPO::getHookAliveTime, LocalDateTime.now()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(String mediaServerId) {
        if (!zlmManager.getServerConfig()){
            log.info("[ZLM MEDIA SERVER ID:{}] 心跳检测离线", zlmProperties.getMediaServerId());

            mediaServerService.update(Wrappers.<MediaServerPO>lambdaUpdate()
                    .eq(MediaServerPO::getMediaServerId,mediaServerId)
                    .set(MediaServerPO::getStatus, 0));
        } else {
            String key = String.format(DynamicTaskConstant.ZLM_STATUS, mediaServerId);
            dynamicTask.startDelay(key, () -> offline(mediaServerId), zlmProperties.getHookAliveInterval() + 5);
        }
    }

    /**
     * 获取流媒体服务列表
     * @return
     */
    @Override
    public List<MediaServerBO> getMediaServerList() {
        List<MediaServerPO> pos = mediaServerService.list(Wrappers.emptyWrapper());
        if (CollectionUtil.isEmpty(pos)) {
            return Collections.emptyList();
        }

        return pos.stream().map(MediaServerBO::po2bo).collect(Collectors.toList());
    }
}
