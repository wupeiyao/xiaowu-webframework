package org.xiaowu.wpywebframework.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PagerResult<T> implements Serializable {
    @Schema(
        description = "页码"
    )
    private int pageIndex;
    @Schema(
        description = "条数"
    )
    private int pageSize;
    @Schema(
        description = "总数"
    )
    private int total;
    @Schema(
        description = "分页数据"
    )
    private List<T> data;

    public static <T> PagerResult<T> empty(int page, int size) {
        return of(page, size, 0, new ArrayList());
    }

    public static <T> PagerResult<T> empty(Pageable pageable) {
        return of(pageable.getPageIndex(), pageable.getPageSize(), 0, new ArrayList());
    }

    public static <T> PagerResult<T> of(Pageable pageable, List<T> list, long total) {
        return of(pageable.getPageIndex(), pageable.getPageSize(), Integer.parseInt(String.valueOf(total)), list);
    }

    public static <T> PagerResult<T> of(int page, int size, List<T> list, long total) {
        return of(page, size, Integer.parseInt(String.valueOf(total)), list);
    }

    public static <T> PagerResult<T> of(List<T> list) {
        return of(0, list.size(), list.size(), list);
    }

    public String toString() {
        int var10000 = this.pageIndex;
        return "PageResp{pageIndex=" + var10000 + ", pageSize=" + this.pageSize + ", total=" + this.total + ", data=" + String.valueOf(this.data) + "}";
    }

    @Generated
    public int getPageIndex() {
        return this.pageIndex;
    }

    @Generated
    public int getPageSize() {
        return this.pageSize;
    }

    @Generated
    public int getTotal() {
        return this.total;
    }

    @Generated
    public List<T> getData() {
        return this.data;
    }

    @Generated
    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Generated
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    @Generated
    public void setTotal(final int total) {
        this.total = total;
    }

    @Generated
    public void setData(final List<T> data) {
        this.data = data;
    }

    @Generated
    private PagerResult(final int pageIndex, final int pageSize, final int total, final List<T> data) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    @Generated
    public static <T> PagerResult<T> of(final int pageIndex, final int pageSize, final int total, final List<T> data) {
        return new PagerResult(pageIndex, pageSize, total, data);
    }
}
