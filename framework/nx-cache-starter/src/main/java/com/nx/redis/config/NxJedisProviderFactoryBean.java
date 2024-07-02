
package com.nx.redis.config;

import com.nx.common.context.SpringUtils;
import com.nx.redis.*;
import com.nx.redis.provider.cluster.JedisClusterProvider;
import com.nx.redis.enums.RedisMode;
import com.nx.redis.provider.sentinel.JedisSentinelProvider;
import com.nx.redis.provider.standalone.JedisStandaloneProvider;
import com.nx.common.context.spi.TenantContextHolder;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.nx.redis.JedisProviderFactory.addGroupProvider;
import static com.nx.redis.RedisConfigFactory.*;
import static com.nx.redis.enums.RedisConstant.REDIS_PROVIDER_SUFFIX;
import static com.nx.redis.enums.RedisConstant.TenantModeEnabledName;

/**
 * redis服务提供者注册工厂
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2015年04月26日
 */
@Configuration
@ConditionalOnMissingBean(NxJedisProviderFactoryBean.class)
public class NxJedisProviderFactoryBean implements ApplicationContextAware,InitializingBean{
	protected static final Logger logger = LoggerFactory.getLogger(NxJedisProviderFactoryBean.class);

	private static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
		SpringUtils.setContext(context);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (context==null) {
			return;
		}
		register(DEFAULT_GROUP_NAME);
	}

	public static void register(String groupName) {
		RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(groupName);
		String beanName  = groupName + REDIS_PROVIDER_SUFFIX;
		RedisMode redisMode =  RedisMode.valueOfCode(redisConfig.getMode());
		Class  beanClass = null;
		BeanDefinitionBuilder beanDefinitionBuilder = null;


		switch (redisMode){
			case standalone:
				beanClass = JedisStandaloneProvider.class;
				beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JedisStandaloneProvider.class);
				Validate.notBlank(redisConfig.getHost(), redisMode.getCode()+"Cluster模式[Host]参数 required");
				Validate.notNull(redisConfig.getPort(), redisMode.getCode()+"Cluster模式[Port]参数 required");
				checkServers(redisConfig.getServers());
				break;
			case cluster:
				beanClass = JedisClusterProvider.class;
				beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JedisClusterProvider.class);
				checkServers(redisConfig.getCluster().getNodes());
				Validate.notBlank(redisConfig.getClientName(), "Cluster模式[clientName]参数 required");
				break;
			case sentinel:
				String masterName = redisConfig.getSentinel().getMasterName();
				beanClass = JedisSentinelProvider.class;
				beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JedisSentinelProvider.class);
				checkServers(redisConfig.getSentinel().getNodes());
				Validate.notBlank(masterName, "Sentinel模式[masterName]参数 required");
				break;
			default:
				throw new RuntimeException("参数mode："+ redisMode==null?"null":redisMode.getCode() +"不支持");
		}


		DefaultListableBeanFactory acf = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
		beanDefinitionBuilder.addConstructorArgValue(redisConfig);
		beanDefinitionBuilder.addPropertyValue(TenantModeEnabledName, TenantContextHolder.enabled());
		acf.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
		logger.info(" register JedisProvider OK,Class:{},beanName:{}",beanClass.getSimpleName(),beanName);
	}



}
