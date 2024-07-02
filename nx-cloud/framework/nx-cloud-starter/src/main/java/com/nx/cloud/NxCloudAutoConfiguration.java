package com.nx.cloud;

import com.nx.cloud.loadbalancer.LoadBalancerConfig;
import com.nx.cloud.seata.SeataAutoConfiguration;
import com.nx.cloud.seata.SeataInitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableDiscoveryClient
@Import(value = {SeataInitProperties.class,
        SeataAutoConfiguration.class,
        LoadBalancerConfig.class})
@ConditionalOnMissingBean(NxCloudAutoConfiguration.class)
public class NxCloudAutoConfiguration {

    @Bean
    @ConditionalOnClass(RestTemplate.class)
    @ConditionalOnMissingBean(RestTemplate.class)
    RestTemplate restTemplate(){
        return new RestTemplate();
    }


}
