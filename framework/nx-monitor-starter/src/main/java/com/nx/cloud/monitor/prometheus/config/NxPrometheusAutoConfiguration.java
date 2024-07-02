package com.nx.cloud.monitor.prometheus.config;

import com.nx.cloud.monitor.prometheus.alert.AlertApiController;
import com.nx.cloud.monitor.prometheus.httpsd.PrometheusHttpSdApiController;
import com.nx.cloud.monitor.prometheus.health.HealthHttpTraceConfig;
import com.nx.cloud.monitor.prometheus.health.HealthIndicatorConfig;
import com.nx.cloud.monitor.prometheus.metric.MetricsEndpoint;
import com.nx.cloud.monitor.prometheus.metric.MetricsEndpointConfig;
import com.nx.cloud.monitor.prometheus.metric.druid.DruidMetricsConfiguration;
import com.nx.cloud.monitor.prometheus.metric.undertow.UndertowMetricsConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "management.endpoint.prometheus", value = "enabled",havingValue = "true",matchIfMissing = true)
@Import({HealthHttpTraceConfig.class,
        HealthIndicatorConfig.class,
        MetricsEndpoint.class,
        MetricsEndpointConfig.class,
        UndertowMetricsConfiguration.class,
        DruidMetricsConfiguration.class,
       })
public class NxPrometheusAutoConfiguration {

    /**
     * 启动服务，浏览器访问 http://127.0.0.1:8088/actuator/prometheus 就可以看到应用的 一系列不同类型 metrics 信息，
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configure(@Autowired Environment environment){
        String applicationName = environment.getProperty("spring.application.name");
        if (StringUtils.isEmpty(applicationName)){
            throw new RuntimeException("${spring.application.name}的值不能为空！");
        }
        return registry ->  registry.config().commonTags("application", applicationName);
    }

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


    /**
     *Spring Cloud将提供阻塞和反应式服务发现客户端。您可以通过设置spring.cloud.discovery.blocking.enabled=false 或spring.cloud.discovery.reactive.enabled=false来轻松地禁用阻止和/或响应客户端。要完全禁用服务发现，您只需要设置spring.cloud.discovery.enabled=false
     */
    @AutoConfiguration
    @ConditionalOnMissingBean(PrometheusApiConfiguration.class)
    @ConditionalOnBean(DiscoveryClient.class)
    @ConditionalOnDiscoveryEnabled
    @ConditionalOnProperty(prefix = "spring.cloud.discovery.blocking.enabled",havingValue = "true")
    public static class PrometheusApiConfiguration {

        @Bean
        public PrometheusHttpSdApiController prometheusApi(Environment environment,
                                                           DiscoveryClient discoveryClient,
                                                           ApplicationEventPublisher eventPublisher) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
            return new PrometheusHttpSdApiController(activeProfile, discoveryClient, eventPublisher);
        }

        @Bean
        @ConditionalOnClass(DiscoveryClient.class)
        public AlertApiController prometheusAlertApi(Environment environment,
                                                     DiscoveryClient discoveryClient,
                                                     ApplicationEventPublisher eventPublisher) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
            return new AlertApiController(activeProfile, discoveryClient, eventPublisher);
        }


    }




}
