package com.nx.prometheus.config.custom;

import com.nx.prometheus.config.custom.aop.Metrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @ClassName CustomMetricsAutoConfiguration
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 19:04
 * @Version 1.0
 **/
public class CustomMetricsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(Environment environment) {
        return registry -> {
            registry.config()
                    .commonTags("application", environment.getProperty("spring.application.name"));
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public Metrics metrics() {
        return new Metrics();
    }
}
