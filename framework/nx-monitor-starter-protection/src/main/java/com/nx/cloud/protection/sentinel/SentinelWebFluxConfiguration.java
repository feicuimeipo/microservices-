package com.nx.cloud.protection.sentinel;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.SentinelWebFluxAutoConfiguration;
import com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration;
import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.reactor.SentinelReactorTransformer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @ClassName SentinelWebFluxAutoConfiguration
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 22:25
 * @Version 1.0
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(SentinelReactorTransformer.class)
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled",havingValue = "true")
@EnableConfigurationProperties(SentinelProperties.class)
@ConditionalOnMissingBean({
        SentinelAutoConfiguration.class,
        SentinelFeignAutoConfiguration.class,
        SentinelWebFluxAutoConfiguration.class
})
@Import({SentinelAutoConfiguration.class,
        SentinelWebFluxAutoConfiguration.class,
        SentinelFeignAutoConfiguration.class})
public class SentinelWebFluxConfiguration {
}
