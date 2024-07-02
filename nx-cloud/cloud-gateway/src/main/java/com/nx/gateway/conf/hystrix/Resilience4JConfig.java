package com.nx.gateway.conf.hystrix;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * @ClassName Resilience4J
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/12 19:28
 * @Version 1.0
 **/
@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.resilience4j",value = "enabled",havingValue = "true")
public class Resilience4JConfig {

    /**
     * 初始化断路器，读取Resilience4J的yaml配置
     * @param circuitBreakerRegistry
     * @return
     */
    @Bean
    @Primary
    public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(
            CircuitBreakerRegistry circuitBreakerRegistry) {
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();


        //设置断路器默认配置
        //不修改默认值可以忽略
        factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                //默认超时规则, 默认1s,不使用断路器超时规则可以设置大一点,4秒
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                //默认断路器规则
                //.circuitBreakerConfig(circuitBreakerConfig).build())
                //.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());

        //添加自定义拦截器
        factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
        return factory;
    }



}
