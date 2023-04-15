package com.htuozhou.wvp.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.business.bo.ZlmServerBO;
import com.htuozhou.wvp.business.properties.ZLMProperties;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.persistence.po.ZlmServerPO;
import com.htuozhou.wvp.persistence.service.IZlmServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/4/14
 */
@Service
@Slf4j
public class ZLMServiceImpl implements IZLMService {

    @Autowired
    private IZlmServerService zlmServerService;

    @Autowired
    private ZLMProperties zlmProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveZlmServer() {
        ZlmServerPO po = zlmServerService.getOne(Wrappers.<ZlmServerPO>lambdaQuery()
                .eq(ZlmServerPO::getDefaultServer, 1));
        ZlmServerBO bo = Objects.isNull(po) ? new ZlmServerBO() : ZlmServerBO.po2bo(po);
        zlmProperties.properties2bo(bo);

        zlmServerService.saveOrUpdate(bo.bo2po());
    }
}
