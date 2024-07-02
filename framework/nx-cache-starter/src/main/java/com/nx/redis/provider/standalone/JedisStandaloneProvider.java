package com.nx.redis.provider.standalone;

import com.nx.redis.JedisProvider;
import com.nx.redis.RedisConfig;
import com.nx.common.context.spi.TenantContextHolder;
import com.nx.redis.enums.RedisMode;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 标准（单服务器）redis服务提供者
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年04月23日
 */
@Data
public class JedisStandaloneProvider implements JedisProvider<Jedis> {
	
	protected static final Logger logger = LoggerFactory.getLogger(JedisStandaloneProvider.class);
	private ThreadLocal<Jedis> context = new ThreadLocal<>();
	private final JedisPool jedisPool;
	private RedisConfig redisConfig;
	private String groupName;
	private static RedisMode MODE = RedisMode.standalone;
	private boolean tenantEnabled;


	public JedisStandaloneProvider(RedisConfig redisConfig) {
		super();

		this.redisConfig = redisConfig;
		this.groupName = redisConfig.getGroupName();

		JedisPoolConfig jedisPoolConfig = redisConfig.getPool();

		int	timeout = redisConfig.getTimeout();
		int database = redisConfig.getDatabase();
		String password = redisConfig.getPassword();
		String clientName = redisConfig.getClientName();

		String[] servers = StringUtils.tokenizeToStringArray(redisConfig.getServers(), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		String[] addrs = servers[0].split(":");
		jedisPool = new JedisPool(jedisPoolConfig, addrs[0], Integer.parseInt(addrs[1].trim()), timeout, password, database, clientName);
	}

	public Jedis get() throws JedisException {
        Jedis jedis = context.get();
        if(jedis != null)return jedis;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisException e) {
            if(jedis!=null){
            	jedis.close();
            }
            throw e;
        }
        context.set(jedis);
        if(logger.isTraceEnabled()){
        	logger.trace(">>get a redis conn[{}],Host:{}",jedis.toString(),jedis.getClient().getHost());
        }
        return jedis;
    }

	public void release() {
		Jedis jedis = context.get();
        if (jedis != null) {
        	context.remove();
        	jedis.close();
        	if(logger.isTraceEnabled()){
            	logger.trace("<<release a redis conn[{}]",jedis.toString());
            }
        }
    }

	
	@Override
	public void destroy() throws Exception{
		jedisPool.destroy();
	}


	@Override
	public RedisMode mode() {
		return MODE;
	}

	@Override
	public boolean tenantEnabled() {
		return this.tenantEnabled;
	}

	@Override
	public String groupName() {
		return this.groupName;
	}

	@Override
	public RedisConfig redisConfig() {
		return this.redisConfig;
	}

}
