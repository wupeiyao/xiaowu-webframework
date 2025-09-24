package org.xiaowu.wpywebframework.common.registry;

public interface Registry {
    String getCode();

    default String getMessage() {
        return "操作成功";
    }
}
