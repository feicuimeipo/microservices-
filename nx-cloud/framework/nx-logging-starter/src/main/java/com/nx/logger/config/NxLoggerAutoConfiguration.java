package com.nx.logger.config;

import com.nx.logger.aop.ApiAccessLogFilter;
import com.nx.logger.aop.OperateLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NxLoggerAutoConfiguration {
    @Bean
    NxLoggerConfig nxLoggerConfig(){
        return new NxLoggerConfig();
    }

    @Bean
    ApiAccessLogFilter apiAccessLogFilter(){
        return new ApiAccessLogFilter();
    }

    @Bean
    OperateLogAspect operateLogAspect(){
        return new OperateLogAspect();
    }

}
