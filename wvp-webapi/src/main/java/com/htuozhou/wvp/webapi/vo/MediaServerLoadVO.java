package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.MediaServerLoadBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Data
public class MediaServerLoadVO {

    private String id;
    private Integer push;
    private Integer proxy;
    private Integer gbReceive;
    private Integer gbSend;

    public static MediaServerLoadVO bo2vo(MediaServerLoadBO bo){
        MediaServerLoadVO vo = new MediaServerLoadVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
