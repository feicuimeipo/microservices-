
package com.nx.cache;


import com.nx.cache.adapter.CacheAdapter;
import com.nx.cache.adapter.impl.RedisCacheAdapter;
import com.nx.cache.config.NxCacheConfig;
import com.nx.redis.RedisTemplateFactory;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nx.redis.RedisConfigFactory.DEFAULT_GROUP_NAME;

/**
 * CacheManager的抽象实现类
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月19日
 */
@Slf4j
public class CacheProviderFactory {
	public static final String DEFAULT_CACHE_GROUP_NAME = "spring";
	/**
	 * 缓存容器
	 * 外层key是cache_name
	 * 里层key是[一级缓存有效时间-二级缓存有效时间-二级缓存自动刷新时间]
	 */
	private static final Map<String, CacheProvider> iCacheMap = new ConcurrentHashMap<>(16);
	private volatile static Map<String, NxCacheConfig> nxCacheConfigMap = new ConcurrentHashMap<>();

	@Synchronized
	public static NxCacheConfig getCacheConfig(String groupName){
		NxCacheConfig nxCacheConfig = nxCacheConfigMap.get(groupName);
		if (nxCacheConfig!=null){
			return nxCacheConfig;
		}else{
			nxCacheConfig = NxCacheConfig.createNxCacheConfig(groupName);
			nxCacheConfigMap.put(groupName, nxCacheConfig);
		}
		return nxCacheConfig;
	}


	public static CacheProvider getCache(String groupName){
		CacheProvider cacheProvider = iCacheMap.get(groupName);
		if (cacheProvider !=null){
			return cacheProvider;
		}else{
			synchronized (CacheProviderFactory.class) {
				//第二次获取缓存Cache，加锁往容器里里面放Cache
				CacheProvider cache = iCacheMap.get(groupName);
				if (cache!=null) {
					return cache;
				}
			}
			cacheProvider = createCache(groupName);
			iCacheMap.put(groupName, cacheProvider);
		}
		return cacheProvider;
	}


	public static CacheProvider getDefaultCache(){
		return getCache(DEFAULT_GROUP_NAME);
	}


	private static CacheProvider createCache(String groupName) {
		NxCacheConfig nxCacheConfig = getCacheConfig(groupName);

		CacheAdapter level1CacheAdapter = LocalCacheFactory.getLocalCache(groupName);
		RedisCacheAdapter level2CacheAdapter;

		if (nxCacheConfig.getRedisGroupName().equals(DEFAULT_GROUP_NAME)) {
			level2CacheAdapter = new RedisCacheAdapter(RedisTemplateFactory.getDefaultRedisTemplate(), RedisTemplateFactory.getDefaultStringRedisTemplate());;
		} else {
			level2CacheAdapter = new RedisCacheAdapter(RedisTemplateFactory.getRedisTemplate(groupName), RedisTemplateFactory.getStringRedisTemplate(groupName));
		}

		CacheProvider cache = new CacheProviderImpl(groupName, level1CacheAdapter, level2CacheAdapter, nxCacheConfig);
		return cache;


	}




}
