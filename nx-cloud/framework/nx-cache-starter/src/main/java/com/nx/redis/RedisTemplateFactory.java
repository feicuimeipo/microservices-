package com.nx.redis;


import com.nx.common.context.SpringUtils;
import com.nx.common.context.spi.TenantContextHolder;
import com.nx.redis.exception.RedisException;
import com.nx.redis.serializer.TenantPartitionKeySerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static com.nx.redis.RedisConfigFactory.DEFAULT_GROUP_NAME;
import static com.nx.redis.RedisConfigFactory.DEFAULT_SESSION_NAME;


/**
 * 一个工程里，可以有多组redis
 */
public class RedisTemplateFactory {

	private static  boolean inited = false;

	private volatile static Map<String, RedisTemplatePair> groupTemplateMapping = new ConcurrentHashMap<>();


	public synchronized static void setDefaultRedisTemplate(RedisTemplate<String, Object> redisTemplate){
		if (redisTemplate==null){
			return;
		}
		if (!groupTemplateMapping.containsKey(DEFAULT_GROUP_NAME)){
			groupTemplateMapping.put(DEFAULT_GROUP_NAME,new RedisTemplatePair());
		}
		groupTemplateMapping.get(DEFAULT_GROUP_NAME).setRedisTemplate(redisTemplate);
	}


	public synchronized static void setDefaultRedisTemplate(StringRedisTemplate stringRedisTemplate){
		if (stringRedisTemplate==null){
			return;
		}
		if (!groupTemplateMapping.containsKey(DEFAULT_GROUP_NAME)){
			groupTemplateMapping.put(DEFAULT_GROUP_NAME,new RedisTemplatePair());
		}
		groupTemplateMapping.get(DEFAULT_GROUP_NAME).setStringRedisTemplate(stringRedisTemplate);
	}

	public static RedisTemplatePair getRedisTemplatePairs(String groupName) {
		RedisTemplatePair redisTemplatePair = groupTemplateMapping.get(groupName);
		if (redisTemplatePair==null){
			throw new RedisException(groupName);
		}
		return redisTemplatePair;
	}


	private static synchronized void initRedisTemplateGroup(String groupName) {
		if(groupTemplateMapping.containsKey(groupName));
		RedisTemplate<String, Object> redisTemplate = null;
		StringRedisTemplate stringRedisTemplate = null;

		Map<String, RedisOperations> instanceMap = SpringUtils.getBeansOfType(RedisOperations.class);
		if (instanceMap!=null && instanceMap.values().size()>0) {
			Collection<RedisOperations> instances = instanceMap.values();
			for (RedisOperations redis : instances) {
				if (redis instanceof StringRedisTemplate) {
					stringRedisTemplate = (StringRedisTemplate) redis;
				} else {
					redisTemplate = (RedisTemplate<String, Object>) redis;
				}
			}
		}else {
			RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(groupName);
			JedisConnectionFactory redisConnectionFactory = getJedisConnectionFactory(redisConfig.getGroupName());

			redisTemplate = new RedisTemplate<>();
			redisTemplate.setConnectionFactory(redisConnectionFactory);
			redisTemplate.setKeySerializer(new StringRedisSerializer());
			redisTemplate.setHashKeySerializer(new StringRedisSerializer());
			redisTemplate.afterPropertiesSet();
			stringRedisTemplate = new StringRedisTemplate();
			stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
			stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
			stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
			stringRedisTemplate.afterPropertiesSet();
		}

		if(redisTemplate == null)return;

		if (TenantContextHolder.enabled()){
			redisTemplate.setKeySerializer(new TenantPartitionKeySerializer());
			stringRedisTemplate.setKeySerializer(new TenantPartitionKeySerializer());
		}else{
			redisTemplate.setKeySerializer(new StringRedisSerializer());
			stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		}

		groupTemplateMapping.put(groupName, new RedisTemplatePair(redisTemplate,stringRedisTemplate));
	}

	public static JedisConnectionFactory getJedisConnectionFactory(String groupName){
		if(!SpringUtils.Env.containsAnyProperty(groupName + ".redis.host",groupName + ".redis.sentinel.nodes",groupName + ".redis.cluster.nodes")) {
			if (RedisConfigFactory.getGroupNames().length>0){
				DEFAULT_GROUP_NAME = RedisConfigFactory.getGroupNames()[0];
			}
		}

		RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(groupName);

		JedisPoolConfig poolConfig = redisConfig.getPool();
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(poolConfig);
		String host = SpringUtils.Env.getProperty(redisConfig.getHost());
		int port = redisConfig.getPort();
		int database = redisConfig.getDatabase();
		String password = redisConfig.getPassword();

		redisConnectionFactory.setHostName(host);
		redisConnectionFactory.setPort(port);
		redisConnectionFactory.setPassword(password);
		redisConnectionFactory.setDatabase(database);
		redisConnectionFactory.afterPropertiesSet();
		return redisConnectionFactory;
	}


	public static RedisTemplate<String, Object> getRedisTemplate(String groupName) {
		RedisTemplatePair pair =  getRedisTemplatePairs(groupName);
		if (pair==null){
			throw new RedisException(groupName);
		}

		return pair.getRedisTemplate();
	}

	public static StringRedisTemplate getStringRedisTemplate(String groupName) {
		RedisTemplatePair pair =  getRedisTemplatePairs(groupName);
		if (pair==null){
			throw new RedisException(groupName);
		}

		return pair.getStringRedisTemplate();
	}


	public static StringRedisTemplate getDefaultStringRedisTemplate() {
		RedisTemplatePair pair =  getRedisTemplatePairs(DEFAULT_GROUP_NAME);
		if (pair==null){
			throw new RedisException(DEFAULT_GROUP_NAME);
		}
		return pair.getStringRedisTemplate();
	}

	public static RedisTemplate<String, Object> getDefaultRedisTemplate() {
		RedisTemplatePair pair =  getRedisTemplatePairs(DEFAULT_GROUP_NAME);
		if (pair==null){
			throw new RedisException(DEFAULT_GROUP_NAME);
		}
		return pair.getRedisTemplate();
	}

	public static StringRedisTemplate getSessionStringRedisTemplate() {
		RedisTemplatePair pair =  getRedisTemplatePairs(DEFAULT_SESSION_NAME);
		if (pair==null){
			pair =  getRedisTemplatePairs(DEFAULT_GROUP_NAME);
		}

		if (pair==null){
			throw new RedisException(DEFAULT_SESSION_NAME);
		}

		return pair.getStringRedisTemplate();
	}

	public static RedisTemplate<String, Object> getSessionRedisTemplate() {

		RedisTemplatePair pair =  getRedisTemplatePairs(DEFAULT_SESSION_NAME);
		if (pair==null){
			pair =  getRedisTemplatePairs(DEFAULT_GROUP_NAME);
		}

		if (pair==null){
			throw new RedisException(DEFAULT_SESSION_NAME);
		}

		return pair.getRedisTemplate();
	}


	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RedisTemplatePair{
		RedisTemplate<String, Object> redisTemplate;
		StringRedisTemplate stringRedisTemplate;
	}
}