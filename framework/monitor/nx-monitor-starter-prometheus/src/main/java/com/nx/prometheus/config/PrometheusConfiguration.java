package com.nx.prometheus.config;

import com.nx.prometheus.alert.PrometheusAlertApi;
import com.nx.prometheus.alert.ReactivePrometheusAlertApi;
import com.nx.prometheus.config.custom.aop.CustomerMetricsAspect;
import com.nx.prometheus.config.httpsd.PrometheusHttpSdApi;
import com.nx.prometheus.config.httpsd.ReactivePrometheusHttpSdApi;
import com.nx.prometheus.health.HealthHttpTraceConfig;
import com.nx.prometheus.health.HealthIndicatorConfig;
import com.nx.prometheus.metric.MetricsEndpoint;
import com.nx.prometheus.metric.MetricsEndpointConfig;
import com.nx.prometheus.metric.druid.DruidMetricsConfiguration;
import com.nx.prometheus.metric.undertow.UndertowMetricsConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@AutoConfiguration
@ConditionalOnProperty(prefix = "management.metrics.export.prometheus.enabled", name = "enabled",havingValue = "true",matchIfMissing = true)
@Import({HealthHttpTraceConfig.class,
        HealthIndicatorConfig.class,
        MetricsEndpoint.class,
        MetricsEndpointConfig.class,
        UndertowMetricsConfiguration.class,
        DruidMetricsConfiguration.class,
       })
public class PrometheusConfiguration {
    

    /**
     * 最后，启动服务，浏览器访问 http://127.0.0.1:8088/actuator/prometheus 就可以看到应用的 一系列不同类型 metrics 信息，
     * @param environment
     * @return
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
     * 通过注解生成的指指标
     * @return
     */
    @Bean
    @Resource
    CustomerMetricsAspect monitorAop(MeterRegistry registry) {return new CustomerMetricsAspect(registry);}


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
        @Resource
        public PrometheusHttpSdApi prometheusApi(Environment environment,
                                                 DiscoveryClient discoveryClient,
                                                 ApplicationEventPublisher eventPublisher) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
            return new PrometheusHttpSdApi(activeProfile, discoveryClient, eventPublisher);
        }

        @Bean
        @ConditionalOnClass(DiscoveryClient.class)
        public PrometheusAlertApi prometheusAlertApi(Environment environment,
                                                     DiscoveryClient discoveryClient,
                                                     ApplicationEventPublisher eventPublisher) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
            return new PrometheusAlertApi(activeProfile, discoveryClient, eventPublisher);
        }


    }




}
