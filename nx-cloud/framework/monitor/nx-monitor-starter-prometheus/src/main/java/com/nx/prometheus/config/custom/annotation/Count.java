package com.nx.prometheus.config.custom.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Count
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 18:58
 * @Version 1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Count {
    String description() default "";
}
