package org.xiaowu.wpywebframework.common.model;




import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    private Long current = 1L;
    private Long size = 6L;
    private String orderBy;
    private String orderDir = "ASC";

    // 泛型支持，返回 Page<T>
    public <T> Page<T> toPage() {
        Page<T> page = new Page<>(current, size);

        if (orderBy != null && !orderBy.isEmpty()) {
            if ("DESC".equalsIgnoreCase(orderDir)) {
                page.addOrder(OrderItem.desc(orderBy));
            } else {
                page.addOrder(OrderItem.asc(orderBy));
            }
        }

        return page;
    }
}
