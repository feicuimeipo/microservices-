package com.nx.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * @Author NIANXIAOLING
 * @Description TODO
 * @Date 2021/1/25 13:49
 * @Version 1.0
 */
@Configuration
public class SentinelGatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    @Autowired
    private  ServerCodecConfigurer serverCodecConfigurer;

    @Autowired
    public SentinelGatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    }

    @Bean
    @ConditionalOnMissingBean(ServerCodecConfigurer.class)
    DefaultServerCodecConfigurer serverCodecConfigurer(){
        return new DefaultServerCodecConfigurer();
    }

    /**
     *  系统默认:SentinelGatewayBlockExceptionHandler
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }


//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE+100)
    SentinelRuleManager sentinelRuleManager(){
        return new SentinelRuleManager();
    }


}