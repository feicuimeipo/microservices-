package com.nx.cloud.protection.idempotent.config;

import com.nx.cloud.protection.idempotent.core.aop.IdempotentAspect;
import com.nx.cloud.protection.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.nx.cloud.protection.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.nx.cloud.protection.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.nx.cloud.protection.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(NxIdeProtectionAutoConfiguration.class)
public class NxIdeProtectionAutoConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
