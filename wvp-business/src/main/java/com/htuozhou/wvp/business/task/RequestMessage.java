package com.htuozhou.wvp.business.task;

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
