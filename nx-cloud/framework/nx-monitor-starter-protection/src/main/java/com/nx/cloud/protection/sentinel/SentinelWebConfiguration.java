package com.nx.cloud.protection.sentinel;


import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration;
import com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration;
import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled",havingValue = "true")
@ConditionalOnMissingBean({
        SentinelAutoConfiguration.class,
        SentinelFeignAutoConfiguration.class,
        SentinelWebAutoConfiguration.class
})
@EnableConfigurationProperties(SentinelProperties.class)
@Import({SentinelAutoConfiguration.class,
        SentinelWebAutoConfiguration.class,
        SentinelFeignAutoConfiguration.class})
public class SentinelWebConfiguration {
}
