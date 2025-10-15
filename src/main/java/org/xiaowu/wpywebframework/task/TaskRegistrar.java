package org.xiaowu.wpywebframework.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.xiaowu.wpywebframework.task.annotation.Task;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class TaskRegistrar implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private final TaskSchedulerService taskSchedulerService;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext ctx) throws BeansException {
        this.applicationContext = ctx;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Task.class);
        for (Object bean : beans.values()) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            Task task = clazz.getAnnotation(Task.class);
            if (!task.enable()) continue;
            if (bean instanceof TaskJob job) {
                try {
                    new CronTrigger(task.cron());
                    taskSchedulerService.register(task.name(), task.cron(), job);
                    log.info("注册任务：{} -> {}", task.name(), task.cron());
                } catch (IllegalArgumentException e) {
                    log.error("任务 {} 的 cron 表达式无效：{}", task.name(), task.cron(), e);
                }
            } else {
                log.warn("类 {} 未实现 TaskJob 接口，跳过注册", clazz.getName());
            }
        }
    }
}
