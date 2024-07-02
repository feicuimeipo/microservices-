package com.nx.prometheus.config;

import com.nx.prometheus.alert.ReactivePrometheusAlertApi;
import com.nx.prometheus.config.httpsd.ReactivePrometheusHttpSdApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@AutoConfiguration
@ConditionalOnMissingBean(ReactivePrometheusApiConfiguration.class)
@ConditionalOnBean(ReactiveDiscoveryClient.class)
@ConditionalOnDiscoveryEnabled
@ConditionalOnReactiveDiscoveryEnabled
public class ReactivePrometheusApiConfiguration {

    @Bean
    @Resource
    public ReactivePrometheusHttpSdApi reactivePrometheusHttpSdApi(Environment environment,
                                                                   ReactiveDiscoveryClient discoveryClient,
                                                                   ApplicationEventPublisher eventPublisher) {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
        return new ReactivePrometheusHttpSdApi(activeProfile, discoveryClient, eventPublisher);
    }

    @Bean
    public ReactivePrometheusAlertApi reactivePrometheusAlertApi(Environment environment,
                                                                 ReactiveDiscoveryClient discoveryClient,
                                                                 ApplicationEventPublisher eventPublisher) {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : null;
        return new ReactivePrometheusAlertApi(activeProfile, discoveryClient, eventPublisher);
    }

}
