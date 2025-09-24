//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.common.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Pageable implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 20;
    private static final int firstPageNo = 0;
    @Parameter(
        description = "页码"
    )
    private Integer pageIndex;
    @Parameter(
        description = "每页大小"
    )
    private Integer pageSize;
    @Parameter(
        hidden = true
    )
    private List<Sort> sorts;

    public static Pageable of(Integer pageIndex, Integer pageSize) {
        return new Pageable(pageIndex, pageSize);
    }

    public Pageable() {
        this(0, 20);
    }

    public Pageable(Integer pageIndex, Integer pageSize) {
        this.sorts = new LinkedList();
        this.pageIndex = pageIndex == null ? 0 : Math.max(pageIndex, 0);
        this.pageSize = pageIndex == null ? 20 : (pageSize <= 0 ? 20 : pageSize);
    }

    public int getPageOffset() {
        if (this.pageIndex < 1) {
            this.pageIndex = 0;
        }

        if (this.pageSize < 1) {
            this.pageSize = 0;
        }

        return (this.pageIndex - 0) * this.pageSize;
    }

    public int getPageIndex() {
        return this.pageIndex == null ? 0 : this.pageIndex;
    }

    public int getPageNumber() {
        return this.pageIndex == null ? 0 : this.pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex == null ? 0 : Math.max(pageIndex, 0);
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = this.pageIndex == null ? 20 : (pageSize <= 0 ? 20 : pageSize);
    }

    public Sort orderBy(String column) {
        Sort sort = Sort.by(new String[]{column});
        this.sorts.add(sort);
        return sort;
    }

    @Parameters({@Parameter(
    name = "sorts[0].name",
    description = "排序字段",
    schema = @Schema(
    implementation = String.class
),
    in = ParameterIn.QUERY
), @Parameter(
    name = "sorts[0].order",
    description = "顺序,asc或者desc",
    schema = @Schema(
    implementation = String.class
),
    in = ParameterIn.QUERY
)})
    public List<Sort> getSorts() {
        return this.sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public String toString() {
        return "Pageable [Pageable=" + this.pageIndex + ", pageSize=" + this.pageSize + "]";
    }
}
