package com.nx.prometheus.metric;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * <b>@Title: CloudNativeMetricsEndpointConfig;<T> </b>
 * <p>@Description: </p>
 * <p>6. @ConditionalOnClass</p>
 * <p>Module ID: framework.core 包<p>
 * <p>Comments:<p>
 * <p>JDK version used:  JDK1.8;
 * <p>Namespace:<p>
 * <p>@author：      admin
 * <p>@ProjectName cloud-native-framework</p>
 * <p>@date： 2020-04-23</p>
 * <p>Modified By：
 * <p>Modified Date:
 * <p>Why & What is modified
 * <p>@version:      coreplatform1.0;
 * <p>CopyRright (c)2017-cloud:
 *
 */
@Configuration
@ConditionalOnClass(Timed.class)
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class MetricsEndpointConfig {

    /**
     * <p>CloudPlatformMetricsEndpoint.</p>
     *  指标端点
     * @param meterRegistry a {@link MeterRegistry} object.
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
