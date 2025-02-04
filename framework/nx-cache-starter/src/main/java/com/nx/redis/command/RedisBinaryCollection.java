/**
 * 
 */
package com.nx.redis.command;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合操作基类
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public abstract class RedisBinaryCollection extends RedisBase {

	protected long expireTime;//过期时间（秒）

	public RedisBinaryCollection(String key) {
		this(key, getDefaultExpireSeconds());
	}
	
	/**
	 * 指定组名
	 * @param key
	 * @param groupName
	 */
	public RedisBinaryCollection(String key,String groupName) {
		this(key,groupName, getDefaultExpireSeconds());
	}
	
	public RedisBinaryCollection(String key,long expireTime) {
		super(key);
		this.expireTime = expireTime;
	}
	
	public RedisBinaryCollection(String key,String groupName,long expireTime) {
		super(key,groupName);
		this.expireTime = expireTime;
	}
	
	protected <T> List<T> toObjectList(List<byte[]> datas) {
		List<T> result = new ArrayList<>();
    	if(datas == null)return result;
    	for (byte[] data : datas) {
			result.add((T)valueDerialize(data));
		}
		return result;
	}
	

}
