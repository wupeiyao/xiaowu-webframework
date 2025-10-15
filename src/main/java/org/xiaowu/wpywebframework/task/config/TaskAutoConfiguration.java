package org.xiaowu.wpywebframework.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaowu.wpywebframework.task.TaskRegistrar;
import org.xiaowu.wpywebframework.task.TaskSchedulerService;

@Configuration
public class TaskAutoConfiguration {

    @Bean
    public TaskSchedulerService taskSchedulerService() {
        return new TaskSchedulerService();
    }

    @Bean
    public TaskRegistrar taskRegistrar(TaskSchedulerService schedulerService) {
        return new TaskRegistrar(schedulerService);
    }
}