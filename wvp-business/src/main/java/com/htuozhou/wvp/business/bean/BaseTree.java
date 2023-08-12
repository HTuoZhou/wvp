package com.htuozhou.wvp.business.bean;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/12
 */
@Data
public class BaseTree<T> {

    private String id;
    private String deviceId;
    private String pid;
    private String name;
    private Boolean parent;
    private T child;

}
