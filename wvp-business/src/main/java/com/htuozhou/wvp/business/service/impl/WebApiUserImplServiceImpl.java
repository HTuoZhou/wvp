package com.htuozhou.wvp.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.htuozhou.wvp.business.bo.UserBO;
import com.htuozhou.wvp.business.bo.easyexcel.UserImportBO;
import com.htuozhou.wvp.business.easyexcel.ImportHeadDict;
import com.htuozhou.wvp.business.easyexcel.TemplateDict;
import com.htuozhou.wvp.business.easyexcel.UserHeadDict;
import com.htuozhou.wvp.business.easyexcel.UserSexDict;
import com.htuozhou.wvp.business.service.IWebApiUserService;
import com.htuozhou.wvp.common.constant.EasyExcelConstant;
import com.htuozhou.wvp.common.easyexcel.ImportDataAssistService;
import com.htuozhou.wvp.common.easyexcel.ImportListener;
import com.htuozhou.wvp.common.easyexcel.ImportWriteHandler;
import com.htuozhou.wvp.common.exception.BusinessException;
import com.htuozhou.wvp.common.page.PageReq;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import com.htuozhou.wvp.common.service.I18nService;
import com.htuozhou.wvp.common.utils.CommonUtil;
import com.htuozhou.wvp.persistence.po.UserPO;
import com.htuozhou.wvp.persistence.service.IUserService;
import com.htuozhou.wvp.persistence.service.cache.IUserCacheService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@Service
@Slf4j
public class WebApiUserImplServiceImpl implements IWebApiUserService, ImportDataAssistService<UserImportBO> {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserCacheService userCacheService;

    @Autowired
    private I18nService i18nService;

