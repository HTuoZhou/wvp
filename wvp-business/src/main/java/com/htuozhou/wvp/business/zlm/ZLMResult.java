package com.htuozhou.wvp.business.zlm;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author hanzai
 * @date 2023/4/1
 */
@Getter
@Setter
@Accessors(chain = true)
public class ZLMResult {

    private Integer code;

    private Integer changed;

    private String msg;

    private Integer result;

    private Object data;

}
