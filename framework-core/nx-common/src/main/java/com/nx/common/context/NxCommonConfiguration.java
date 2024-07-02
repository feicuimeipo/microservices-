package com.nx.common.context;

import com.nx.common.context.filter.NxCurrentRuntimeFilter;
import com.nx.common.context.filter.NxTraceIdFilter;
import org.slf4j.impl.StaticMDCBinder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NxCommonConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        StaticMDCBinder.SINGLETON.getMDCA();
    }

    @Bean
    NxTraceIdFilter nxTraceIdFilter(){
        return new NxTraceIdFilter();
    }

    @Bean
    NxCurrentRuntimeFilter commonFilter(){
        return new NxCurrentRuntimeFilter();
    }


    @Bean
    SpringUtils springUtils(){
        return new SpringUtils();
    }


}
