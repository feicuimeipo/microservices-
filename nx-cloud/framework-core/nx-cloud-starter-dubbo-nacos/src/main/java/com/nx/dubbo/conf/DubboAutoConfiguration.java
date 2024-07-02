package com.nx.dubbo.conf;

import com.nx.common.banner.BannerUtils;
import org.apache.dubbo.config.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DubboConfiguration
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/6 18:50
 * @Version 1.0
 **/
@Configuration
public class DubboAutoConfiguration {

    public DubboAutoConfiguration(){
        BannerUtils.push(this.getClass(),"nx-cloud-starter-dubbo-nacos enabled!");
    }



    @Bean
    @ConditionalOnMissingBean(ApplicationConfig.class)
    @ConfigurationProperties(prefix = "dubbo.application")
    ApplicationConfig applicationConfig(){
        return new ApplicationConfig();
    }


    @Bean
    @ConditionalOnMissingBean(ArgumentConfig.class)
    @ConfigurationProperties(prefix = "dubbo.argument")
    ArgumentConfig argumentConfig(){
        return new ArgumentConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ConfigCenterConfig.class)
    @ConfigurationProperties(prefix = "dubbo.config-center")
    ConfigCenterConfig configCenterConfig(){
        return new ConfigCenterConfig();
    }


    @Bean
    @ConditionalOnMissingBean(ConsumerConfig.class)
    @ConfigurationProperties(prefix = "dubbo.consumer")
    ConsumerConfig consumerConfig(){
        return new ConsumerConfig();
    }


    @Bean
    @ConditionalOnMissingBean(MetadataReportConfig.class)
    @ConfigurationProperties(prefix = "dubbo.meta-data")
    MetadataReportConfig metadataReportConfig(){
        return new MetadataReportConfig();
    }


    @Bean
    @ConditionalOnMissingBean(MethodConfig.class)
    @ConfigurationProperties(prefix = "dubbo.method")
    MethodConfig methodConfig(){
        return new MethodConfig();
    }


    @Bean
    @ConditionalOnMissingBean(MetricsConfig.class)
    @ConfigurationProperties(prefix = "dubbo.metric")
    MetricsConfig metricsConfig(){
        return new MetricsConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ModuleConfig.class)
    @ConfigurationProperties(prefix = "dubbo.module")
    ModuleConfig moduleConfig(){
        return new ModuleConfig();
    }

    @Bean
    @ConditionalOnMissingBean(MonitorConfig.class)
    @ConfigurationProperties(prefix = "dubbo.monitor")
    MonitorConfig monitorConfig(){
        return new MonitorConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ProtocolConfig.class)
    @ConfigurationProperties(prefix = "dubbo.protocol")
    ProtocolConfig protocolConfig(){
        return new ProtocolConfig();
    }


    @Bean
    @ConditionalOnMissingBean(ProviderConfig.class)
    @ConfigurationProperties(prefix = "dubbo.provider")
    ProviderConfig providerConfig(){
        return new ProviderConfig();
    }


    @Bean
    @ConditionalOnMissingBean(ReferenceConfig.class)
    @ConfigurationProperties(prefix = "dubbo.reference")
    ReferenceConfig referenceConfig(){
        return new ReferenceConfig();
    }


    @Bean
    @ConditionalOnMissingBean(RegistryConfig.class)
    @ConfigurationProperties(prefix = "dubbo.register")
    RegistryConfig registryConfig(){
        return new RegistryConfig();
    }



    @Bean
    @ConditionalOnMissingBean(SslConfig.class)
    @ConfigurationProperties(prefix = "dubbo.ssl")
    SslConfig sslConfig(){
        return new SslConfig();
    }



}
