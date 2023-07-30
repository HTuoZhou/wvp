package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.SystemInfoBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/7/29
 */
@Data
public class SystemInfoVO {

    private List<Object> cpu;
    private List<Object> mem;
    private List<Object> net;

    private long netTotal;

    private Object disk;

    public static SystemInfoVO bo2vo(SystemInfoBO bo){
        SystemInfoVO vo = new SystemInfoVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
