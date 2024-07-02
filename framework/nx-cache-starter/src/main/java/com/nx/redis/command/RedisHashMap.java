/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.cache.utils.NxCacheJsonUtil;

import java.util.*;

/**
 * redis操作hashmap
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisHashMap extends RedisBinaryCollection {

	public RedisHashMap(String key) {
		super(key);
	}
	
	/**
	 * @param key
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisHashMap(String key,long expireTime) {
		super(key,expireTime);
	}

	/**
	 * 指定组名
	 * 
	 * @param key
	 * @param groupName
	 */
	public RedisHashMap(String key, String groupName) {
		super(key, groupName);
	}
	
	/**
	 * 
	 * @param key
	 * @param groupName 分组名
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisHashMap(String key,String groupName,long expireTime) {
		super(key,groupName,expireTime);
	}

	/**
	 * 设置hash缓存
	 * 
	 * @param datas
	 * @return
	 */
	public <T> boolean set(Map<String, T> datas) {
		if(datas == null || datas.isEmpty())return false;
		Map<String, String> newDatas = new HashMap<>();
		Set<String> keySet = datas.keySet();
		for (String key : keySet) {
			if(datas.get(key) == null)continue;
			newDatas.put(key,NxCacheJsonUtil.toJson(datas.get(key)));
		}

		boolean result = false;
		try {
			result =  JedisProviderFactory.getJedisCommands(groupName).hmset(key,newDatas).equals(RESP_OK);
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).hmset(keyBytes, newDatas).equals(RESP_OK);
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).hmset(keyBytes, newDatas).equals(RESP_OK);
//			}
			//设置超时时间
			if(result)setExpireIfNot(expireTime);
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

	}
	
	/**
	 * 获取所有值
	 * 
	 * @return
	 */
	public <T> Map<String, T> getAll(Class<T> clz) {
		try {
//			Map<byte[], byte[]> datas = null;
//			Map<String, T> result = null;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				datas = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).hgetAll(keyBytes);
//			} else {
//				datas = JedisProviderFactory.getBinaryJedisCommands(groupName).hgetAll(keyBytes);
//			}
//			result = new HashMap<>(datas.size());
//			Iterator<Map.Entry<byte[], byte[]>> it = datas.entrySet().iterator();
//			while(it.hasNext()){
//				Map.Entry<byte[], byte[]> entry=it.next();
//				result.put(SafeEncoder.encode(entry.getKey()), (T)valueDerialize(entry.getValue()));
//			}

			Map<String, String> datas =  JedisProviderFactory.getJedisCommands(groupName).hgetAll(key);
			Map<String, T> result = new HashMap<>(datas.size());
			Iterator<Map.Entry<String, String>> it = datas.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, String> entry=it.next();
				result.put(key, NxCacheJsonUtil.toObject(entry.getValue(),clz));
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

	}

	/**
	 * 查看缓存hash是否包含某个key
	 * 
	 * @param field
	 * @return
	 */
	public boolean containsKey(String field) {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).hexists(key,field);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			if (JedisProviderFactory.isCluster(groupName)) {
//				return JedisProviderFactory.getBinaryJedisClusterCommands(groupName).hexists(keyBytes, SafeEncoder.encode(field));
//			} else {
//				return JedisProviderFactory.getBinaryJedisCommands(groupName).hexists(keyBytes, SafeEncoder.encode(field));
//			}
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 设置ç
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean set(String field, Object value) {
		if(value == null)return false;
		//返回值（1:新字段被设置,0:已经存在值被更新）
		try {
			String valueStr = NxCacheJsonUtil.toJson(value);
			long ret = JedisProviderFactory.getJedisCommands(groupName).hset(key,field,valueStr);
			if(ret>=0){
				setExpireIfNot(expireTime);
				return true;
			}
			return false;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		boolean result = false;
//		if(value == null)return false;
		//返回值（1:新字段被设置,0:已经存在值被更新）
//		try {
//			byte[] valueBytes = SerializeUtils.serialize(value, RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType());
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName)
//						.hset(keyBytes, SafeEncoder.encode(field), valueSerialize(value)) >= 0;
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).hset(keyBytes, SafeEncoder.encode(field), valueSerialize(value)) >= 0;
//			}
//			//设置超时时间
//			if(result)setExpireIfNot(expireTime);
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 移除hash缓存中的指定值
	 * 
	 * @param field
	 * @return
	 */
	public boolean remove(String field) {
		try {
			long ret = JedisProviderFactory.getJedisCommands(groupName).hdel(key,field);
			return ret>0;

		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}

	/**
	 * 返回长度
	 * 
	 * @return
	 */
	public long length() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).hlen(key);

		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}

	/**
	 * 获取一个值
	 * 
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOne(String field) {
		return (T) get(field).get(field);
	}

	/**
	 * 获取多个key的值
	 * 
	 * @param fields
	 * @return
	 */
	public <T> Map<String, T> get(String... fields) {
		try {
			List<String> datas = null;
			Map<String, T> result = new HashMap<>();

			datas = JedisProviderFactory.getJedisCommands(key).hmget(key,fields);

			for (int i = 0; i < fields.length; i++) {
				result.put(fields[i], (T) valueDerialize(datas.get(i).getBytes()));
			}
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

	}

}
