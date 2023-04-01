package com.htuozhou.wvp.business.bo.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.htuozhou.wvp.business.easyexcel.UserSexDict;
import com.htuozhou.wvp.common.service.I18nService;
import com.htuozhou.wvp.common.utils.CommonUtil;
import com.htuozhou.wvp.persistence.po.UserPO;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/2/3
 */
@Data
@ColumnWidth(20)
@HeadStyle(horizontalAlignment = HorizontalAlignment.CENTER, fillForegroundColor = 9)
@ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
public class UserImportBO {

    /**
     * 姓名
     */
    @ExcelProperty(index = 0)
    private String name;

    /**
     * 昵称
     */
    @ExcelProperty(index = 1)
    private String nickname;

    /**
     * 性别
     */
    @ExcelProperty(index = 2)
    private String sex;

    /**
     * 年龄
     */
    @ExcelProperty(index = 3)
    private Integer age;

    public UserPO importBo2po(I18nService i18nService){
        UserPO po = new UserPO();
        BeanUtils.copyProperties(this,po);
        po.setSex(Objects.equals(this.getSex(),CommonUtil.getI18nMsg(UserSexDict.USER_SEX_MALE, i18nService)) ? 0 : 1);

        return po;
    }

    public static UserImportBO po2ImportBo(UserPO po,I18nService i18nService){
        UserImportBO bo = new UserImportBO();
        BeanUtils.copyProperties(po,bo);
        bo.setSex(po.getSex() == 0 ? CommonUtil.getI18nMsg(UserSexDict.USER_SEX_MALE, i18nService) :CommonUtil.getI18nMsg(UserSexDict.USER_SEX_FEMALE, i18nService));

        return bo;
    }

}
