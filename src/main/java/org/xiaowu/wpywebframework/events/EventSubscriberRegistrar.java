package org.xiaowu.wpywebframework.events;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.xiaowu.wpywebframework.events.annotation.EventSubscriber;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class EventSubscriberRegistrar implements ApplicationListener<ApplicationEvent>,
        ApplicationContextAware, DisposableBean {

    private ApplicationContext applicationContext;
    private final Map<Class<?>, List<SubscriberInvoker>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService retryExecutor = Executors.newFixedThreadPool(4);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        List<SubscriberInvoker> exactInvokers = subscribers.get(event.getClass());
        if (exactInvokers != null) {
            exactInvokers.forEach(invoker -> invoker.invoke(event));
        }
        subscribers.forEach((eventType, invokers) -> {
            if (eventType.isAssignableFrom(event.getClass()) &&
                    !eventType.equals(event.getClass())) {
                invokers.forEach(invoker -> invoker.invoke(event));
            }
        });
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        registerSubscribers();
    }

    private void registerSubscribers() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getDeclaredMethods()) {
                EventSubscriber ann = method.getAnnotation(EventSubscriber.class);
                if (ann != null) {
                    validateMethod(method, ann);
                    method.setAccessible(true); // 只设置一次

                    subscribers.computeIfAbsent(ann.value(), k -> new CopyOnWriteArrayList<>())
                            .add(new SubscriberInvoker(bean, method, ann, retryExecutor));

                    log.info("注册事件订阅: {}.{} -> {} (retry={}, max={})",
                            bean.getClass().getSimpleName(),
                            method.getName(),
                            ann.value().getSimpleName(),
                            ann.retry(),
                            ann.maxRetries());
                }
            }
        }
    }

    private void validateMethod(Method method, EventSubscriber ann) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(
                    String.format("事件订阅方法必须有且仅有一个参数: %s.%s",
                            method.getDeclaringClass().getSimpleName(),
                            method.getName())
            );
        }

        Class<?> paramType = method.getParameterTypes()[0];
        if (!ann.value().isAssignableFrom(paramType)) {
            throw new IllegalStateException(
                    String.format("方法参数类型 %s 与订阅事件类型 %s 不匹配: %s.%s",
                            paramType.getSimpleName(),
                            ann.value().getSimpleName(),
                            method.getDeclaringClass().getSimpleName(),
                            method.getName())
            );
        }
    }

    @Override
    public void destroy() {
        retryExecutor.shutdown();
        try {
            if (!retryExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            retryExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    static class SubscriberInvoker {
        private final Object bean;
        private final Method method;
        private final EventSubscriber meta;
        private final ExecutorService retryExecutor;

        SubscriberInvoker(Object bean, Method method, EventSubscriber meta,
                          ExecutorService retryExecutor) {
            this.bean = bean;
            this.method = method;
            this.meta = meta;
            this.retryExecutor = retryExecutor;
        }

        void invoke(Object event) {
            if (meta.retry()) {
                retryExecutor.submit(() -> invokeWithRetry(event));
            } else {
                try {
                    method.invoke(bean, event);
                } catch (Exception e) {
                    log.error("事件处理失败: {}.{}",
                            bean.getClass().getSimpleName(),
                            method.getName(), e);
                }
            }
        }

        private void invokeWithRetry(Object event) {
            int maxAttempts = meta.maxRetries();
            Exception lastException = null;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    method.invoke(bean, event);
                    if (attempt > 1) {
                        log.info("事件重试成功: {}.{} (第{}次)",
                                bean.getClass().getSimpleName(),
                                method.getName(),
                                attempt);
                    }
                    return;
                } catch (Exception e) {
                    lastException = e;
                    log.warn("事件执行失败 [第{}/{}次]: {}.{} - {}",
                            attempt,
                            maxAttempts,
                            bean.getClass().getSimpleName(),
                            method.getName(),
                            e.getCause() != null ? e.getCause().getMessage() : e.getMessage());

                    if (attempt < maxAttempts) {
                        try {
                            Thread.sleep(meta.retryDelay());
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            log.error("重试被中断: {}.{}",
                                    bean.getClass().getSimpleName(),
                                    method.getName());
                            return;
                        }
                    }
                }
            }

            log.error("事件重试全部失败 ({}次): {}.{}",
                    maxAttempts,
                    bean.getClass().getSimpleName(),
                    method.getName(),
                    lastException);
        }
    }
}