    /**
     * 添加用户信息
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(UserBO bo) {
        boolean save = userService.save(bo.bo2po());
        if (save) {
            userCacheService.listEvict();
        }
        return "添加用户信息成功";
    }

    /**
     * 批量添加用户信息
     * @param bos
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addBatch(List<UserBO> bos) {
        List<UserPO> pos = bos.stream().map(UserBO::bo2po).collect(Collectors.toList());
        boolean saveBatch = userService.saveBatch(pos);
        if (saveBatch) {
            userCacheService.listEvict();
        }
        return "批量添加用户信息成功";
    }

    /**
     * 修改用户信息
     * @param bo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String update(UserBO bo) {
        boolean updateById = userService.updateById(bo.bo2po());
        if (updateById) {
            userCacheService.listEvict();
        }
        return "修改用户信息成功";
    }

    /**
     * 删除用户信息
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Integer id) {
        boolean removeById = userService.removeById(id);
        if (removeById) {
            userCacheService.listEvict();
        }
        return "删除用户信息成功";
    }

    /**
     * 获取所有用户信息
     * @param bo
     * @return
     */
    @Override
    public List<UserBO> list(UserBO bo) {
        String name = bo.getName();
        String nickname = bo.getNickname();

        List<UserPO> pos = userCacheService.list();
        if (CollUtil.isEmpty(pos)) {
            return Collections.emptyList();
        }

        if (StringUtils.isNotBlank(name)) {
            pos = pos.stream()
                    .filter((x) -> x.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(nickname)) {
            pos = pos.stream()
                    .filter((x) -> x.getNickname().contains(nickname))
                    .collect(Collectors.toList());
        }

        return pos.stream().map(UserBO::po2bo).collect(Collectors.toList());
    }

    /**
     * 分页查询所有用户信息
     * @param pageReq
     * @return
     */
    @Override
    public IPage<UserBO> page(PageReq<UserBO> pageReq) {
        Integer pageNum = pageReq.getPageNum();
        Integer pageSize = pageReq.getPageSize();

        LambdaQueryWrapper<UserPO> wrapper = Wrappers.<UserPO>lambdaQuery();

        if (Objects.nonNull(pageReq.getQueryParam()) && StringUtils.isNotBlank(pageReq.getQueryParam().getName())) {
            wrapper.like(UserPO::getName,pageReq.getQueryParam().getName());
        }
        if (Objects.nonNull(pageReq.getQueryParam()) && StringUtils.isNotBlank(pageReq.getQueryParam().getNickname())) {
            wrapper.like(UserPO::getNickname,pageReq.getQueryParam().getNickname());
        }

        IPage<UserPO> page = userService.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(UserBO::po2bo);
    }

    /**
     * 下载用户信息导入模板
     * @param response
     */
    @Override
    @SneakyThrows(Exception.class)
    public void download(HttpServletResponse response) {
        OutputStream os = response.getOutputStream();
        String fileName = new String((CommonUtil.getI18nMsg(TemplateDict.USER_IMPORT_TEMPLATE_NAME, i18nService)).getBytes(EasyExcelConstant.CHARSET_UTF), EasyExcelConstant.CHARSET_ISO);
        CommonUtil.setResponse(response, fileName);

        // 下拉框
        Map<Integer, String[]> mapDropDown = new HashMap<>();
        String male = CommonUtil.getI18nMsg(UserSexDict.USER_SEX_MALE, i18nService);
        String female = CommonUtil.getI18nMsg(UserSexDict.USER_SEX_FEMALE, i18nService);
        mapDropDown.put(2, new String[]{male,female});

        EasyExcel.write(os, UserImportBO.class)
                .head(head())
                .registerWriteHandler(new ImportWriteHandler(CommonUtil.getI18nMsg(TemplateDict.TEMPLATE_DECRIPTION, i18nService),mapDropDown, head().size() - 1))
                .sheet(CommonUtil.getI18nMsg(TemplateDict.USER_DETAIL, i18nService))
                .relativeHeadRowIndex(1)
                .doWrite(Collections.emptyList());
    }

    /**
     * 导入用户信息
     * @param file
     * @return
     */
    @Override
    @SneakyThrows(Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public String importExcel(MultipartFile file) {
        List<UserPO> pos = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        ImportListener<UserImportBO> listener = new ImportListener<>(this);
        EasyExcel.read(inputStream, UserImportBO.class, listener).headRowNumber(2).sheet().doRead();

        Boolean head = listener.getHead();
        if (Objects.equals(head,Boolean.FALSE)) {
            throw new BusinessException(ResultCodeEnum.IMPORT_TEMPLATE_ERROR);
        }

        StringBuilder errorMsg = listener.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new BusinessException(ResultCodeEnum.IMPORT_DATA_ERROR,errorMsg);
        }

        List<UserImportBO> bos = listener.getBos();
        if (CollUtil.isNotEmpty(bos)) {
            checkDuplicate(bos);
        }

        for (int i = 0; i < bos.size(); i++) {
            UserImportBO bo = bos.get(i);
            String name = bo.getName();
            if (Objects.nonNull(userService.getOne(Wrappers.<UserPO>lambdaQuery().eq(UserPO::getName, name)))) {
                throw new BusinessException(ResultCodeEnum.IMPORT_DATA_ERROR,formatImportMsg(i18nService, ImportHeadDict.USER_NAME_EXIST, (i+3)));
            } else {
                UserPO po = bo.importBo2po(i18nService);
                pos.add(po);
            }
        }

        userService.saveBatch(pos);
        return "导入用户信息成功";
    }

    @Override
    @SneakyThrows(Exception.class)
    public void exportExcel(HttpServletResponse response) {
        List<UserImportBO> bos = new ArrayList<>();

        List<UserPO> pos = userService.list(Wrappers.emptyWrapper());

        if (CollectionUtils.isEmpty(pos)) {
            bos = Collections.emptyList();
        }

        bos = pos.stream().map(po -> UserImportBO.po2ImportBo(po,i18nService)).collect(Collectors.toList());

        OutputStream os = response.getOutputStream();
        String fileName = new String((CommonUtil.getI18nMsg(TemplateDict.USER_EXPORT_TEMPLATE_NAME, i18nService)).getBytes(EasyExcelConstant.CHARSET_UTF), EasyExcelConstant.CHARSET_ISO);
        CommonUtil.setResponse(response, fileName);

        EasyExcel.write(os, UserImportBO.class)
                .head(head())
                .sheet(CommonUtil.getI18nMsg(TemplateDict.USER_DETAIL, i18nService))
                .relativeHeadRowIndex(1)
                .doWrite(bos);
    }

    @Override
    public void verifyImportData(UserImportBO bo, Integer rowIndex, StringBuilder errorMsg) {
        if (StringUtils.isBlank(bo.getName())) {
            errorMsg.append(formatImportMsg(i18nService, ImportHeadDict.USER_NAME_NULL, rowIndex)).append("\n");
        }
        if (StringUtils.isBlank(bo.getNickname())) {
            errorMsg.append(formatImportMsg(i18nService, ImportHeadDict.USER_NICKNAME_NULL, rowIndex)).append("\n");
        }
        if (Objects.isNull(bo.getSex())) {
            errorMsg.append(formatImportMsg(i18nService, ImportHeadDict.USER_SEX_NULL, rowIndex)).append("\n");
        }
        if (Objects.isNull(bo.getAge())) {
            errorMsg.append(formatImportMsg(i18nService, ImportHeadDict.USER_AGE_NULL, rowIndex)).append("\n");
        }
    }

    @Override
    public List<String> getTemplateHeadName() {
        return Arrays.stream(UserHeadDict.values()).map(e -> CommonUtil.getI18nMsg(e, i18nService)).collect(Collectors.toList());
    }

    private List<List<String>> head() {
        UserHeadDict[] userHeadDicts = UserHeadDict.values();
        List<List<String>> headList = new ArrayList<>(userHeadDicts.length);
        for (UserHeadDict excelPersonnelHead : userHeadDicts) {
            List<String> head = new ArrayList<>();
            head.add(CommonUtil.getI18nMsg(excelPersonnelHead, i18nService));
            headList.add(head);
        }
        return headList;
    }

    private void checkDuplicate(List<UserImportBO> bos) {
        // 姓名验重复
        Map<String, Long> countName = bos.stream().collect(Collectors.groupingBy(UserImportBO::getName, Collectors.counting()));
        countName.forEach((k, v) -> {
            if (v > 1) {
                throw new BusinessException(ResultCodeEnum.IMPORT_DATA_ERROR,formatImportMsg(i18nService, ImportHeadDict.USER_NAME_REPEAT, k));
            }
        });
    }
}
