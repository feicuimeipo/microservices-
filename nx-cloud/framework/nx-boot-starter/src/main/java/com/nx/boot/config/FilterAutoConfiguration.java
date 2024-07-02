package com.nx.boot.config;


import com.nx.boot.filter.*;
import com.nx.boot.filter.request.CacheRequestBodyFilter;
import com.nx.common.context.filter.WebFilterOrderEnum;
import com.nx.logger.aop.ApiAccessLogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@Import(WebProperties.class)
public class FilterAutoConfiguration {
    /**
     * 应用名
     */
    @Value("${spring.application.name:'--'}")
    private String applicationName;

    @Autowired
    private WebProperties webProperties;


    @Bean
    @Primary
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter() {
        ApiAccessLogFilter filter = new ApiAccessLogFilter();
        filter.addExcludePathPrefix(webProperties.getAdminApi().getPrefix());
        FilterRegistrationBean<ApiAccessLogFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
        return bean;
    }


    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        CacheRequestBodyFilter filter = new CacheRequestBodyFilter();
        FilterRegistrationBean<CacheRequestBodyFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
        return bean;
    }


    @Bean
    @ConditionalOnProperty(prefix = "nx.cors",value = {"enabled"},havingValue = "true",matchIfMissing = true)
    @Order(WebFilterOrderEnum.CORS_FILTER)
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }


    @Bean
    @ConditionalOnProperty(prefix = "nx.xss",value = {"enabled"},havingValue = "true",matchIfMissing = true)
    public WafXssFilter wafFilter(){
        return new WafXssFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "nx.demo",value = {"enabled"},havingValue = "true")
    public DemoFilter demoFilter(){
        return new DemoFilter();
    }



	


}
