package com.htuozhou.wvp.business.bo;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/7/30
 */
@Data
public class MediaServerLoadBO {

    private String id;
    private Integer push;
    private Integer proxy;
    private Integer gbReceive;
    private Integer gbSend;

}
