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

}
