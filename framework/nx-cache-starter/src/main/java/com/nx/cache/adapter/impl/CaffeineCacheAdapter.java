
package com.nx.cache.adapter.impl;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nx.cache.adapter.CacheAdapter;
import com.nx.cache.config.LocalCacheConfig;
import com.nx.cache.expression.ExpireableObject;
import com.nx.cache.utils.NullValue;
import com.nx.redis.utils.RedisKeyUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine-一级缓存
 */
@Getter
public class CaffeineCacheAdapter implements CacheAdapter {
	private static final Logger log = LoggerFactory.getLogger(CaffeineCacheAdapter.class);
	
	private Cache<Object, Object> cache;

	private CaffeineCacheAdapter() {

	}

	public CaffeineCacheAdapter(LocalCacheConfig cacheSetting)  {

		// 根据配置创建Caffeine builder
		cache=Caffeine.newBuilder()
				.initialCapacity(cacheSetting.getInitialCapacity())
				.maximumSize(cacheSetting.getMaximumSize())
				.expireAfterWrite(cacheSetting.getExpireAfterWrite(), TimeUnit.SECONDS)
				.build();	}


	@Override
	public void set(String key, Object value) {
		set(key,value,false,-1,TimeUnit.MILLISECONDS);
	}

	@Override
	public void set(String key, Object value, long expire) {
		set(key,value,false,expire,TimeUnit.MILLISECONDS);
	}

	@Override
	public void set(String key, Object value, long expire, TimeUnit timeUnit) {
		set(key,value,false,expire,timeUnit);
	}

	@Override
	public void set(String key, Object value, boolean isAllowNull, long expire, TimeUnit timeUnit) {
		key = RedisKeyUtils.buildNameSpaceKey(key);

		if (value==null && isAllowNull){
			value = NullValue.INSTANCE;
		}

		if (value==null){
			return;
		}

		try {
			ExpireableObject expireableObject = new ExpireableObject(value, expire,timeUnit);

			cache.put(key, expireableObject);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}

	}


	@Override
	public <T> T get(String key) {
		key = RedisKeyUtils.buildNameSpaceKey(key);
		try {
			T obj = (T) this.cache.getIfPresent(key);
			if(obj == null) return null;

			if (!(obj instanceof ExpireableObject)){
				return obj;
			}

			ExpireableObject expireableObject = (ExpireableObject) obj;
			if (expireableObject.getExpireAt()==-1){
				return (T) expireableObject.getTarget();
			}
			if(expireableObject.getExpireAt() < System.currentTimeMillis()) {
				cache.invalidate(key);
				return null;
			}
			return (T) expireableObject.getTarget();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return  null;
		}
	}

	@Override
	public <T> T get(String key,Class<T> tClass) {
		return get(key);
	}


	@Override
	public void remove(String... keys) {

		if (keys==null || keys.length==0) return;

		String[] myKeys = Arrays.stream(keys).map(key->{
			return RedisKeyUtils.buildNameSpaceKey(key);
		}).toArray(String[]::new);

		try {
			if(keys.length == 1) {
				cache.invalidate(myKeys[0]);
			}else {
				cache.invalidateAll(Arrays.asList(myKeys));
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}


	}

	@Override
	public boolean exists(String key) {
		return get(key) != null;
	}


	/**
	 * 毫秒
	 * @param key
	 * @param items
	 */
	@Override
	public void addListItems(String key, String... items) {
		if (items.length==0) return;

		key = RedisKeyUtils.buildNameSpaceKey(key);

		List values = Arrays.asList(items);

		try {
			Object object = cache.getIfPresent(key);
			ExpireableObject expireableObject = null;
			if (object==null) {
				expireableObject = new ExpireableObject(values, -1,TimeUnit.MILLISECONDS);
				this.set(key,expireableObject);
			}else if (object instanceof NullValue){
				expireableObject = new ExpireableObject(values, -1,TimeUnit.MILLISECONDS);
				this.set(key,expireableObject);
			}else{
				expireableObject = (ExpireableObject) object;
				((List)expireableObject.getTarget()).addAll(values);
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}


	}



	@Override
	public List<Object> getListItems(String key, int start, int end) {
		List<Object> list = get(key);
		if(list == null)return new ArrayList<>(0);
		if(list.size() <= end + 1)return list;
		return list.subList(start, end);
	}

	@Override
	public long getListSize(String key) {
		List<String> list = get(key);
		return list == null ? 0 : list.size();
	}

	@Override
	public boolean setIfAbsent(String key, String value, long expire) {
		if(exists(key))return false;
		set(key, value, expire);
		return true;
	}


	@Override
	public boolean setIfAbsent(String key, String value, long expire, TimeUnit timeUnit) {
		if(exists(key))return false;
		set(key, value, expire,timeUnit);
		return true;
	}


	@Override
	public void setMapItem(String key, String field, Object value)  {
		if (value==null) return;

		Map<String,Object> values = new HashMap(){{put(field,value);}};

		setMapItem(key,values);
	}

	@Override
	public void setMapItem(String key, Map<String, Object> values)  {
		if (values==null || values.size()==0) return;

		key = RedisKeyUtils.buildNameSpaceKey(key);
		try {
			Object object = cache.getIfPresent(key);
			ExpireableObject expireableObject = null;
			if (object==null) {
				expireableObject = new ExpireableObject(values, -1,TimeUnit.MILLISECONDS);
				this.set(key,expireableObject);
			}else if (object instanceof NullValue){
				expireableObject = new ExpireableObject(values, -1,TimeUnit.MILLISECONDS);
				this.set(key,expireableObject);
			}else{
				expireableObject = (ExpireableObject) object;
				((Map)expireableObject.getTarget()).putAll(values);
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}

	}

	@Override
	public Map<String, Object> getMap(String key) {
		return get(key);
	}

	@Override
	public Object getMapItem(String key, String field) {
		Map<String, Object> map = getMap(key);
		return map == null ? null : map.get(field);
	}

	@Override
	public void setExpire(String key, long expire) {
		setExpire(key,expire,TimeUnit.MILLISECONDS);
	}

	@Override
	public void setExpireAt(String key, Date expireAt) {
		key = RedisKeyUtils.buildNameSpaceKey(key);
		try {
			Object value = cache.getIfPresent(key);
			if(value == null)return;
			Object object = cache.getIfPresent(key);
			if (object==null) return;
			if (object instanceof ExpireableObject){
				synchronized (CaffeineCacheAdapter.class) {
					ExpireableObject expireableObject = (ExpireableObject) object;
					expireableObject.setExpireAt(expireAt.getTime());
				}
			}else{
				synchronized (CaffeineCacheAdapter.class) {
					ExpireableObject expireableObject = new ExpireableObject(object, -1, TimeUnit.MILLISECONDS);
					expireableObject.setExpireAt(expireAt.getTime());
					cache.put(key, expireableObject);
				}
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}

	}

	@Override
	public void setExpire(String key, long expire,TimeUnit timeUnit) {
		setExpireAt(key,new Date(System.currentTimeMillis() + timeUnit.toMillis(expire)));
	}

	@Override
	public long getTtl(String key) {
		key = RedisKeyUtils.buildNameSpaceKey(key);
		try {
			Object value = cache.getIfPresent(key);
			if(value == null)return 0;
			if(value instanceof ExpireableObject) {
				long diff = ((ExpireableObject)value).getExpireAt() - System.currentTimeMillis();
				return diff < 0 ? 0 : diff/1000;
			}
			return -1;
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return 0;
		}

	}

	public Cache<Object, Object> getCache() {
		return cache;
	}


}
