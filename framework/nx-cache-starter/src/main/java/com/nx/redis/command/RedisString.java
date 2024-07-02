/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 字符串redis操作命令
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisString extends RedisBase{
	
	public RedisString(String key) {
		super(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param groupName 组名
	 */
	public RedisString(String key,String groupName) {
		super(key, groupName);
	}

	/**
	 * 重置key（适合一个方法里面频繁操作不同缓存的场景）<br>
	 * <font color="red">非线程安全，请不要在多线程场景使用</font>
	 * 
	 * @param key
	 * @return
	 */
	public RedisString resetKey(String key) {
		this.key = key;
		return this;
	}
	
	/**
	 * 设置缓存，默认过期时间
	 * @param value
	 * @return
	 */
	public boolean set(String value){
		return set(value, getDefaultExpireSeconds());
	}
	
	/**
	 * 设置缓存指定过期时间间隔
	 * @param value
	 * @param seconds (过期秒数 ，小于等于0时 不设置)
	 * @return
	 */
	public boolean set(String value, long seconds) {

		if (value == null)
			return false;
		try {
			boolean result = JedisProviderFactory.getJedisCommands(groupName).set(key, value).equals(RESP_OK);
			if(result && seconds > 0){
				result =  setExpire(seconds);
				//set可能是更新缓存，所以统一通知各节点清除本地缓存
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		
	}

	/**
	 * 检查给定 key 是否存在。
	 * @return
	 */
	public boolean set(String value, Date expireAt) {
		if (value == null)
			return false;
		try {
			boolean result = JedisProviderFactory.getJedisCommands(groupName).set(key, value).equals(RESP_OK);
			if(result){
				result = setExpireAt(expireAt);
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	public String get() {
		try {
			String value = JedisProviderFactory.getJedisCommands(groupName).get(key);
			return value;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		
	}


	public boolean setnx(String value, long expireSeconds) {
		try {
			Long result = JedisProviderFactory.getJedisCommands(groupName).setnx(key, value);
			if(result > 0)setExpire(expireSeconds);
			return result > 0;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
}
