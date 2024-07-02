/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.cache.utils.NxCacheJsonUtil;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 对象redis操作对象（通过二进制序列化缓存）
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisObject extends RedisBase {


	/**
	 * @param key
	 */
	public RedisObject(String key) {
		super(key);
	}
	
	/**
	 * 指定组名
	 * @param key
	 * @param groupName
	 */
	public RedisObject(String key,String groupName) {
		super(key,groupName);
	}
	
	/**
	 * 重置key（适合一个方法里面频繁操作不同缓存的场景）<br>
	 * <font color="red">非线程安全，请不要在多线程场景使用</font>
	 * @param key
	 * @return
	 */
	public RedisObject resetKey(String key){
		this.key = key;
		return this;
	}
	
	/**
	 * 设置缓存，默认过期时间
	 * @param value
	 * @return
	 */
	public boolean set(Object value){
		return set(value, getDefaultExpireSeconds());
	}
	
	/**
	 * 设置缓存指定过期时间间隔
	 * @param value
	 * @param seconds (过期秒数 ，小于等于0时 不设置)
	 * @return
	 */
	public boolean set(Object value, long seconds) {
		if (value == null)
			return false;
		try {
			String valueStr = NxCacheJsonUtil.toJson(value);
			boolean result = false;
			result = JedisProviderFactory.getJedisCommands(groupName).set(key, valueStr).equals(RESP_OK);

//			if(JedisProviderFactory.isCluster(groupName)){
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
//			}else{
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
//			}
			if(result){
				result =  setExpire(seconds);
				//set可能是更新缓存，所以统一通知各节点清除本地缓存
				//CacheProviderFactory.getInstance().getCache(groupName).nativeLocalCache().set(key,value,seconds);
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		
	}

	/**
	 * 检查给定 key 是否存在。
	 * 
	 * @param expireAt
	 * @return
	 */
	public boolean set(Object value, Date expireAt) {
		if (value == null)
			return false;
		if (value == null)
			return false;
		try {
			String valueStr = NxCacheJsonUtil.toJson(value);
			boolean result = false;
			result = JedisProviderFactory.getJedisCommands(groupName).set(key, valueStr).equals(RESP_OK);
			if(result){
				result =  setExpireAt(expireAt);
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			boolean result = false;
//			if(JedisProviderFactory.isCluster(groupName)){
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);;
//			}else{
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
//			}
//			if(result){
//				result = setExpireAt(expireAt);
//				//set可能是更新缓存，所以统一通知各节点清除本地缓存
//				CacheProviderFactory.getInstance().getCache(groupName).nativeLocalCache().set(key,value);
//				CacheProviderFactory.getInstance().getCache(groupName).nativeLocalCache().setExpireAt(key,expireAt);
//				//Level1CacheSupport.getInstance().publishSyncEvent(key);
//			}
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
	
	
	public <T> T get(Class<T> tClass) {
		try {
			String valueStr = JedisProviderFactory.getJedisCommands(groupName).get(key);
			if (StringUtils.hasLength(valueStr)){
				return NxCacheJsonUtil.toObject(valueStr,tClass);
			}
			return null;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			//本地缓存读取
//			T value = CacheProviderFactory.getInstance().getCache(groupName).nativeLocalCache().get(this.key);
//			if(value != null)return value;
//
//			byte[] bytes = null;
//			if(JedisProviderFactory.isCluster(groupName)){
//				bytes = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).get(keyBytes);
//			}else{
//				bytes = JedisProviderFactory.getBinaryJedisCommands(groupName).get(keyBytes);
//			}
//			value = valueDerialize(bytes);
//			//local
//			//Level1CacheSupport.getInstance().set(this.key, value);
//			CacheProviderFactory.getInstance().getCache(groupName).nativeLocalCache().set(key,value);
//			return value;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
//
	}

	

}
