package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bean.MediaServerItem;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.business.task.DynamicTask;
import com.htuozhou.wvp.business.zlm.ZLMManager;
import com.htuozhou.wvp.common.constant.DynamicTaskConstant;
import com.htuozhou.wvp.common.exception.BusinessException;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
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
    private DynamicTask dynamicTask;

    @Autowired
    private ZLMManager zlmManager;

    @Autowired
    private SIPProperties sipProperties;

    @Override
    public MediaServerBO getDefaultMediaServer() {
        MediaServerPO po = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getDefaultServer, Boolean.TRUE));
        if (Objects.nonNull(po)) {
            return MediaServerBO.po2bo(po);
        }
        return null;
    }

    @Override
    public MediaServerBO getMediaServer(String mediaServerId) {
        MediaServerPO po = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getMediaServerId, mediaServerId));
        if (Objects.nonNull(po)) {
            return MediaServerBO.po2bo(po);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateMediaServer(MediaServerBO bo) {
        mediaServerService.saveOrUpdate(bo.bo2po());
        refreshKeepAlive(bo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void online(String mediaServerId) {
        MediaServerPO po = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getMediaServerId, mediaServerId));
        po.setStatus(Boolean.TRUE);
        mediaServerService.updateById(po);
        refreshKeepAlive(MediaServerBO.po2bo(po));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setKeepAliveTime(String mediaServerId) {
        MediaServerPO po = mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getMediaServerId, mediaServerId));
        po.setStatus(Boolean.TRUE);
        po.setHookAliveTime(LocalDateTime.now());
        mediaServerService.updateById(po);
        refreshKeepAlive(MediaServerBO.po2bo(po));
    }

    @Override
    public void refreshKeepAlive(MediaServerBO bo) {
        String key = String.format(DynamicTaskConstant.ZLM_STATUS, bo.getMediaServerId());
        dynamicTask.startDelay(key, new ZLMStatusCheckRunner(bo), bo.getHookAliveInterval() + 5);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(String mediaServerId) {
        mediaServerService.update(Wrappers.<MediaServerPO>lambdaUpdate()
                .eq(MediaServerPO::getMediaServerId, mediaServerId)
                .set(MediaServerPO::getStatus, 0));
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

    /**
     * 测试流媒体服务
     * @param ip
     * @param port
     * @param secret
     * @return
     */
    @Override
    public MediaServerBO check(String ip, Integer port, String secret) {
        MediaServerBO bo = new MediaServerBO();

        if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getIp, ip)
                .eq(MediaServerPO::getHttpPort, port)))) {
            throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_EXIST);
        }

        bo.setIp(ip);
        bo.setHttpPort(port);
        bo.setSecret(secret);
        MediaServerItem mediaServerItem = zlmManager.getServerConfig(bo);
        if (Objects.isNull(mediaServerItem)) {
            throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_ERROR);
        }

        if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                .eq(MediaServerPO::getMediaServerId, mediaServerItem.getMediaServerId())))) {
            throw new BusinessException(ResultCodeEnum.ZLM_ID_EXIST);
        }

        bo.setMediaServerId(mediaServerItem.getMediaServerId());
        bo.setStreamIp(ip);
        bo.setSdpIp(ip);
        bo.setHookIp(sipProperties.getIp());
        bo.setHttpSslPort(mediaServerItem.getHttpSslPort());
        bo.setRtspPort(mediaServerItem.getRtspPort());
        bo.setRtspSslPort(mediaServerItem.getRtspSslPort());
        bo.setRtmpPort(mediaServerItem.getRtmpPort());
        bo.setRtmpSslPort(mediaServerItem.getRtmpSslPort());
        bo.setRtpPortRange(mediaServerItem.getRtpPortRange());
        bo.setRtpProxyPort(mediaServerItem.getRtpProxyPort());
        bo.setRtpEnable(Boolean.TRUE);
        bo.setHookAliveInterval(mediaServerItem.getHookAliveInterval());
        bo.setDefaultServer(Boolean.FALSE);
        return bo;
    }

    /**
     * 编辑流媒体服务
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(MediaServerBO bo) {
        if (Objects.isNull(bo.getId())) {
            if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                    .eq(MediaServerPO::getIp, bo.getIp())
                    .eq(MediaServerPO::getHttpPort, bo.getHttpPort())))) {
                throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_EXIST);
            }

            MediaServerItem mediaServerItem = zlmManager.getServerConfig(bo);
            if (Objects.isNull(mediaServerItem)) {
                throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_ERROR);
            }

            if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                    .eq(MediaServerPO::getMediaServerId, mediaServerItem.getMediaServerId())))) {
                throw new BusinessException(ResultCodeEnum.ZLM_ID_EXIST);
            }
        } else {
            if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                    .ne(MediaServerPO::getId, bo.getId())
                    .eq(MediaServerPO::getIp, bo.getIp())
                    .eq(MediaServerPO::getHttpPort, bo.getHttpPort())))) {
                throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_EXIST);
            }

            MediaServerItem mediaServerItem = zlmManager.getServerConfig(bo);
            if (Objects.isNull(mediaServerItem)) {
                throw new BusinessException(ResultCodeEnum.ZLM_CONNECT_ERROR);
            }

            if (Objects.nonNull(mediaServerService.getOne(Wrappers.<MediaServerPO>lambdaQuery()
                    .ne(MediaServerPO::getId, bo.getId())
                    .eq(MediaServerPO::getMediaServerId, mediaServerItem.getMediaServerId())))) {
                throw new BusinessException(ResultCodeEnum.ZLM_ID_EXIST);
            }

        }
        zlmManager.setServerConfig(bo);
        mediaServerService.saveOrUpdate(bo.bo2po());
        refreshKeepAlive(bo);
        return Boolean.TRUE;
    }

    /**
     * 删除流媒体服务
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Integer id) {
        mediaServerService.removeById(id);
        return Boolean.TRUE;
    }

    class ZLMStatusCheckRunner implements Runnable {

        private MediaServerBO bo;

        public ZLMStatusCheckRunner(MediaServerBO bo) {
            this.bo = bo;
        }

        @Override
        public void run() {
            MediaServerItem mediaServerItem = zlmManager.getServerConfig(bo);
            if (Objects.nonNull(mediaServerItem)) {
                refreshKeepAlive(bo);
            } else {
                log.warn("[ZLM MEDIA SERVER ID:{}] 心跳检测离线", bo.getMediaServerId());
                offline(bo.getMediaServerId());
            }
        }
    }
}
