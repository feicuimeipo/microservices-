package com.nx.redis.provider.cluster;

import com.nx.redis.RedisConfig;
import com.nx.redis.JedisProvider;
import com.nx.common.context.spi.TenantContextHolder;
import com.nx.redis.enums.RedisMode;
import lombok.Data;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.context.ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS;


/**
 * 集群 redis服务提供者
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年04月23日
 */
@Data
public class JedisClusterProvider implements JedisProvider<JedisCluster> {
	private Integer maxRedirections = 3; //重试3次
	private JedisCluster jedisCluster;
	private final RedisConfig redisConfig;
	private String groupName;
	private boolean tenantEnabled;
	public static final RedisMode MODE =  RedisMode.cluster;


	/**
	 *
	 */
	public JedisClusterProvider(RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
		this.groupName = redisConfig.getGroupName();
		String[] servers = redisConfig.getCluster().getNodes().split(",;|");
		int timeout = redisConfig.getTimeout();


		JedisPoolConfig jedisPoolConfig = redisConfig.getPool();

		Set<HostAndPort> nodes = this.parseHostAndPort(servers);
		jedisCluster = new JedisCluster(nodes, timeout, maxRedirections,jedisPoolConfig);
	}




	//public JedisClusterProvider(final RedisConfig redisConfig, final JedisPoolConfig jedisPoolConfig, String[] servers, final int timeout, final String password,String clientName) {
	public JedisClusterProvider(final RedisConfig redisConfig, final JedisPoolConfig jedisPoolConfig) {
		//connectionTimeout:url的连接等待时间。
		//soTimeout:  获取数据超时
		this.redisConfig = redisConfig;

		int	timeout = redisConfig.getTimeout();
		String password = redisConfig.getPassword();
		String clientName = redisConfig.getClientName();
		String[] servers =redisConfig.getServers().split(CONFIG_LOCATION_DELIMITERS);


		Set<HostAndPort> nodes = this.parseHostAndPort(servers);
		jedisCluster =new JedisCluster(nodes,  timeout, timeout,  maxRedirections, password,  clientName, jedisPoolConfig);

		//binaryJedisCluster = new BinaryJedisCluster(nodes, timeout,timeout, maxRedirections ,password,clientName,jedisPoolConfig);
	}


	private Set<HostAndPort> parseHostAndPort(String[] servers){
		try {
			Set<HostAndPort> haps = new HashSet<HostAndPort>();

			for (String part : servers) {
				String[] ipAndPort = part.split(":");
				HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				haps.add(hap);
			}

			return haps;
		}catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	public JedisCluster get() {
		return jedisCluster;
	}



	@Override
	public void release() {}

	@Override
	public void destroy() throws Exception{
		jedisCluster.close();
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