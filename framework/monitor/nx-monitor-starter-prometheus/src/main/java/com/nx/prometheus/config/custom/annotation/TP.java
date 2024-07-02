package com.nx.prometheus.config.custom.annotation;

import java.lang.annotation.*;

/**
 * 标注上报计时器指标类型的注解，
 * @ClassName TP
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 18:57
 * @Version 1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TP {
    String description() default "";
}
