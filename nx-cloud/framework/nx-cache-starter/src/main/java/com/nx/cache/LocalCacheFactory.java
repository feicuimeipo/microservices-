
package com.nx.cache;


import com.nx.cache.adapter.impl.CaffeineCacheAdapter;
import com.nx.cache.adapter.impl.GuavaCacheAdapter;
import com.nx.cache.adapter.CacheAdapter;
import com.nx.cache.config.LocalCacheConfig;
import com.nx.cache.enums.LocalCacheTypeEnum;
import com.nx.redis.RedisConfigFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Caffeine-一级缓存
 */
@SuppressWarnings("unchecked")
public class LocalCacheFactory {

	/**
	 * 缓存容器
	 * 外层key是cache_name
	 * 里层key是[一级缓存有效时间-二级缓存有效时间-二级缓存自动刷新时间]
	 */
	protected static final ConcurrentMap<String, CacheAdapter> localContainer = new ConcurrentHashMap<>(16);

	public static CacheAdapter getDefaultLocalCache() {
		return getLocalCache(RedisConfigFactory.DEFAULT_GROUP_NAME, LocalCacheTypeEnum.guava);
	}

	public static CacheAdapter getLocalCache(String cacheName) {
		return getLocalCache(cacheName, LocalCacheTypeEnum.caffeine);
	}

	public static CacheAdapter getLocalCache(String cacheName, LocalCacheTypeEnum localCacheTypeEnum) {
		String key = localCacheTypeEnum.name() + "_" + cacheName;
		if (localContainer.containsKey(key)){
			return localContainer.get(key);
		}
		CacheAdapter adapter = newLocalCache(cacheName, localCacheTypeEnum);
		localContainer.put(key,adapter);
		return adapter;
	}


	private static CacheAdapter newLocalCache(String cacheName, LocalCacheTypeEnum localCacheTypeEnum) {
		LocalCacheConfig LocalCacheConfig = CacheProviderFactory.getCacheConfig(cacheName).getLevel1();
		switch (localCacheTypeEnum){
			case guava:
				return new GuavaCacheAdapter(LocalCacheConfig);
			default:
				return new CaffeineCacheAdapter(LocalCacheConfig);
		}
	}

}
