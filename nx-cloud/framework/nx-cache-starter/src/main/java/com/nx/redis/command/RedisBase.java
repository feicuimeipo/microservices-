package com.nx.redis.command;

import com.nx.cache.utils.CacheExpiresUtils;
import com.nx.cache.utils.NxCacheJsonUtil;
import com.nx.redis.JedisProviderFactory;
import com.nx.redis.RedisConfigFactory;
import com.nx.redis.utils.RedisKeyUtils;
import com.nx.redis.serializer.SerializeUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import static com.nx.redis.RedisConfigFactory.DEFAULT_GROUP_NAME;


/**
 * redis基础操作指令
 */
@Data
public abstract class RedisBase {
	protected static final Logger logger = LoggerFactory.getLogger(RedisBase.class);
	protected static final String RESP_OK = "OK";
    protected String groupName;
	protected String key;

	public RedisBase(String key) {
		this(key,DEFAULT_GROUP_NAME);
	}

	public RedisBase(String key,String groupName) {
		this.groupName = groupName;
		if(JedisProviderFactory.getJedisProvider(groupName).tenantEnabled()){
			this.key = RedisKeyUtils.buildNameSpaceKey(key);
		}else{
			this.key = key;
		}
	}
	
	/**
	 * 检查给定 key 是否存在。
	 * @return
	 */
	public boolean exists() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).exists(key);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		
	}
	

	/**
	 * 删除给定的一个 key 。
	 * 
	 * 不存在的 key 会被忽略。
	 * 
	 * @return true：存在该key删除时返回
	 * 
	 *         false：不存在该key
	 */
	public boolean remove() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).del(key) == 1;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 * 
	 * @param seconds
	 *            超时时间，单位：秒
	 * @return true：超时设置成功
	 * 
	 *         false：key不存在或超时未设置成功
	 */
	public boolean setExpire(long seconds) {
		if(seconds <= 0)return true;
		try {
			return JedisProviderFactory.getJedisCommands(groupName).expire(key, (int)seconds) == 1;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}

	}

	/**
	 * 
	 * 设置指定时间戳时失效
	 *
	 * 注意：redis服务器时间问题
	 * 
	 * @param expireAt
	 *            超时时间点
	 * @return true：超时设置成功
	 *
	 *         false：key不存在或超时未设置成功
	 */
	public boolean setExpireAt(Date expireAt) {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).pexpireAt(key, expireAt.getTime()) == 1;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	/**
	 * 没设置过期时间则设置
	 * @param seconds
	 * @return
	 */
	public boolean setExpireIfNot(long seconds) {
		if(seconds <= 0)return true;
		Long ttl = getTtl();
		if(ttl == -1){
			return setExpire(seconds);
		}
		return ttl >= 0;
	}

	/**
	 * 返回给定 key 的剩余生存时间(单位：秒)
	 * @return 当 key 不存在时，返回 -2 。
	 *         当 key 存在但没有设置剩余生存时间时，返回 -1 。
	 *         否则返回 key的剩余生存时间。
	 */
	public Long getTtl() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).ttl(key);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
		
	}

	/**
	 * 移除给定 key 的生存时间，设置为永久有效
	 * 
	 * @return 当生存时间移除成功时，返回 1 .
	 * 
	 *         如果 key 不存在或 key 没有设置生存时间，返回 0 。
	 */
	public boolean removeExpire() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).persist(key) == 1;
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}

	/**
	 * 返回 key 所储存的值的类型。
	 * 
	 * @return none (key不存在)
	 *         string (字符串)
	 *         list (列表)
	 *         set (集合)
	 *         zset (有序集)
	 *         hash (哈希表)
	 */
	public String type() {
		try {
			return JedisProviderFactory.getJedisCommands(groupName).type(key);
		} finally {
			JedisProviderFactory.getJedisProvider(groupName).release();
		}
	}
	
	/**
	 * 查找所有符合给定模式 pattern 的 key
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern){
		Set<String> keys = JedisProviderFactory.getMultiKeyCommands(groupName).keys(pattern);
		return keys;
	}

	protected byte[] valueSerialize(Object value) {
		try {
			return SerializeUtils.serialize(value, RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected byte[][] valuesSerialize(Object... objects) {
		try {
			byte[][] many = new byte[objects.length][];
			for (int i = 0; i < objects.length; i++) {
				many[i] = SerializeUtils.serialize(objects[i], RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType());
			}
			return many;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T valueDerialize(byte[] bytes) {
		if(bytes == null)return null;
		try {
			return (T)SerializeUtils.deserialize(bytes, RedisConfigFactory.getRedisConfig(groupName).getValueSerializerType());
		} catch (Exception e) {
			remove();
			logger.warn("get key[{}] from redis is not null,but Deserialize error,message:{}",key,e);
			return null;
		}
	}
	
	protected <T> List<T> listDerialize(List<byte[]> datas){
		List<T> list = new ArrayList<>();
		if(datas == null)return list;
         for (byte[] bs : datas) {
        	 list.add((T)valueDerialize(bs));
		}
		return list;
	}

	protected  List<String> objectJsonList(Object... objects){
		List<String> datas = new ArrayList<>();
		for (Object object : objects) {
			String value = NxCacheJsonUtil.toJson(object);
			datas.add(value);
		}
		return datas;
	}

	protected  String[] objectJsonArray(Object... objects){
		List<String> list = objectJsonList(objects);
		return list.toArray(new String[]{});
	}

	protected  <T> List<T> jsonListToList(List<String> jsonList,Class<T> clz){
		List<T> datas = new ArrayList<>();
		for (String object : jsonList) {
			T value = NxCacheJsonUtil.toObject(object,clz);
			datas.add(value);
		}
		return datas;
	}

	protected  <T> List<T> jsonSetToList(Set<String> jsonSet,Class<T> clz){
		List<T> datas = new ArrayList<>();
		for (String object : jsonSet) {
			T value = NxCacheJsonUtil.toObject(object,clz);
			datas.add(value);
		}
		return datas;
	}


	protected  <T> Set<T> jsonSetToSet(Set<String> jsonSet,Class<T> clz){
		Set<T> datas = new HashSet<>();
		for (String object : jsonSet) {
			T value = NxCacheJsonUtil.toObject(object,clz);
			datas.add(value);
		}
		return datas;
	}



	/**
	 * 默认过期时间
	 * @return
	 */
	public static long getDefaultExpireSeconds(){
		return CacheExpiresUtils.todayEndSeconds();
	}
}
