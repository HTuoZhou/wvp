package com.htuozhou.wvp.common.page;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@Getter
@Setter
@Accessors(chain = true)
public class PageReq<T> {

    /**
     * 当前页码 默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页大小 默认10
     */
    private Integer pageSize = 10;

    private T queryParam;

    public <R> PageReq<R> pageVo2Bo(Function<? super T, ? extends R> mapper) {
        if (Objects.isNull(queryParam)) {
            return (PageReq<R>) this;
        }
        return ((PageReq<R>) this).setQueryParam(mapper.apply(queryParam));
    }


}
