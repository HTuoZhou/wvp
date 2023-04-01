package com.htuozhou.wvp.common.page;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hanzai
 * @date 2023/2/2
 */
public class PageResp<T> {

    /**
     * 记录总数
     */
    private Integer total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 分页总数
     */
    private Integer pages;

    /**
     * 分页记录
     */
    private List<T> records;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public static <T, R> PageResp<R> pageBo2Vo(IPage<T> page, Function<? super T, ? extends R> mapper) {
        PageResp<R> pageResult = new PageResp();
        pageResult.setTotal((int) page.getTotal());
        pageResult.setPageSize((int) page.getSize());
        pageResult.setPageNum((int) page.getCurrent());
        pageResult.setPages((int) page.getPages());
        if (Objects.nonNull(page.getRecords()) && CollUtil.isNotEmpty(page.getRecords())) {
            List<R> collects = page.getRecords().stream().map(mapper).collect(Collectors.toList());
            pageResult.setRecords(collects);
        }
        return pageResult;
    }


}
