package com.nx.cache.adapter;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CacheAdapter  {

	<T> T get(String key);

	<T> T get(String key,Class<T> tClass);

	void set(String key, Object value, long expire, TimeUnit timeUnit);

	void set(String key, Object value, boolean isAllowNull, long expire, TimeUnit timeUnit);

	void set(String key, Object value);

	void set(String key, Object value, long expireSeconds);

	void remove(String...keys);

	boolean exists(String key);

	void addListItems(String key,String ...items);

	List<Object> getListItems(String key, int start, int end);

	long getListSize(String key);

	boolean setIfAbsent(String key,String value,long expire);

	void setMapItem(String key,String field,Object value);

	void setMapItem(String key, Map<String, Object> values);

	boolean setIfAbsent(String key, String value, long expire, TimeUnit timeUnit);

	Map<String, Object> getMap(String key);

	Object getMapItem(String key, String field);

	void setExpire(String key, long expire);

	void setExpireAt(String key,Date expireAt);

	void setExpire(String key, long expire, TimeUnit timeUnit);

	long getTtl(String key);


}
