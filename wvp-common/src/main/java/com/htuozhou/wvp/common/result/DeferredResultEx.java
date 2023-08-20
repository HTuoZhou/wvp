package com.htuozhou.wvp.common.result;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author hanzai
 * @date 2023/8/13
 */
public class DeferredResultEx<T> {

    private DeferredResult<T> deferredResult;

    private DeferredResultFilter filter;

    public DeferredResultEx(DeferredResult<T> result) {
        this.deferredResult = result;
    }


    public DeferredResult<T> getDeferredResult() {
        return deferredResult;
    }

    public void setDeferredResult(DeferredResult<T> deferredResult) {
        this.deferredResult = deferredResult;
    }

    public DeferredResultFilter getFilter() {
        return filter;
    }

    public void setFilter(DeferredResultFilter filter) {
        this.filter = filter;
    }

}
