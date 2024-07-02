/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.cache.utils.NxCacheJsonUtil;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * redis操作List
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisList extends RedisBinaryCollection {

	public RedisList(String key) {
		super(key);
	}
	
	/**
	 * @param key
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisList(String key,long expireTime) {
		super(key,expireTime);
	}

	/**
	 * 指定组名
	 * 
	 * @param key
	 * @param groupName
	 */
	public RedisList(String key, String groupName) {
		super(key, groupName);
	}
	
	/**
	 * 
	 * @param key
	 * @param groupName 分组名
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisList(String key,String groupName,long expireTime) {
		super(key,groupName,expireTime);
	}



	public long lpush(Object... objects) {
		try {
			long result = 0;
			String[] datas = objectJsonArray(objects);
			result = JedisProviderFactory.getJedisCommands(groupName).lpush(key,datas);

			//设置超时时间
			if(result>0)setExpireIfNot(expireTime);
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			long result = 0;
//			byte[][] datas = valuesSerialize(objects);
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).lpush(keyBytes, datas);
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).lpush(keyBytes, datas);
//			}
//			//设置超时时间
//			if(result>0)setExpireIfNot(expireTime);
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public long rpush(Object...objects) {
		try {
			long result = 0;
			String[] datas = objectJsonArray(objects);
			result = JedisProviderFactory.getJedisCommands(groupName).rpush(key,datas);

			//设置超时时间
			if(result > 0)setExpireIfNot(expireTime);
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			byte[][] datas = valuesSerialize(objects);
//			long result = 0;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).rpush(keyBytes, datas);
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).rpush(keyBytes, datas);
//			}
//			//设置超时时间
//			if(result > 0)setExpireIfNot(expireTime);
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public <T> T lpop(Class<T> tClass) {
		try {
			String ret = JedisProviderFactory.getJedisCommands(groupName).lpop(key);
			if (StringUtils.hasLength(ret)){
				return NxCacheJsonUtil.toObject(ret,tClass);
			}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		return null;
//		byte[] datas = null;
//		try {
//			if (JedisProviderFactory.isCluster(groupName)) {
//				datas = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).lpop(keyBytes);
//			} else {
//				datas = JedisProviderFactory.getBinaryJedisCommands(groupName).lpop(keyBytes);
//			}
//			return valueDerialize(datas);
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	public <T> T rpop(Class<T> tClass) {
		try {
			String ret = JedisProviderFactory.getJedisCommands(groupName).rpop(key);
			if (StringUtils.hasLength(ret)){
				return NxCacheJsonUtil.toObject(ret,tClass);
			}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		return null;

//		try {
//
//			byte[] datas = null;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				datas = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).rpop(keyBytes);
//			} else {
//				datas = JedisProviderFactory.getBinaryJedisCommands(groupName).rpop(keyBytes);
//			}
//			return valueDerialize(datas);
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 获取全部列表
	 * 
	 * @return
	 */
	public <T> List<T> get(Class<T> tClass) {
		return range(0, -1,tClass);
	}

	public <T> List<T> range(int start, int end,Class<T> tClass) {
		try {
			List<String> result = JedisProviderFactory.getJedisCommands(groupName).lrange(key, start, end);
			return jsonListToList(result,tClass);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			List<byte[]> result = null;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).lrange(keyBytes, start, end);
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).lrange(keyBytes, start, end);
//			}
//			return toObjectList(result);
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 返回长度
	 * 
	 * @return
	 */
	public long length() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).llen(key);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			if (JedisProviderFactory.isCluster(groupName)) {
//				return JedisProviderFactory.getBinaryJedisClusterCommands(groupName).llen(keyBytes);
//			} else {
//				return JedisProviderFactory.getBinaryJedisCommands(groupName).llen(keyBytes);
//			}
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 设置指定位置的值
	 * 
	 * @param index
	 * @param newValue
	 * @return
	 */
	public boolean set(long index, Object newValue) {
		try {
			boolean result = false;
			String str = NxCacheJsonUtil.toJson(newValue);
			result =  JedisProviderFactory.getJedisCommands(groupName).lset(key, index,str).equals(RESP_OK);
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			boolean result = false;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).lset(keyBytes, index, valueSerialize(newValue))
//						.equals(RESP_OK);
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).lset(keyBytes, index, valueSerialize(newValue)).equals(RESP_OK);
//			}
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 移除(所有)指定值元素
	 * @param value
	 * @return
	 */
	public boolean removeValue(Object value) {
		try {
			boolean result = false;
			String str = NxCacheJsonUtil.toJson(value);
			result =  JedisProviderFactory.getJedisCommands(groupName).lrem(key, 0,str)>=1;
			return result;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//		try {
//			boolean result = false;
//			if (JedisProviderFactory.isCluster(groupName)) {
//				result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).lrem(keyBytes, 0, valueSerialize(value)) >= 1;
//			} else {
//				result = JedisProviderFactory.getBinaryJedisCommands(groupName).lrem(keyBytes, 0, valueSerialize(value)) >= 1;
//			}
//			return result;
//		} finally {
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
}
