package org.xiaowu.wpywebframework.core.genid;

public class IDGenerator {

    private static final SnowflakeIdWorker worker;

    static {
        long workerId = 1L;
        long datacenterId = 1L;

        worker = SnowflakeIdWorker.getInstance(workerId, datacenterId);
    }

    /**
     * 获取下一个 long 类型的 ID
     */
    public static long nextId() {
        return worker.nextId();
    }

    /**
     * 获取下一个 String 类型的 ID
     */
    public static String nextIdStr() {
        return String.valueOf(worker.nextId());
    }
}
