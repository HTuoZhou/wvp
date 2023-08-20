package com.htuozhou.wvp.common.result;

import lombok.Data;

/**
 * @author hanzai
 * @date 2023/8/13
 */
@Data
public class RequestMessage {

    private String id;

    private String key;

    private Object data;

}
