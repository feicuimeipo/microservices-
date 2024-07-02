/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache.annotation;

import com.nx.redis.enums.RedisConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static com.nx.cache.CacheProviderFactory.DEFAULT_CACHE_GROUP_NAME;

/**
 * 删除缓存
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
public @interface CacheEvict {
    //缓存名称
    String cacheName() default DEFAULT_CACHE_GROUP_NAME;


    //key前缀
    String prefix() default "";

    @AliasFor("key")
    String name() default "";

    @AliasFor("key")
    String value() default "";

    /**
     * 缓存key，支持SpEL表达式
     * @return String
     */
    String key() default "";

    
    /**
     * key是否为纯文本，即不做SpEL解析
     * @return
     */
    boolean pureKey() default false;

    /**
     * 是否忽略在操作缓存中遇到的异常，如反序列化异常，默认true。
     * <p>true: 有异常会输出warn级别的日志，并直接执行被缓存的方法（缓存将失效）</p>
     * <p>false:有异常会输出error级别的日志，并抛出异常</p>
     *
     * @return boolean
     */
    boolean ignoreException() default true;

}
