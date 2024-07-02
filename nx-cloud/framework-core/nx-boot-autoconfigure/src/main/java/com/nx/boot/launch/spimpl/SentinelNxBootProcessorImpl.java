package com.nx.boot.launch.spimpl;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @ClassName
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@Slf4j
@AutoService(NxBootProcessor.class)
public class SentinelNxBootProcessorImpl implements NxBootProcessor {

    /**props.setProperty("spring.cloud.sentinel.enabled", "false");
     *
     * 来自控制台
     */
    @Override
    public void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass) {
        try{
            if (!props.getProperty("spring.cloud.sentinel.enabled","").equals("false")) {
                Class.forName("com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration");
                if (StringUtils.hasLength(bootstrap.getSentinelAddr())) {
                    props.setProperty("spring.cloud.sentinel.enabled", "true");
                    props.setProperty("feign.sentinel.enabled", "true");
                    props.setProperty("spring.cloud.resilience4j.enabled", "false"); //#服务注启动，直接注册到dashboard
                }
            }
        }catch (Exception e){
            log.warn(e.getMessage());
            props.setProperty(" spring.cloud.sentinel.enabled","false");
            props.setProperty("feign.sentinel.enabled","false");
        }
        if (StringUtils.hasLength(bootstrap.getSentinelAddr())) {
            props.setProperty("spring.cloud.sentinel.eager", "true"); //#服务注启动，直接注册到dashboard
             props.setProperty("spring.cloud.sentinel.transport.heartbeat-interval-ms", "1000");
            props.setProperty("spring.cloud.sentinel.enabled", "true");
            props.setProperty("spring.cloud.sentinel.transport.dashboard", bootstrap.getSentinelAddr());
            props.setProperty("spring.cloud.sentinel.transport.port", bootstrap.getSentinelPort().toString());
        }


        try {
            //<artifactId>sentinel-datasource-nacos</artifactId>
            //Class.forName("com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource");
            Class.forName("com.alibaba.cloud.sentinel.datasource.config.DataSourcePropertiesConfiguration");
            if (StringUtils.hasLength(bootstrap.getCloudServerAddr())) {
                //#流量控制
                setDsProperties(bootstrap, props, "flow", "-flow-rules");
                //#熔断降级,除流控以外的其他规则配置,可以选择性配置,也就是持久化哪个规则就配哪个
                setDsProperties(bootstrap, props, "degrade", "-degrade-rules");
                //#热点规则
                setDsProperties(bootstrap, props, "param-flow", "-param-flow-rules");
                //#系统规则
                setDsProperties(bootstrap, props, "system", "-system-rules");
                //#授权规则
                setDsProperties(bootstrap, props, "authority", "-authority-rules");
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            //Class.forName("com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource");
            Class.forName("com.alibaba.cloud.sentinel.datasource.config.DataSourcePropertiesConfiguration");
            Class.forName("com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRules");
            if (StringUtils.hasLength(bootstrap.getCloudServerAddr())) {
                setDsProperties(bootstrap, props, "gw-api-group", "-gw-api-group-rules");
                setDsProperties(bootstrap, props, "gw-flow", "-gw-flow-rules");  //#网关API
                setDsProperties(bootstrap, props, "gw-api-predicate", "-gw-api-predicate-rules");
                setDsProperties(bootstrap, props, "gw-param-flow", "-gw-param-flow-rules");  //#网关API
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+5;
    }


    private void setDsProperties(NxBootstrap bootstrap, Properties props, String type, String dataIdPrefix){
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.server-addr", bootstrap.getCloudServerAddr());
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.data-id", bootstrap.getConfigPrefix() + dataIdPrefix);
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.groupId", "SENTINEL_GROUP");
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.namespace", bootstrap.getCloudNamespace());
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.data-type", "json");
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.rule-type", type);
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.username", bootstrap.getCloudServerUserName());
        props.setProperty("spring.cloud.sentinel.datasource."+ type +".nacos.password", bootstrap.getCloudServerPassword());
    }
}
