/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.cache.utils.NxCacheJsonUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 对象redis操作set
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisSet extends RedisBinaryCollection {

	public RedisSet(String key) {
		super(key);
	}
	
	/**
	 * @param key
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisSet(String key,long expireTime) {
		super(key,expireTime);
	}
	
	/**
	 * 指定组名
	 * @param key
	 * @param groupName
	 */
	public RedisSet(String key,String groupName) {
		super(key,groupName);
	}
	
	/**
	 * 
	 * @param key
	 * @param groupName 分组名
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisSet(String key,String groupName,long expireTime) {
		super(key,groupName,expireTime);
	}
	
	public long add(Object... objects) {
		try {
			long result = 0;
			String[] datas = objectJsonArray(objects);
			result = JedisProviderFactory.getJedisCommands(groupName).sadd(key,datas);
			//设置超时时间
			if(result > 0){
				setExpireIfNot(expireTime);
			}
			return result;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//        try {
//        	long result = 0;
//        	byte[][] datas = valuesSerialize(objects);
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).sadd(keyBytes,datas);
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).sadd(keyBytes,datas);
//        	}
//        	//设置超时时间
//        	if(result > 0)setExpireIfNot(expireTime);
//			return result;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public <T> Set<T> get(Class<T> tClass) {
        try {
			Set<String> strings = JedisProviderFactory.getJedisCommands(groupName).smembers(key);
			return jsonSetToSet(strings,tClass);
    	} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		Set<byte[]> datas = null;
//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		datas = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).smembers(keyBytes);
//        	}else{
//        		datas = JedisProviderFactory.getBinaryJedisCommands(groupName).smembers(keyBytes);
//        	}
//        	return toObjectSet(datas);
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	
	public boolean remove(Object... objects) {
		try {
			String[] datas = objectJsonArray(objects);
			return JedisProviderFactory.getJedisCommands(groupName).srem(key,datas)>0;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//        try {
//        	byte[][] datas = valuesSerialize(objects);
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		return JedisProviderFactory.getBinaryJedisClusterCommands(groupName).srem(keyBytes,datas) == 1;
//        	}else{
//        		return JedisProviderFactory.getBinaryJedisCommands(groupName).srem(keyBytes,datas) == 1;
//        	}
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public long length() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).scard(key);
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		return JedisProviderFactory.getBinaryJedisClusterCommands(groupName).scard(keyBytes);
//        	}else{
//        		return JedisProviderFactory.getBinaryJedisCommands(groupName).scard(keyBytes);
//        	}
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public boolean contains(Object object) {
		try {
			//String[] datas = objectJsonArray(object);
			String data = NxCacheJsonUtil.toJson(object);
			return JedisProviderFactory.getJedisCommands(groupName).sismember(key,data);
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		return JedisProviderFactory.getBinaryJedisClusterCommands(groupName).sismember(keyBytes, valueSerialize(object));
//        	}else{
//        		return JedisProviderFactory.getBinaryJedisCommands(groupName).sismember(keyBytes, valueSerialize(object));
//        	}
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
	
	public boolean containsAny(Object... objects) {
		try {
			String[] datas = objectJsonArray(objects);
			boolean result;
			for (String object : datas) {
				result = JedisProviderFactory.getJedisCommands(groupName).sismember(key, object);
				if(result) return result;
			}
			return false;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//        try {
//        	boolean result;
//        	for (Object object : objects) {
//        		if(JedisProviderFactory.isCluster(groupName)){
//        			result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).sismember(keyBytes, valueSerialize(object));
//            	}else{
//            		result = JedisProviderFactory.getBinaryJedisCommands(groupName).sismember(keyBytes, valueSerialize(object));
//            	}
//        		if(result)return result;
//			}
//        	return false;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	protected <T> Set<T> toObjectSet(Set<byte[]> datas) {
		Set<T> result = new HashSet<>();
    	if(datas == null)return result;
    	for (byte[] data : datas) {
			result.add((T)valueDerialize(data));
		}
		return result;
	}
}
