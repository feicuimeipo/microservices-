package com.nx.cloud.monitor.prometheus.metric;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


@Configuration
@ConditionalOnClass(Timed.class)
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class MetricsEndpointConfig {

    /**
     *  指标端点
     */
    @Bean
    @ConditionalOnBean({MeterRegistry.class})
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public MetricsEndpoint metricsEndpoint(MeterRegistry meterRegistry) {
        log.info("云平台初始化自定义度量指标监控!");
        return new MetricsEndpoint(meterRegistry);
    }
}
