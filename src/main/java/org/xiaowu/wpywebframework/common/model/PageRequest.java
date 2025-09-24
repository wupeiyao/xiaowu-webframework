package org.xiaowu.wpywebframework.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向（asc/desc）
     */
    private String orderDirection = "asc";

    /**
     * 搜索关键字
     */
    private String keyword;
}
