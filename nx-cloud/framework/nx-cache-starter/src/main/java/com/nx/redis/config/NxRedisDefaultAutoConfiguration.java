package com.nx.redis.config;


import com.nx.cache.adapter.impl.RedisCacheAdapter;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.SpringUtils;
import com.nx.redis.RedisTemplateFactory;
import com.nx.redis.RedisConfig;
import com.nx.redis.RedisConfigFactory;
import com.nx.redis.serializer.SerializerTypeEnum;
import com.nx.redis.serializer.TenantPartitionKeySerializer;
import com.nx.redis.serializer.KryoRedisSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * spring.redis-starter
 * com.nx.redis.starter.RedisAutoConfiguration
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureAfter(NxJedisProviderFactoryBean.class)
public class NxRedisDefaultAutoConfiguration implements ApplicationContextAware {

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     */
	@Bean
    @Autowired(required = false)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        if (factory==null){
            factory = RedisTemplateFactory.getJedisConnectionFactory(RedisConfigFactory.DEFAULT_GROUP_NAME);
        }
        RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(RedisConfigFactory.DEFAULT_GROUP_NAME);


        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置 RedisConnection 工厂。😈 它就是实现多种 Java Redis 客户端接入的秘密工厂。感兴趣的胖友，可以自己去撸下。
        template.setConnectionFactory(factory);

        //key
        boolean keyUseStringSerializer = redisConfig.isKeyUseStringSerializer();
        if(keyUseStringSerializer) {
        	template.setKeySerializer(new TenantPartitionKeySerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
        }
        //value
        if(SerializerTypeEnum.Kryo.getCode().equalsIgnoreCase(redisConfig.getValueSerializerType())) {
        	KryoRedisSerializer kryoRedisSerializer = new KryoRedisSerializer();
            template.setValueSerializer(kryoRedisSerializer);
            template.setHashValueSerializer(kryoRedisSerializer);

        }else if(SerializerTypeEnum.Json.getCode().equalsIgnoreCase(redisConfig.getValueSerializerType())) {
        	Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        	template.setValueSerializer(jsonRedisSerializer);
            template.setHashValueSerializer(jsonRedisSerializer);
        }

        RedisTemplateFactory.setDefaultRedisTemplate(template);
        RedisConfigFactory.DEFAULT_GROUP_NAME = redisConfig.getGroupName();

        BannerUtils.push(this.getClass(),"nx-cache-start-RedisTemplate<String, Object> enabled",null);
        template.afterPropertiesSet();
        return template;
    }

	@Bean
    @Autowired(required = false)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        if (factory==null){
            factory = RedisTemplateFactory.getJedisConnectionFactory(RedisConfigFactory.DEFAULT_GROUP_NAME);
        }

        RedisConfig redisConfig = RedisConfigFactory.getRedisConfig(RedisConfigFactory.DEFAULT_GROUP_NAME);


        StringRedisTemplate template = new StringRedisTemplate();
        template.setKeySerializer(new TenantPartitionKeySerializer());
        template.setConnectionFactory(factory);

        //value
        if(SerializerTypeEnum.Kryo.getCode().equalsIgnoreCase(redisConfig.getValueSerializerType())) {
            KryoRedisSerializer kryoRedisSerializer = new KryoRedisSerializer();
            template.setValueSerializer(kryoRedisSerializer);
            template.setHashValueSerializer(kryoRedisSerializer);

        }else if(SerializerTypeEnum.Json.getCode().equalsIgnoreCase(redisConfig.getValueSerializerType())) {
            Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
            template.setValueSerializer(jsonRedisSerializer);
            template.setHashValueSerializer(jsonRedisSerializer);
        }

        template.afterPropertiesSet();

        RedisTemplateFactory.setDefaultRedisTemplate(template);
        RedisConfigFactory.DEFAULT_GROUP_NAME = redisConfig.getGroupName();
        return template;
    }

    @Bean
    public RedisCacheAdapter redisCacheAdapter(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        return new RedisCacheAdapter(redisTemplate, stringRedisTemplate);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.setContext(applicationContext);
    }
}
