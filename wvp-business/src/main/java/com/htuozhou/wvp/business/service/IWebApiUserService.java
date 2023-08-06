package com.htuozhou.wvp.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.htuozhou.wvp.business.bo.UserBO;
import com.htuozhou.wvp.common.page.PageReq;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public interface IWebApiUserService {

    /**
     * 添加用户信息
     *
     * @param bo
     * @return
     */
    String add(UserBO bo);

    /**
     * 批量添加用户信息
     *
     * @param bos
     * @return
     */
    String addBatch(List<UserBO> bos);

    /**
     * 修改用户信息
     *
     * @param bo
     * @return
     */
    String update(UserBO bo);

    /**
     * 删除用户信息
     *
     * @param id
     * @return
     */
    String delete(Integer id);

    /**
     * 获取所有用户信息
     *
     * @param bo
     * @return
     */
    List<UserBO> list(UserBO bo);

    /**
     * 分页查询所有用户信息
     *
     * @param pageReq
     * @return
     */
    IPage<UserBO> page(PageReq<UserBO> pageReq);

    /**
     * 下载用户信息导入模板
     *
     * @param response
     */
    void download(HttpServletResponse response);

    /**
     * 导入用户信息
     *
     * @param file
     * @return
     */
    String importExcel(MultipartFile file);

    /**
     * 导出用户信息
     *
     * @param response
     */
    void exportExcel(HttpServletResponse response);
}
