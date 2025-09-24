package org.xiaowu.wpywebframework.core.cipher;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class ServerThreadExecutor {
    private static ThreadPoolTaskExecutor executor;

    public ServerThreadExecutor() {
    }

    public static ThreadPoolTaskExecutor getExecutor() {
        if (executor != null) {
            return executor;
        } else {
            Class var0 = ServerThreadExecutor.class;
            synchronized(ServerThreadExecutor.class) {
                if (executor != null) {
                    return executor;
                } else {
                    executor = new ThreadPoolTaskExecutor();
                    executor.setCorePoolSize(5);
                    executor.setMaxPoolSize(50);
                    executor.setQueueCapacity(1000);
                    executor.setKeepAliveSeconds(100);
                    executor.setThreadNamePrefix("ServerThreadExecutor:");
                    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                    executor.initialize();
                    return executor;
                }
            }
        }
    }
}
