/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache.annotation;

import java.lang.annotation.*;

/**
 *
 *
 * 将对应参数的到缓存中
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
public @interface CacheFields {
    CacheField[] value() default @CacheField();
}
