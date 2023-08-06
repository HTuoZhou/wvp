package com.htuozhou.wvp.webapi.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.htuozhou.wvp.business.bo.UserBO;
import com.htuozhou.wvp.business.service.IWebApiUserService;
import com.htuozhou.wvp.common.aop.WebLog;
import com.htuozhou.wvp.common.page.PageReq;
import com.htuozhou.wvp.common.page.PageResp;
import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.webapi.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@RestController
@RequestMapping("/webapi/user")
public class WebApiUserController {

    @Autowired
    private IWebApiUserService webApiUserService;

    /**
     * 添加用户信息
     *
     * @param vo
     * @return
     */
    @PostMapping("/add")
    @WebLog
    public ApiFinalResult<String> add(@RequestBody UserVO vo) {
        return ApiFinalResult.success(webApiUserService.add(vo.vo2bo()));
    }

    /**
     * 批量添加用户信息
     *
     * @param vos
     * @return
     */
    @PostMapping("/addBatch")
    @WebLog
    public ApiFinalResult<String> addBatch(@RequestBody List<UserVO> vos) {
        List<UserBO> bos = vos.stream().map(UserVO::vo2bo).collect(Collectors.toList());
        return ApiFinalResult.success(webApiUserService.addBatch(bos));
    }

    /**
     * 修改用户信息
     *
     * @param vo
     * @return
     */
    @PutMapping("/update")
    @WebLog
    public ApiFinalResult<String> update(@RequestBody UserVO vo) {
        return ApiFinalResult.success(webApiUserService.update(vo.vo2bo()));
    }

    /**
     * 删除用户信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @WebLog
    public ApiFinalResult<String> delete(@PathVariable("id") Integer id) {
        return ApiFinalResult.success(webApiUserService.delete(id));
    }

    /**
     * 获取所有用户信息
     *
     * @return
     */
    @GetMapping("/list")
    @WebLog
    public ApiFinalResult<List<UserVO>> list(UserVO vo) {
        List<UserBO> bos = webApiUserService.list(vo.vo2bo());
        if (CollUtil.isEmpty(bos)) {
            return ApiFinalResult.success(Collections.emptyList());
        }

        return ApiFinalResult.success(bos.stream().map(UserVO::bo2vo).collect(Collectors.toList()));
    }

    /**
     * 分页查询所有用户信息
     *
     * @param pageReq
     * @return
     */
    @PostMapping("/page")
    @WebLog
    public ApiFinalResult<PageResp<UserVO>> page(@RequestBody PageReq<UserVO> pageReq) {
        IPage<UserBO> pageResp = webApiUserService.page(pageReq.pageVo2Bo(UserVO::vo2bo));
        return ApiFinalResult.success(PageResp.pageBo2Vo(pageResp, UserVO::bo2vo));
    }

    /**
     * 下载用户信息导入模板
     *
     * @param response
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        webApiUserService.download(response);
    }

    /****
     * 导入用户信息
     */
    @PostMapping("/importExcel")
    public ApiFinalResult<String> importExcel(MultipartFile file) {
        return ApiFinalResult.success(webApiUserService.importExcel(file));
    }

    /***
     * 导出用户信息
     */
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) {
        webApiUserService.exportExcel(response);
    }

}
