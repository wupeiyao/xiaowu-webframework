package org.xiaowu.wpywebframework.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tasking {

    /**
     * 任务名称
     */
    String name() default "";

    /**
     * 任务描述
     */
    String description() default "";
}
