package com.nx.redis.command;

import com.nx.redis.JedisProviderFactory;
import com.nx.redis.RedisConfigFactory;
import com.nx.redis.serializer.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年12月9日
 */
public class RedisBatchCommand {
	protected static final Logger logger = LoggerFactory.getLogger(RedisBatchCommand.class);
	protected static final String RESP_OK = "OK";

	/**
	 * 指定组批量写入字符串
	 * @param groupName 缓存组
	 * @param keyValueMap 
	 * @return
	 */
	public static boolean setStringsWithGroup(String groupName,Map<String, Object> keyValueMap){
		if(keyValueMap == null || keyValueMap.isEmpty())return false;
		String[] keysValues = new String[keyValueMap.size() * 2];
		int index = 0;
		for (String key : keyValueMap.keySet()) {
			if(keyValueMap.get(key) == null)continue;
			keysValues[index++] = key;
			keysValues[index++] = keyValueMap.get(key).toString();
		}
		try {			
			if(JedisProviderFactory.isCluster(groupName)){
				return JedisProviderFactory.getMultiKeyJedisClusterCommands(groupName).mset(keysValues).equals(RESP_OK);
			}else{
				return JedisProviderFactory.getMultiKeyCommands(groupName).mset(keysValues).equals(RESP_OK);
			}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	/**
	 * 默认组批量写入字符串
	 * @param keyValueMap
	 * @return
	 */
	public static boolean setStrings(Map<String, Object> keyValueMap){
		return setStringsWithGroup(null, keyValueMap);
	}
	
	/**
	 * 指定组批量写入对象
	 * @param groupName 缓存组
	 * @param keyValueMap 
	 * @return
	 */
	public static boolean setObjectsWithGroup(String groupName,Map<String, Object> keyValueMap){
		if(keyValueMap == null || keyValueMap.isEmpty())return false;
		byte[][] keysValues = new byte[keyValueMap.size() * 2][];
		int index = 0;
		for (String key : keyValueMap.keySet()) {
			if(keyValueMap.get(key) == null)continue;
			keysValues[index++] = SafeEncoder.encode(key);
			keysValues[index++] = SerializeUtils.serialize(keyValueMap.get(key),groupName);
		}
		
        try {			
        	if(JedisProviderFactory.isCluster(groupName)){
        		return JedisProviderFactory.getMultiKeyBinaryJedisClusterCommands(groupName).mset(keysValues).equals(RESP_OK);
        	}else{
        		return JedisProviderFactory.getMultiKeyBinaryCommands(groupName).mset(keysValues).equals(RESP_OK);
        	}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	/**
	 * 默认组批量写入对象
	 * @param keyValueMap
	 * @return
	 */
	public static boolean setObjects(Map<String, Object> keyValueMap){
		return setObjectsWithGroup(null, keyValueMap);
	}
	
	/**
	 * 按key批量从redis获取值（指定缓存组名）
	 * @param groupName
	 * @param keys
	 * @return list<String>
	 */
	public static List<String> getStringsWithGroup(String groupName,String...keys){
        try {
        	if(JedisProviderFactory.isCluster(groupName)){
        		return JedisProviderFactory.getMultiKeyJedisClusterCommands(groupName).mget(keys);
        	}else{
        		return JedisProviderFactory.getMultiKeyCommands(groupName).mget(keys);
        	}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}

	public static List<String> getStrings(String...keys){
		return getStringsWithGroup(null, keys);
	}
	
	public static boolean removeStringsWithGroup(String groupName,String...keys){
        try {			
        	if(JedisProviderFactory.isCluster(groupName)){
        		return JedisProviderFactory.getMultiKeyJedisClusterCommands(groupName).del(keys) == 1;
        	}else{
        		return JedisProviderFactory.getMultiKeyCommands(groupName).del(keys) == 1;
        	}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
    public static boolean removeStrings(String...keys){
    	return removeStringsWithGroup(null, keys);
	}
    
    
    public static boolean removeObjectsWithGroup(String groupName,String...keys){
    	byte[][] byteKeys = SafeEncoder.encodeMany(keys);
        try {			
        	if(JedisProviderFactory.isCluster(groupName)){
        		return JedisProviderFactory.getMultiKeyBinaryJedisClusterCommands(groupName).del(byteKeys) == 1;
        	}else{
        		return JedisProviderFactory.getMultiKeyBinaryCommands(groupName).del(byteKeys) == 1;
        	}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
    public static boolean removeObjects(String...keys){
    	return removeObjectsWithGroup(null, keys);
	}
	
	public static <T> List<T> getObjectsWithGroup(String groupName, String... keys) {
		byte[][] byteKeys = SafeEncoder.encodeMany(keys);

		try {
			if (JedisProviderFactory.isCluster(groupName)) {
				List<byte[]> bytes = JedisProviderFactory.getMultiKeyBinaryJedisClusterCommands(groupName)
						.mget(byteKeys);
				return listDerialize(bytes,groupName);
			} else {
				List<byte[]> bytes = JedisProviderFactory.getMultiKeyBinaryCommands(groupName).mget(byteKeys);
				return listDerialize(bytes,groupName);
			}
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	public static <T> List<T> getObjects(String...keys){
		return getObjectsWithGroup(null, keys);
	}
	
	public static void removeByKeyPrefix(String keyPrefix){
		removeByKeyPrefix(null, keyPrefix);
	}
	
	public static void removeByKeyPrefix(String group,String keyPrefix){
		try {			
			Set<String> keys = JedisProviderFactory.getMultiKeyCommands(group).keys(keyPrefix +"*");
			if(keys != null && keys.size() > 0){
				RedisBatchCommand.removeObjectsWithGroup(group,keys.toArray(new String[0]));
			}
		} finally {
			JedisProviderFactory.getJedisProvider(group).release();
		}
	}

	private static <T> T valueDerialize(byte[] bytes,String groupName) {
		if(bytes == null)return null;
		try {
			return (T)SerializeUtils.deserialize(bytes, RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType());
		} catch (Exception e) {
			return null;
		}
	}
	
	private static <T> List<T> listDerialize(List<byte[]> datas,String groupName) {
		List<T> list = new ArrayList<>();
		if(datas == null)return list;
         for (byte[] bs : datas) {
        	 list.add((T)valueDerialize(bs, RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType()));
		}
		return list;
	}
}
