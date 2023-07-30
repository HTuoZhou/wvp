package com.htuozhou.wvp.business.bo;

import com.htuozhou.wvp.business.bean.ResourceBaseInfo;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Data
public class ResourceInfoBO {

    private ResourceBaseInfo device;
    private ResourceBaseInfo channel;
    private ResourceBaseInfo push;
    private ResourceBaseInfo proxy;

}
