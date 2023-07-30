package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bean.ResourceBaseInfo;
import com.htuozhou.wvp.business.bo.ResourceInfoBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Data
public class ResourceInfoVO {

    private ResourceBaseInfo device;
    private ResourceBaseInfo channel;
    private ResourceBaseInfo push;
    private ResourceBaseInfo proxy;

    public static ResourceInfoVO bo2vo(ResourceInfoBO bo){
        ResourceInfoVO vo = new ResourceInfoVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
