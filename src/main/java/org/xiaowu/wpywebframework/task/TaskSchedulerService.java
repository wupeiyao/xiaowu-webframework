package org.xiaowu.wpywebframework.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class TaskSchedulerService  implements DisposableBean {

    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    public TaskSchedulerService() {
        scheduler.setPoolSize(8);
        scheduler.setThreadNamePrefix("task-");
        scheduler.initialize();
    }

    public void register(String name, String cron, TaskJob job) {
        if (futures.containsKey(name)) {
            log.warn("任务名称重复，将覆盖：{}", name);
            cancel(name);
        }
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            long start = System.currentTimeMillis();
            log.info("开始执行任务：{}", name);
            try {
                job.execute();
                log.info("任务完成：{} 耗时：{}ms", name, System.currentTimeMillis() - start);
            } catch (Exception e) {
                log.error("任务失败：{}", name, e);
            }
        }, new CronTrigger(cron));
        futures.put(name, future);
    }

    public void cancel(String name) {
        ScheduledFuture<?> future = futures.remove(name);
        if (future != null) future.cancel(true);
        log.info("停止任务：{}", name);
    }

    @Override
    public void destroy() {
        futures.values().forEach(f -> f.cancel(false));
        scheduler.shutdown();
        log.info("任务调度器已关闭");
    }
}
