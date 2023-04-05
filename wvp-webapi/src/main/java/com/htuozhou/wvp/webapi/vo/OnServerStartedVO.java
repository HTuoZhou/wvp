package com.htuozhou.wvp.webapi.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Data
public class OnServerStartedVO {

    @JSONField(name = "general.mediaServerId")
    private String mediaServerId;

}
