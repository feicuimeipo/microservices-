package com.alibaba.csp.sentinel.nx.dashboard.conf;


import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import com.nx.boot.launch.conf.NxBootstrapConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class NacosConfig {

    @Value("${nacos.address:''}")
    private String address;
    @Value("${nacos.namespace:''}")
    private String namespace;
    @Value("${nacos.username:''}")
    private String username;
    @Value("${nacos.password:''}")
    private String passwrod;

    @Bean
    @ConditionalOnMissingBean(ConfigService.class)
    public ConfigService nacosConfigService() throws Exception {

        try{
            Class.forName("com.nx.boot.launch.NxSpringBootApplicationBuilder");
            com.nx.boot.launch.conf.NxBootstrapConfig bootstrap = com.nx.boot.launch.NxSpringBootApplicationBuilder.bootstrap();
            if (!StringUtils.hasLength(namespace)) {
                namespace = bootstrap.getCloudNamespace();
            }
            if (!StringUtils.hasLength(address)) {
                address = bootstrap.getCloudServerAddr();
            }

            if (!StringUtils.hasLength(username)) {
                username = bootstrap.getCloudServerUserName();
            }
            if (!StringUtils.hasLength(passwrod)) {
                passwrod = bootstrap.getCloudServerPassword();
            }
        }catch (ClassNotFoundException e){

        }

        Properties properties = new Properties();
        if (StringUtils.hasLength(namespace)) {
            properties.put(PropertyKeyConst.NAMESPACE, namespace);
        }
        properties.put(PropertyKeyConst.SERVER_ADDR, address);
        properties.put(PropertyKeyConst.USERNAME, username);
        properties.put(PropertyKeyConst.PASSWORD, passwrod);

        if (properties.get(PropertyKeyConst.SERVER_ADDR)!=null
                && !properties.get(PropertyKeyConst.SERVER_ADDR).toString().equals("")) {
            return ConfigFactory.createConfigService(properties);
        }
        return null;
    }



}
