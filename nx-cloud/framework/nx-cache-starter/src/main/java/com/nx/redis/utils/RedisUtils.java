package com.nx.redis.utils;

import com.nx.cache.exception.LoaderCacheValueException;
import com.nx.cache.utils.NullValue;
import com.nx.redis.RedisTemplateFactory;
import com.nx.redis.RedisConfigFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 *
 * cache的redis实现
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月17日
 */
@SuppressWarnings("unchecked")
public class RedisUtils {
	private static RedisUtils redisCache;

	protected static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

	/**
	 * 刷新缓存重试次数
	 */
	private static final int RETRY_COUNT = 20;
	/**
	 * 刷新缓存等待时间，单位毫秒
	 */
	private static final long WAIT_TIME = 20;

	/**
	 * 非空值和null值之间的时间倍率，默认是1。allowNullValue=true才有效
	 * 如配置缓存的有效时间是200秒，倍率这设置成10，
	 * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
	 */
	private int magnification=10;
	/**
	 * 缓存有效时间,秒
	 */
	private long expiration=15;

	private final TimeUnit timeUnit=TimeUnit.SECONDS;

	private AwaitThreadContainer container = new AwaitThreadContainer();


	private RedisTemplate<String, Object> redisTemplate;

	private boolean usePrefix=true;


	/**
	 * 是否允许为NULL
	 */
	private final boolean allowNullValue=true;

	public RedisUtils() {
		super();
	}


	private RedisSerializer<?> getKeySerialize;

	private synchronized static RedisUtils getInstance(){
		if (redisCache==null){
			redisCache = new RedisUtils();
			redisCache.redisTemplate  = RedisTemplateFactory.getDefaultRedisTemplate();
			redisCache.getKeySerialize = RedisTemplateFactory.getDefaultRedisTemplate().getKeySerializer();

		}
		return redisCache;
	}
	public RedisKeyUtils getRedisKey(Object key){
		return new RedisKeyUtils(RedisConfigFactory.DEFAULT_GROUP_NAME,"",key, redisCache.redisTemplate.getKeySerializer());
	}


	public Object get(Object key) {
		RedisKeyUtils redisKeyUtils = getRedisKey(key);
		logger.debug("redis缓存 key= {} 查询redis缓存", redisKeyUtils);
		return redisTemplate.opsForValue().get(redisKeyUtils.getKey());
	}

	public <T> T get(Object key, Callable<T> valueLoader) {
		RedisKeyUtils redisKeyUtils = getRedisKey(key);
		logger.debug("redis缓存 key= {} 查询redis缓存如果没有命中，从数据库获取数据", redisKeyUtils.getKey());
		// 先获取缓存，如果有直接返回
		Object result = redisTemplate.opsForValue().get(redisKeyUtils.getKey());
		if (result != null || redisTemplate.hasKey(redisKeyUtils.getKey())) {
			// 刷新缓存
			if (allowNullValue && result instanceof NullValue) {
				return null;
			}
			return (T) result;
		}
		// 执行缓存方法
		return executeCacheMethod(redisKeyUtils, valueLoader);
	}

	public void put(Object key, Object value) {
		RedisKeyUtils redisKeyUtils = getRedisKey(key);
		logger.debug("redis缓存 key= {} put缓存", redisKeyUtils.getKey());
		putValue(redisKeyUtils, value);
	}

	public Object putIfAbsent(Object key, Object value) {
		logger.debug("redis缓存 key= {} putIfAbsent缓存", getRedisKey(key).getKey());
		Object reult = get(key);
		if (reult != null) {
			return reult;
		}
		put(key, value);
		return null;
	}

	public void evict(Object key) {
		RedisKeyUtils redisKeyUtils = getRedisKey(key);
		logger.info("清除redis缓存 key= {} ", redisKeyUtils.getKey());
		redisTemplate.delete(redisKeyUtils.getKey());
	}

	public void evictAll(Iterable<? extends Object> keys) {

	}

	private Object fromStoreValue(Object result){
		if (result!=null && result instanceof NullValue){
			return null;
		}
		return result;
	}

	protected Object toStoreValue(Object userValue) {
		if (allowNullValue && userValue == null) {
			return NullValue.INSTANCE;
		}
		return userValue;
	}



