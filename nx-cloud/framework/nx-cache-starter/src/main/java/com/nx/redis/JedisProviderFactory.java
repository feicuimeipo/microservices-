package com.nx.redis;

import com.nx.redis.enums.RedisMode;
import com.nx.redis.provider.sentinel.JedisSentinelProvider;
import com.nx.redis.provider.standalone.JedisStandaloneProvider;
import com.nx.redis.provider.cluster.JedisClusterProvider;
import com.nx.common.context.SpringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nx.redis.RedisConfigFactory.DEFAULT_GROUP_NAME;
import static com.nx.redis.enums.RedisConstant.REDIS_PROVIDER_SUFFIX;

/**
 * redis实例工厂
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2015年04月26日
 */
public class JedisProviderFactory {

	@SuppressWarnings("rawtypes")
	private static Map<String, JedisProvider> jedisProviderMap = new ConcurrentHashMap<>();
	private static JedisProvider<?> defaultJedisProvider;

	protected static final Logger logger = LoggerFactory.getLogger(JedisProviderFactory.class);


	public static JedisProvider<?> getJedisProvider(String groupName) {
		JedisProvider  jedisProviders = jedisProviderMap.get(groupName);
		if (jedisProviders!=null){
			if(defaultJedisProvider == null && groupName.equals(DEFAULT_GROUP_NAME)){
				defaultJedisProvider = jedisProviders;
			}
			return jedisProviders;
		}

		initFactoryFromSpring();

		if (defaultJedisProvider==null) {
			defaultJedisProvider = jedisProviderMap.get(DEFAULT_GROUP_NAME);
			if(defaultJedisProvider == null && jedisProviderMap.size() == 1){
				defaultJedisProvider = new ArrayList<>(jedisProviderMap.values()).get(0);
			}
		}

		jedisProviders = jedisProviderMap.get(groupName);
		return jedisProviders;
	}

	@SuppressWarnings("rawtypes")
	private synchronized static void initFactoryFromSpring() {

		Map<String, JedisProvider> interfaces = SpringUtils.getBeansOfType(JedisProvider.class);
		if(interfaces != null && interfaces.size() >0){
			Iterator<JedisProvider> iterator = interfaces.values().iterator();
			while(iterator.hasNext()){
				JedisProvider jp = iterator.next();
				jedisProviderMap.putIfAbsent(jp.groupName(), jp);
			}
		}

		Assert.notNull(defaultJedisProvider,"无默认缓存配置，请指定一组缓存配置group为"+DEFAULT_GROUP_NAME);

	}

	public static JedisCommands getJedisCommands(String groupName) {
		return (JedisCommands) getJedisProvider(groupName).get();
	}


	public static MultiKeyCommands getMultiKeyCommands(String groupName) {
		return (MultiKeyCommands) getJedisProvider(groupName).get();
	}
	
	public static MultiKeyBinaryCommands getMultiKeyBinaryCommands(String groupName) {
		return (MultiKeyBinaryCommands) getJedisProvider(groupName).get();
	}
	
	public static MultiKeyJedisClusterCommands getMultiKeyJedisClusterCommands(String groupName) {
		return (MultiKeyJedisClusterCommands) getJedisProvider(groupName).get();
	}

	public static MultiKeyBinaryJedisClusterCommands getMultiKeyBinaryJedisClusterCommands(String groupName) {
		return (MultiKeyBinaryJedisClusterCommands) getJedisProvider(groupName).get();
	}
	

	public static boolean isCluster(String groupName){
		RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(groupName);
		if (redisConfig!=null){
			return redisConfig.getMode().equalsIgnoreCase(RedisMode.cluster.getCode());
		}
		return false;
	}

	@Deprecated
	public static synchronized void addGroupProvider(String groupName) {
		RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(groupName);

		RedisMode redisMode = RedisMode.valueOfCode(redisConfig.getMode());
		if (redisMode == RedisMode.standalone) {
			JedisProvider<Jedis> provider = new JedisStandaloneProvider(redisConfig);
			addProvider(provider);
		} else if (redisMode == RedisMode.sentinel) {
			Validate.notBlank(redisConfig.getSentinel().getMasterName(), "[masterName] not found");
			JedisSentinelProvider provider = new JedisSentinelProvider(redisConfig);
			addProvider(provider);
		} else if (redisMode == RedisMode.cluster) {
			Validate.notBlank(redisConfig.getCluster().getMasterName(), "[masterName] not found");
			JedisProvider<JedisCluster> provider = new JedisClusterProvider(redisConfig);
			addProvider(provider);
		}
	}

	private static synchronized void addProvider(JedisProvider provider){
		if (provider==null) return;
		jedisProviderMap.put(provider.groupName(), provider);
	}






}
