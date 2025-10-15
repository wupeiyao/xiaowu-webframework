package org.xiaowu.wpywebframework.events.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventSubscriber {
    /**
     * 订阅的事件类型
     */
    Class<?> value();

    /**
     * 是否启用重试
     */
    boolean retry() default false;

    /**
     * 最大重试次数
     */
    int maxRetries() default 3;

    /**
     * 重试间隔（毫秒）
     */
    long retryDelay() default 1000;
}
