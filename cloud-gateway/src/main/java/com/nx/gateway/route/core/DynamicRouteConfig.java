package com.nx.gateway.route.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @ClassName RouteConfig
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/17 9:48
 * @Version 1.0
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gateway.dynamic-route")
@Data
@Order(Ordered.HIGHEST_PRECEDENCE+90)
public class DynamicRouteConfig {
    /**
     * 格式，支持yaml与json
     */
    public String configType="yaml";
    public String getConfigType(){
        if (configType!= null && configType.equals("json")){
            return DynamicRouteConfigType.JSON.getType();
        }else{
            return DynamicRouteConfigType.YAML.getType();
        }
    }


}