	/**
	 * 同一个线程循环5次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
	 */
	private <T> T executeCacheMethod(RedisKeyUtils redisKeyUtils, Callable<T> valueLoader) {
		Lock redisLock = new Lock(redisTemplate, redisKeyUtils.getKey() + "_sync_lock");
		// 同一个线程循环20次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
		for (int i = 0; i < RETRY_COUNT; i++) {
			try {
				// 先取缓存，如果有直接返回，没有再去做拿锁操作
				Object result = redisTemplate.opsForValue().get(redisKeyUtils.getKey());
				if (result != null) {
					logger.debug("redis缓存 key= {} 获取到锁后查询查询缓存命中，不需要执行被缓存的方法", redisKeyUtils.getKey());
					return (T) fromStoreValue(result);
				}

				// 获取分布式锁去后台查询数据
				if (redisLock.lock()) {
					T t = loaderAndPutValue(redisKeyUtils, valueLoader, true);
					logger.debug("redis缓存 key= {} 从数据库获取数据完毕，唤醒所有等待线程", redisKeyUtils.getKey());
					// 唤醒线程
					container.signalAll(redisKeyUtils.getKey());
					return t;
				}
				// 线程等待
				logger.debug("redis缓存 key= {} 从数据库获取数据未获取到锁，进入等待状态，等待{}毫秒", redisKeyUtils.getKey(), WAIT_TIME);
				container.await(redisKeyUtils.getKey(), WAIT_TIME);
			} catch (Exception e) {
				container.signalAll(redisKeyUtils.getKey());
				throw new LoaderCacheValueException(redisKeyUtils.getKey(), e);
			} finally {
				redisLock.unlock();
			}
		}
		logger.debug("redis缓存 key={} 等待{}次，共{}毫秒，任未获取到缓存，直接去执行被缓存的方法", redisKeyUtils.getKey(), RETRY_COUNT, RETRY_COUNT * WAIT_TIME, WAIT_TIME);
		return loaderAndPutValue(redisKeyUtils, valueLoader, true);
	}

	/**
	 * 加载并将数据放到redis缓存
	 */
	private <T> T loaderAndPutValue(RedisKeyUtils key, Callable<T> valueLoader, boolean isLoad) {
		long start = System.currentTimeMillis();
		try {
			// 加载数据
			Object result = putValue(key, valueLoader.call());
			logger.debug("redis缓存 key={} 执行被缓存的方法，并将其放入缓存, 耗时：{}。", key.getKey(), System.currentTimeMillis() - start);
			return (T) fromStoreValue(result);
		} catch (Exception e) {
			throw new LoaderCacheValueException(key.getKey(), e);
		}
	}

	private Object putValue(RedisKeyUtils key, Object value) {
		Object result = toStoreValue(value);
		// redis 缓存不允许直接存NULL
		if (result == null) {
			return result;
		}
		// 不允许缓存NULL值，删除缓存
		if (!allowNullValue && result instanceof NullValue) {
			redisTemplate.delete(key.getKey());
			return result;
		}

		// 允许缓存NULL值
		long expirationTime = this.expiration;
		// 允许缓存NULL值且缓存为值为null时需要重新计算缓存时间
		if (allowNullValue && result instanceof NullValue) {
			expirationTime = expirationTime / magnification;
		}
		// 将数据放到缓存
		redisTemplate.opsForValue().set(key.getKey(), result, expirationTime, timeUnit);
		return result;
	}



	public Map<String, String> getAll(Iterable<String> keys, String type) {
		Map<String, String> map = new HashMap<String, String>();
		if (keys==null)
			return map;
		HashMap<String, List<Object>> newMap = new HashMap<String, List<Object>>();
		for (String key : keys) {
			RedisKeyUtils redisKeyUtils = getRedisKey(key);
			List<Object> multiGet = redisTemplate.opsForHash().multiGet(redisKeyUtils.getKey(), Arrays.asList(type));
			newMap.put(key, multiGet);
		}
		for (Map.Entry<String, List<Object>> entry : newMap.entrySet()) {
			String key = entry.getKey();
			List<Object> list = entry.getValue();
			if (list!=null && list.size()>0 && list.get(0)!=null) {
				map.put(key, String.valueOf(list.get(0)));
			} else {
				// 没找到国际化资源时，将key作为值返回
				map.put(key, key);
			}
		}
		return map;
	}


	public void putAll(Map<String, Map<String, String>> map) {
		if(map==null) {
			return;
		}
		try {
			Iterator<Map.Entry<String, Map<String, String>>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Map<String, String>> next = it.next();
				String key = next.getKey();
				RedisKeyUtils redisKeyUtils = getRedisKey(key);
				Map<String, String> value = next.getValue();
				redisTemplate.opsForHash().putAll(redisKeyUtils.getKey(), value);
			}
		}
		catch(Exception ex) {
			logger.error("批量存放数据到缓存中时出错了：", ExceptionUtils.getRootCauseMessage(ex));
		}
	}

	public void hdel(String key, String field) {
		RedisKeyUtils redisKeyUtils = getRedisKey(key);
		redisTemplate.opsForHash().delete(redisKeyUtils.getKey(), field);
	}




}
