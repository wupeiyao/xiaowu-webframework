package org.xiaowu.wpywebframework.common.model;

/**
 * 分页查询请求对象，解决多个@RequestBody的问题
 */
public class PageQueryRequest<V> {
    private V condition;
    private PageRequest pageRequest;

    // getters and setters
    public V getCondition() { return condition; }
    public void setCondition(V condition) { this.condition = condition; }
    public PageRequest getPageRequest() { return pageRequest; }
    public void setPageRequest(PageRequest pageRequest) { this.pageRequest = pageRequest; }
}