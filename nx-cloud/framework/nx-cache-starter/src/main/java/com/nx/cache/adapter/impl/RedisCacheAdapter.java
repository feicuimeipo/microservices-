package com.nx.cache.adapter.impl;

import com.nx.cache.utils.NullValue;
import com.nx.cache.adapter.CacheAdapter;
import com.nx.redis.utils.RedisKeyUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class RedisCacheAdapter implements CacheAdapter {

	private static final Logger log = LoggerFactory.getLogger(RedisCacheAdapter.class);

	private RedisTemplate<String, Object> redisTemplate;
	private StringRedisTemplate stringRedisTemplate;

	public RedisCacheAdapter() {}

	public RedisCacheAdapter(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public <T> T get(String key) {
		try {
			return (T) redisTemplate.opsForValue().get(key);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}


	@Override
	public <T> T get(String key,Class<T> tClass) {
		try {
			return (T) redisTemplate.opsForValue().get(key);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}


	@Override
	public void set(String key, Object value, long expire) {
		set(key,value,false,expire,TimeUnit.MILLISECONDS);
	}

	@Override
	public void set(String key, Object value, long expire,TimeUnit timeUnit) {
		set(key,value,false,expire,timeUnit);
	}

	@Override
	public void set(String key, Object value, boolean isAllowNull, long expire, TimeUnit timeUnit) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		if (value==null && isAllowNull){
			value = NullValue.INSTANCE;
		}
		try {
			if (expire == -1) {
				redisTemplate.opsForValue().set(key, value);
			} else {
				redisTemplate.opsForValue().set(key, value, expire, timeUnit);
			}
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}


	@Override
	public void set(String key, Object value) {
		set(key,value,false,-1,TimeUnit.MILLISECONDS);
	}



	@Override
	public void remove(String... keys) {
		String[] myKeys = Arrays.stream(keys).map(key->{
			return RedisKeyUtils.buildNameSpaceKey(key);
		}).toArray(String[]::new);

		try {
			if (keys.length == 1) {
				redisTemplate.delete(myKeys[0]);
			}
			redisTemplate.delete(Arrays.asList(myKeys));
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public boolean exists(String key) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			return redisTemplate.hasKey(key);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
	}

	@Override
	public void addListItems(String key, String... items) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			redisTemplate.opsForList().leftPushAll(key, items);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}



	@Override
	public List<Object> getListItems(String key, int start, int end) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			return redisTemplate.opsForList().range(key, start, end);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public long getListSize(String key) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			return redisTemplate.opsForList().size(key);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return 0;
		}
	}

	@Override
	public boolean setIfAbsent(String key, String value, long expire) {
		return setIfAbsent(key,value,expire,TimeUnit.MILLISECONDS);

	}

	@Override
	public boolean setIfAbsent(String key, String value, long expire, TimeUnit timeUnit) {
		//key = RedisCacheKey.buildNameSpaceKey(key);

		try {
			return redisTemplate.opsForValue().setIfAbsent(key, value, expire, timeUnit);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return false;
		}

	}
	
	@Override
	public Map<String, Object> getMap(String key) {
		//key = RedisCacheKey.buildNameSpaceKey(key);

		try {
			Map<?,?> entries = redisTemplate.opsForHash().entries(key);
			Set<?> fields = entries.keySet();

			Map<String, Object> result = new HashMap<>(fields.size());
			for (Object field : fields) {
				result.put(field.toString(), entries.get(field));
			}
			return result;
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return Collections.emptyMap();
		}
	}

	@Override
	public void setMapItem(String key, String field, Object value) {
		//key = RedisCacheKey.buildNameSpaceKey(key);

		try {
			stringRedisTemplate.opsForHash().put(key, field, value);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public void setMapItem(String key, Map<String, Object> values) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			stringRedisTemplate.opsForHash().putAll(key,values);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public Object getMapItem(String key, String field) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			Object value = stringRedisTemplate.opsForHash().get(key, field);
			return value == null ? null : value.toString();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
	}

	@Override
	public void setExpire(String key, long expire) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public void setExpireAt(String key, Date expireAt) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			redisTemplate.expireAt(key, expireAt);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public void setExpire(String key, long expire, TimeUnit timeUnit) {
		//key = RedisCacheKey.buildNameSpaceKey(key);
		try {
			redisTemplate.expire(key, expire,timeUnit);
		}catch (Exception e){
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public long getTtl(String key) {
		return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
	}




}
