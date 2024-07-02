package org.nianxi.api.feign;

import org.nianxi.api.feign.fallback.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceApiAutoConfiguration {
    @Bean
    BpmServiceApiFallbackFactory bpmModelFallbackFactory(){
        return new BpmServiceApiFallbackFactory();
    }

    @Bean
    FormServiceApiFallbackFactory formFallbackFactory(){
        return new FormServiceApiFallbackFactory();
    }

    @Bean
    AdminServiceApiFallbackFactory ucFallbackFactory(){
        return new AdminServiceApiFallbackFactory();
    }

}
