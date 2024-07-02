package com.nx.cache.config;


import com.nx.cache.enums.LocalCacheTypeEnum;
import com.nx.cache.utils.CacheExpiresUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 本地缓存，也就是一级缓存
 */
@Data
public class LocalCacheConfig implements Serializable {

    private LocalCacheTypeEnum type;

    protected int concurrencyLevel=8;     //并发级别=8，并发级别表示可以同时写缓存的线程数
    protected int initialCapacity=50;     //设置缓存容器的初始容量为50
    protected int maximumSize=10000;        //设置缓存最大容量为100，超过100之后就会按照LRU最近最少使用移除缓存项
    protected long expireAfterWrite= CacheExpiresUtils.IN_1DAY;   //设置写缓存后100毫秒后过期,TimeUnit.MILLISECONDS
    protected boolean recordStats=false;  // 统计缓存情况，生产环境慎重使用




}