package com.nx.redis.provider.sentinel;

import com.nx.redis.RedisConfig;
import com.nx.redis.JedisProvider;
import com.nx.common.context.spi.TenantContextHolder;
import com.nx.redis.enums.RedisMode;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static org.springframework.context.ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS;

/**
 * redis哨兵主从模式服务提供者
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年04月26日
 */
@Data
public class JedisSentinelProvider implements JedisProvider<Jedis> {
	private static final String SLAVE_CHEKER_KEY = "_slave_cheker";
	private static final String SLAVE_CHEKER_VAL = "1";
	protected static final Logger logger = LoggerFactory.getLogger(JedisSentinelProvider.class);
	public static final RedisMode MODE =  RedisMode.sentinel;
	private ThreadLocal<Jedis> context = new ThreadLocal<>();
	private JedisSentinelPool jedisPool;
	private String groupName;
	private RedisConfig redisConfig;
	private boolean tenantEnabled;


	private ScheduledExecutorService failoverChecker;

	public JedisSentinelProvider(RedisConfig redisConfig) {
		super();

		this.groupName = redisConfig.getGroupName();
		this.redisConfig = redisConfig;

		JedisPoolConfig jedisPoolConfig = redisConfig.getPool();

		int	timeout = redisConfig.getTimeout();
		int database = redisConfig.getDatabase();
		String password = redisConfig.getPassword();
		String clientName = redisConfig.getClientName();
		String[] servers =redisConfig.getServers().split(CONFIG_LOCATION_DELIMITERS);
		String masterName = redisConfig.getSentinel().getMasterName();


		final Set<String> sentinels = new HashSet<String>(Arrays.asList(servers));
		jedisPool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig, timeout, password, database,clientName);
		failoverChecker = Executors.newScheduledThreadPool(1);
		failoverChecker.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Jedis jedis = null;
				try {
					jedis = jedisPool.getResource();
					jedis.set(SLAVE_CHEKER_KEY,SLAVE_CHEKER_VAL);
				} catch (Exception e) {
					if(e instanceof JedisDataException && e.getMessage().contains("READONLY")){
						logger.warn(" JedisDataException happend error:{} and will re-init jedisPool" ,e.getMessage());
						//重新初始化jedisPool
						synchronized (jedisPool) {
							jedisPool.destroy();
							jedisPool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig, timeout, password, database,clientName);
							logger.info(" jedisPool re-init ok,currentHostMaster is:{}:{}" ,jedisPool.getCurrentHostMaster().getHost(),jedisPool.getCurrentHostMaster().getPort());
						}
					}
				}finally {
					try {jedis.close();} catch (Exception e2) {}
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
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


	@Override
	public void destroy() throws Exception{
		failoverChecker.shutdown();
		jedisPool.destroy();
	}


}
