package com.nx.cloud.protection.sentinel;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SentinelAspectConfiguration
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/13 15:04
 * @Version 1.0
 **/
@Configuration
@ConditionalOnProperty(
        prefix = "spring.cloud.alibaba.sentinel",
        name = {"enabled"},
        havingValue = "true"
)
public class SentinelAspectConfiguration {
    @Bean
    @ConditionalOnMissingBean(SentinelResourceAspect.class)
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
