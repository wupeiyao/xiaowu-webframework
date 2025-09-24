package org.xiaowu.wpywebframework.core.genid;

public class SnowflakeIdWorker {

    private static final long TWEPOCH = 1288834974657L;  // 起始时间戳
    private static final long WORKER_ID_BITS = 5L; // 机器ID部分的位数
    private static final long DATACENTER_ID_BITS = 5L; // 数据中心部分的位数
    private static final long SEQUENCE_BITS = 12L; // 序列号部分的位数

    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);  // 最大机器ID值
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);  // 最大数据中心ID值
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);  // 序列号掩码

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    private final long workerId;
    private final long datacenterId;

    private static volatile SnowflakeIdWorker instance;

    private SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static SnowflakeIdWorker getInstance(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (instance == null) {
                    instance = new SnowflakeIdWorker(workerId, datacenterId);
                }
            }
        }
        return instance;
    }

    public static SnowflakeIdWorker getInstance() {
        return getInstance(1L, 1L);
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return (timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static SnowflakeIdWorker create(long workerId, long datacenterId) {
        return new SnowflakeIdWorker(workerId, datacenterId);
    }
}
