/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache;

import com.nx.cache.adapter.impl.RedisCacheAdapter;
import com.nx.cache.enums.CacheModeEnum;
import com.nx.cache.adapter.CacheAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * 缓存操作接口
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2018年6月1日
 */
public interface CacheProvider {


    CacheAdapter get1LevelCache();

    RedisCacheAdapter getLevel2Cache();

    <T> T get(String key);

    <T> T getFromLevel2(String key);

    <T> T getFromLevel1(String key);

    <T> T getFromLocalCache(String key);

    /**
     * 根据Key数组及类型批量返回对应的缓存值
     * <p>该方法主要用于批量获取指定语种的国际化资源</p>
     * @param keys	key数组
     * @return		Map格式的缓存数据，Map中的可以与key数组中的key对应
     */
    Map<String, Object> getAll(Iterable<String> keys);

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则返回NULL
     *
     * @param key  缓存key
     * @param type 返回值类型
     * @param <T>  Object
     * @return 缓存key对应的值
     */
    <T> T get(String key, Class<T> type);

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则调用valueLoader加载数据
     *
     * @param key         缓存key
     * @param valueLoader 加载缓存的回调方法
     * @param <T>         Object
     * @return 缓存key对应的值
     */
    <T> T get(String key, Callable<T> valueLoader);



    <T> T get(String key,  Callable<T> valueLoader,Class<T> retrunClass);


    void set(String key, Object value, long expire, boolean allowNullValue);

    void set(String key, Object value, long level1Expire, long level2Expire);

    void set(String key, Object value, long expire, TimeUnit timeUnit);

    void set(String key, Object value, boolean allowNullValue);



   void set(String key, Object value, long level1Expire, long level2Expire, CacheModeEnum cacheModeEnum, boolean level1allowNullValue, boolean level2AllowNullValue);

    /**
     * 将对应key-value放到缓存，如果key原来有值就直接覆盖
     *
     * @param key   缓存key
     * @param value 缓存的值
     */
    void set(String key, Object value);


    /**
     *
     * @param key
     * @param value
     * @param expiration
     *  expireation[0]: firstexpire
     *  expireation[1]: redisexpire
     */
    public void set(String key, Object value,long expiration) ;

    void set(String key, Object value, long firstExpire, long secondExpire, boolean allowNullValue);




    void set(String key, Object value, long firstExpire, long secondExpire, TimeUnit timeUnit, boolean allowNullValue);



    void evict(String key);

    /**
     * 清除指定类型的指定字段缓存
     * <p>该方法用于清除指定语种的指定字段国际化资源</p>
     * @param key
     * @param field
     */
    void hdel(String key, String field);

    /**
     * 通过key数组删除对应缓存
     * @param keys	key数组
     */
    void evictAll(Iterable<String> keys);

    /**
     * 清除缓存
     */
    void clearLocalCache();


    void remove(String... keys);

    boolean exists(String key);

    void addListItems(String key, String... items);

    List<Object> getListItems(String key, int start, int end);

        void setMapItem(String key, String field, Object value);
}