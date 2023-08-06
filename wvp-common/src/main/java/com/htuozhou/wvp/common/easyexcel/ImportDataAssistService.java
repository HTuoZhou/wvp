package com.htuozhou.wvp.common.easyexcel;

import com.htuozhou.wvp.common.dict.BaseDict;
import com.htuozhou.wvp.common.service.I18nService;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/2/7
 */
public interface ImportDataAssistService<T> {

    /**
     * 校验数据
     *
     * @param bo       数据
     * @param rowIndex 行号
     * @param errorMsg 行数据校验错误信息
     */
    void verifyImportData(T bo, Integer rowIndex, StringBuilder errorMsg);

    /**
     * 格式化导入提示信息
     *
     * @param dict
     * @param params
     * @return
     */
    default String formatImportMsg(I18nService i18nService, BaseDict dict, Object... params) {
        return i18nService.getMessage(dict.getI18nKey(), params, dict.getDefaultValue());
    }

    /**
     * 获取导入模板的表头信息
     *
     * @return
     */
    List<String> getTemplateHeadName();
}
