package com.htuozhou.wvp.business.bo;

import lombok.Data;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Data
public class SystemInfoBO {

    private List<Object> cpu;
    private List<Object> mem;
    private List<Object> net;

    private long netTotal;

    private Object disk;

}
