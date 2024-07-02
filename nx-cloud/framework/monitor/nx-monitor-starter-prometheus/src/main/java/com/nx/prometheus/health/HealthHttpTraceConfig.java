package com.nx.prometheus.health;

import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceProperties;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
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
/**
 * <b>@Title: CloudPlatformHealthHttpTraceConfig;<T> </b>
 * <p>@Description: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.tracing  </p>
 * <p>1. 需要实现 HttpTraceRepository 持久化保存</p>
 * <p>2. 需要</p>
 * <p>Module ID: framework.core 包<p>
 * <p>Comments:<p>
 * <p>JDK version used:  JDK1.8;
 * <p>Namespace:<p>
 * <p>@author：      admin
 * <p>@ProjectName cloud-native-framework</p>
 * <p>@date： 2020-04-23</p>
 * <p>Modified By：
 * <p>Modified Date:
 * <p>Why & What is modified
 * <p>@version:      coreplatform1.0;
 * <p>CopyRright (c)2017-cloud:
 *
 */
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
     * 2. http://localhost:8089/actuator/httptrace
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
