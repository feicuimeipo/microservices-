package com.nx.redis;

import com.nx.redis.enums.RedisMode;
import org.springframework.beans.factory.DisposableBean;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2015年04月23日
 */
public interface JedisProvider<S> extends DisposableBean{

	public S get();

	public void release();
	
	public RedisMode mode();

	public boolean tenantEnabled();
	
	public String groupName();

	public RedisConfig redisConfig();

}
