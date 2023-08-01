package com.htuozhou.wvp.webapi.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.htuozhou.wvp.business.bo.MediaServerBO;
import com.htuozhou.wvp.business.service.IZLMService;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.webapi.vo.MediaServerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@RestController
@RequestMapping("/webapi/mediaServer")
public class MediaServerController {

    @Autowired
    private IZLMService zlmService;

    /**
     * 获取流媒体服务列表
     * @return
     */
    @GetMapping("/list")
    public ApiFinalResult<List<MediaServerVO>> getMediaServerList(){
        List<MediaServerBO> bos = zlmService.getMediaServerList();
        if (CollectionUtil.isEmpty(bos)){
            return ApiFinalResult.success(Collections.emptyList());
        }

        return ApiFinalResult.success(bos.stream().map(MediaServerVO::bo2vo).collect(Collectors.toList()));
    }



}
