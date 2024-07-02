package com.nx.skywalking;

import com.nx.skywalking.juc.EnhanceMDCAdapterInitializer;
import com.nx.skywalking.juc.EnhanceThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName SkywalkingAutoConfiguration
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/7/7 14:24
 * @Version 1.0
 **/
@Configuration
@Slf4j
public class SkywalkingAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean({EnhanceMDCAdapterInitializer.class})
    EnhanceMDCAdapterInitializer adapterInitializer() {
        return new EnhanceMDCAdapterInitializer();
    }

    @Bean
    @ConditionalOnMissingBean({RequestTraceIdFilter.class})
    RequestTraceIdFilter requestTraceIdFilter() {
        return new RequestTraceIdFilter();
    }

    @Bean
    @Primary
    TaskExecutor threadPoolExecutor() {
        EnhanceThreadPoolTaskExecutor enhanceThreadPoolTaskExecutor = new EnhanceThreadPoolTaskExecutor();
        enhanceThreadPoolTaskExecutor.setCorePoolSize(5);
        enhanceThreadPoolTaskExecutor.setMaxPoolSize(10);
        enhanceThreadPoolTaskExecutor.setKeepAliveSeconds(60);
        enhanceThreadPoolTaskExecutor.setQueueCapacity(1000);
        enhanceThreadPoolTaskExecutor.setThreadFactory((ThreadFactory)new CustomizableThreadFactory("enchance-threadpool-dispose-%d"));
        enhanceThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        enhanceThreadPoolTaskExecutor.initialize();
        enhanceThreadPoolTaskExecutor.execute(() -> log.info(""));
        return (TaskExecutor)enhanceThreadPoolTaskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean({EnhanceMDCAdapterInitializer.class})
    EnhanceMDCAdapterInitializer enhanceMDCAdapterInitializer() {
        return new EnhanceMDCAdapterInitializer();
    }
}
