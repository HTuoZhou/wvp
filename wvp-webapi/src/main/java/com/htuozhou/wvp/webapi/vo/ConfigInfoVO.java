package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.ConfigInfoBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author hanzai
 * @date 2023/8/23
 */
@Data
public class ConfigInfoVO {

    private String ip;
    private Integer port;
    private String domain;
    private String id;
    private String password;

    public static ConfigInfoVO bo2vo(ConfigInfoBO bo){
        ConfigInfoVO vo = new ConfigInfoVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
