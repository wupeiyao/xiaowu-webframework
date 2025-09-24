package org.xiaowu.wpywebframework.object.bucket;


import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
public class SimpleBucket implements Bucket {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
    /**
     * 桶名称
     */
    private final String name;

    /**
     * 文件夹
     */
    private final String folder;

    private SimpleBucket(String name, String folder) {
        this.name = name;
        this.folder = folder;
    }

    public static SimpleBucket of(String name) {
        return new SimpleBucket(name, FORMAT.format(new Date()));
    }

    public static SimpleBucket of(String name, String folder) {
        return new SimpleBucket(name, folder);
    }

}
