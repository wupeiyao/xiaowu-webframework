package org.xiaowu.wpywebframework.object.bucket;

public interface Bucket {

    /**
     * @return 桶名称
     */
    String getName();

    /**
     * @return 文件夹
     */
    default String getFolder() {
        return null;
    }

    /**
     * 是否是临时的桶
     *
     * @return true临时 false 永久
     */
    default boolean temporary() {
        return false;
    }

    /**
     * @return 临时文件过期时间, 默认3天
     */
    default int overdue() {
        return 0;
    }

}
