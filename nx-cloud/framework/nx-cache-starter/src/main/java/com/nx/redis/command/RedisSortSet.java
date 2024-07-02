/**
 * 
 */
package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.cache.utils.NxCacheJsonUtil;

import java.util.List;
import java.util.Set;

/**
 * redis操作可排序set
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月7日
 */
public class RedisSortSet extends RedisBinaryCollection {

	public RedisSortSet(String key) {
		super(key);
	}
	
	/**
	 * @param key
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisSortSet(String key,long expireTime) {
		super(key,expireTime);
	}
	
	/**
	 * 指定组名
	 * @param key
	 * @param groupName
	 */
	public RedisSortSet(String key,String groupName) {
		super(key,groupName);
	}
	
	/**
	 * 
	 * @param key
	 * @param groupName 分组名
	 * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
	 */
	public RedisSortSet(String key,String groupName,long expireTime) {
		super(key,groupName,expireTime);
	}
	
	/**
	 * 新增元素
	 * @param score 权重
	 * @param value  元素
	 * @return
	 */
	public long add(double score, Object value){
		try {
			long result = 0;
			result = JedisProviderFactory.getJedisCommands(groupName).zadd(key, score, NxCacheJsonUtil.toJson(value));
			//设置超时时间
			if(result > 0)setExpireIfNot(expireTime);
			return result;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//        try {
//        	long result = 0;
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zadd(keyBytes, score, valueSerialize(value));
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zadd(keyBytes, score, valueSerialize(value));
//        	}
//        	//设置超时时间
//        	if(result > 0)setExpireIfNot(expireTime);
//			return result;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}

	/**
	 * 删除有序集合中的一个成员
	 * @return
	 */
	public boolean remove(Object mem){
        try {
        	boolean result = false;
        	result = JedisProviderFactory.getJedisCommands(groupName).zrem(key,NxCacheJsonUtil.toJson(mem)) >= 1;
			return result;
    	} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//        try {
//        	boolean result = false;
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zrem(keyBytes,valueSerialize(mem)) >= 1;
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zrem(keyBytes,valueSerialize(mem)) >= 1;
//        	}
//			return result;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
	/**
	 * 查询集合成员数量
	 * @return
	 */
	public long count(){
		try {

			long count  = JedisProviderFactory.getJedisCommands(groupName).zcard(key) ;
			return count;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//        try {
//        	long count = 0;
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		count = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zcard(keyBytes);
//        	}else{
//        		count = JedisProviderFactory.getBinaryJedisCommands(groupName).zcard(keyBytes);
//        	}
//			return count;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
	
	
	/**ß
     * 获取全部列表
     * @return
     */
    public <T> List<T> get(Class<T> tClass){
    	return range(0, -1,tClass);
    }
    
    /**
     * 按指定区间取出元素列表
     * @param start
     * @param end
     * @return
     */
    public <T> List<T> range(long start,long end,Class<T> tClass){
		try {

			Set<String> values  = JedisProviderFactory.getJedisCommands(groupName).zrange(key,start,end) ;

			List<T> list = jsonSetToList(values,tClass);

			return list;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//    	Set<byte[]> result = null;
//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zrange(keyBytes, start, end);
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zrange(keyBytes, start, end);
//        	}
//        	return toObjectList(new ArrayList<>(result));
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
    
    /**
     * 按指定权重取出元素列表
     * @param min
     * @param max
     * @return
     */
    public <T> List<T> getByScore(double min,double max,Class<T> tClass){
		try {
			Set<String> values  = JedisProviderFactory.getJedisCommands(groupName).zrangeByScore(key,min, max) ;
			List<T> list = jsonSetToList(values,tClass);
			return list;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//    	Set<byte[]> result = null;
//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zrangeByScore(keyBytes, min, max);
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zrangeByScore(keyBytes, min, max);
//        	}
//        	return toObjectList(new ArrayList<>(result));
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
    
    /**
     * 按权重删除
     * @param min
     * @param max
     * @return
     */
    public long removeByScore(double min,double max){
		try {
			return JedisProviderFactory.getJedisCommands(groupName).zremrangeByScore(key,min, max) ;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//        try {
//        	long result = 0;
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zremrangeByScore(keyBytes, min, max);
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zremrangeByScore(keyBytes, min, max);
//        	}
//			return result;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
    
    public double getScore(Object value){
		try {
			Double result = JedisProviderFactory.getJedisCommands(groupName).zscore(key,NxCacheJsonUtil.toJson(value)) ;
			return result == null ?  -1 : result;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
//        try {
//        	Double result;
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zscore(keyBytes, valueSerialize(value));
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zscore(keyBytes, valueSerialize(value));
//        	}
//			return result == null ?  -1 : result;
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
    
    public long countByScore(double min,double max){
		try {
			Long result = JedisProviderFactory.getJedisCommands(groupName).zcount(key,min,max) ;
			return result == null ?  0 : result;
		} finally{
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

//    	Long result = null;
//        try {
//        	if(JedisProviderFactory.isCluster(groupName)){
//        		result = JedisProviderFactory.getBinaryJedisClusterCommands(groupName).zcount(keyBytes, min, max);
//        	}else{
//        		result = JedisProviderFactory.getBinaryJedisCommands(groupName).zcount(keyBytes, min, max);
//        	}
//        	return result == null ? 0 : result.longValue();
//    	} finally{
//			JedisProviderFactory.getJedisProvider(groupName).release();
//		}
	}
}
