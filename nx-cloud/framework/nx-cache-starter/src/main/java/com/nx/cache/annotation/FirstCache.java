/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 一级缓存配置项
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月16日
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FirstCache {
    boolean allowNull() default true;

    /**
     * 缓存有效时间
     *
     * @return int
     */
    int expireTime() default 7;
    
    /**
     * 缓存有效时间表达式
     * <p>可以通过SpEL表达式从注解的方法入参中提取有效时间，支持：int类型、String类型的int值，注意：在该属性和{@code expireTime}均提供的情况下，优先获取该属性。</p>
     * 
     * @return
     */
    String expireTimeExp() default "";

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
