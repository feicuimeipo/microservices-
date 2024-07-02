package com.nx.prometheus.config.custom.annotation;

import java.lang.annotation.*;

/**
 * @ClassName 一个通用注解，用于同时上报多个指标类型
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 18:50
 * @Version 1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Monitor {
    String description() default "";
}
