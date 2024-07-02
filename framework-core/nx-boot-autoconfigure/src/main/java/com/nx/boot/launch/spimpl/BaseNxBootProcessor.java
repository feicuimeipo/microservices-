package com.nx.boot.launch.spimpl;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import java.util.Properties;

/**
 * @ClassName BootProcessorListenerImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@Slf4j
@AutoService(NxBootProcessor.class)
public class BaseNxBootProcessor implements NxBootProcessor {

    @Override
    public void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass) {
        props.setProperty("spring.config.location",bootstrap.getConfigLocation());
        props.setProperty("spring.application.name", applicationName);
        props.setProperty("spring.profiles.active", bootstrap.getProfile());
        props.setProperty("application.name", applicationName);
        props.setProperty("server.port", bootstrap.getServerPort().toString());
        props.setProperty("spring.mvc.pathmatch.matching-strategy", "ant_path_matcher");
        props.setProperty("spring.mvc.throw-exception-if-no-handler-found", "true");
        props.setProperty("spring.main.allow-bean-definition-overriding", "true");
        

        props.setProperty("nx.boot.base-package", mainClass.getPackage().getName());
        props.setProperty("nx.boot.author", "nianxi");
        props.setProperty("nx.boot.version", bootstrap.getVersion());
        props.setProperty("nx.boot.tenant.enabled",String.valueOf(bootstrap.isTenantEnabled()));
        props.setProperty("nx.boot.launch.dev-mode", bootstrap.isProd()?"false":"true");
        props.setProperty("nx.boot.local-dev", String.valueOf(bootstrap.isLocalDev()));
        props.setProperty("nx.boot.main-class",mainClass.getName());


        props.setProperty("spring.http.encoding","UTF-8");
        props.setProperty("spring.application.version",bootstrap.getVersion());
        props.setProperty("server.port",bootstrap.getServerPort().toString());


        props.setProperty("management.server.port",bootstrap.getManagementServerPort().toString());
        props.setProperty("management.metrics.tags.application",bootstrap.getApplicationName());

        props.setProperty("management.metrics.export.simple.enabled","false");
        props.setProperty("management.metrics.export.prometheus.enabled","true");

        props.setProperty("management.endpoint.shutdown.enabled","true");
        props.setProperty("management.endpoint.prometheus.enabled","true");
        props.setProperty("management.health.elasticsearch.enabled","true");

        props.setProperty("management.endpoint.health.show-details","always");
        props.setProperty("management.endpoint.health.prometheus.enabled","true");
        props.setProperty("management.endpoint.health.elasticsearch.enabled","true");


        if (bootstrap.isProd()){
            props.setProperty("management.endpoints.web.exposure.include","*");
        }else{
            props.setProperty("management.endpoints.web.exposure.include","*");
        }
                
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+1;
    }


}
