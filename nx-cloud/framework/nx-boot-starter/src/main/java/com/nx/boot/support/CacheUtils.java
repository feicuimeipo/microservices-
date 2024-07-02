package com.nx.boot.support;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Cache 工具类
 */
public class CacheUtils {

    public static <K, V> LoadingCache<K, V> buildAsyncReloadingCache(Long duration,TimeUnit timeUnit, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder()
                // 只阻塞当前数据加载线程，其他线程返回旧值
                .refreshAfterWrite(duration,timeUnit)
                //通过 asyncReloading 实现全异步加载，包括 refreshAfterWrite 被阻塞的加载线程
                .build(CacheLoader.asyncReloading(loader, Executors.newCachedThreadPool())); 
                // TODO 芋艿：可能要思考下，未来要不要做成可配置
    }

}
