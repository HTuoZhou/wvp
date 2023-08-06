package com.htuozhou.wvp.business.bean;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/6
 */
@Data
public class CivilCodeItem {

    private String code;

    private String name;

    private String parentCode;

    public static CivilCodeItem getCivilCodeItem(String[] split) {
        CivilCodeItem item = new CivilCodeItem();
        item.setCode(split[0]);
        item.setName(split[1]);
        item.setParentCode(split[2]);
        return item;
    }

}
