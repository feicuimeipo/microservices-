package com.nx.cloud.monitor.prometheus.health;

import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceProperties;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "management.trace.http", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HttpTraceProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE-100)
public class HealthHttpTraceConfig {
    /**
     *  定义存储 HttpTrace list 结构
     * */
    private List<HttpTrace> list = new CopyOnWriteArrayList<HttpTrace>();
    /**
     * 1. 手动打开 显示HTTP跟踪信息（默认情况下，最后100个HTTP请求 - 响应交换）
     * 2. http://localhost:8089/actuator/httptraces
     * */
    @Bean
    @ConditionalOnMissingBean(HttpTraceRepository.class)
    public HttpTraceRepository traceRepository() {
        return new HttpTraceRepository() {
            @Override
            public List<HttpTrace> findAll() {
                return list;
            }

            @Override
            public void add(HttpTrace trace) {
                if (list.size() > 99){
                    list.remove(0);
                }
                list.add(trace);
            }
        };
    }

}
