package org.xiaowu.wpywebframework.common.annotation;

import java.lang.annotation.*;

import java.lang.annotation.*;

/**
 * 用于在实体类、字段、方法上增加说明性注释
 * 可扩展字段属性，比如长度、是否可为空等
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Comment {

    /**
     * 注释内容
     */
    String value();

    /**
     * 字段长度（默认 -1 表示不限制）
     */
    int length() default -1;

    /**
     * 是否允许为空（默认 true）
     */
    boolean nullable() default true;

    /**
     * 是否唯一（默认 false）
     */
    boolean unique() default false;
}

