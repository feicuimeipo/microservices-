package com.nx.gateway.route.core;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.conf.AbstractNxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @ClassName ApplicationBootProcessImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/18 20:12
 * @Version 1.0
 **/
@Slf4j
@AutoService(NxBootProcessor.class)
public class ApplicationBootProcessImpl implements NxBootProcessor {
    @Override
    public void launcher(String applicationName, AbstractNxBootstrap bootstrap, Properties props, Class mainClass) {
        props.setProperty("spring.main.web-application-type","reactive");
        props.setProperty("spring.datasource.driver-class-name","org.h2.Driver");
        props.setProperty("spring.datasource.druid.driver-class-name","org.h2.Driver");
//      spring.datasource.schema=schema.sql
//      spring.datasource.data=data.sql
        props.setProperty("spring.h2.console.enabled","true");
        props.setProperty("spring.h2.console.path","/h2");
        props.setProperty("spring.h2.console.settings.web-allow-others","true");
        props.setProperty("spring.h2.console.settings.trace", "true");
//        if (bootstrap.isLocalDev() && !bootstrap.isProd()) {
//            H2DBServerListener.start();
//            //http://localhost:8082/h2
//            props.setProperty("spring.datasource.druid.url","jdbc:h2:tcp://localhost:9092/~/gateway/gatewaydb;MODE=MYSQL;DB_CLOSE_DELAY=-1;FILE_LOCK=SOCKET;AUTO_SERVER=TRUE;");
//         }else{
            props.setProperty("spring.datasource.druid.url","jdbc:h2:file:~/gateway/gatewaydb;MODE=MYSQL;DB_CLOSE_DELAY=-1;FILE_LOCK=SOCKET;");
//        }

        props.setProperty("spring.datasource.druid.username","sa");
        props.setProperty("spring.datasource.druid.password","sa");
        props.setProperty("spring.datasource.druid.filter.wall.db-type","h2");
        props.setProperty("spring.datasource.druid.filter.wall.config.multi-statement-allow","true");
        props.setProperty("spring.datasource.druid.filter.wall.config.lock-table-allow","false");

        props.setProperty("spring.cloud.sentinel.scg.fallback.redirect","/defaultfallback");
        //props.setProperty("spring.cloud.sentinel.scg.fallback.response-body","error request");

        props.setProperty("spring.cloud.sentinel.scg.fallback.mode","response");
        props.setProperty("spring.cloud.sentinel.scg.fallback.response-status","426");

        props.setProperty("spring.cloud.resilience4j.enabled","false");

        props.setProperty(" spring.cloud.gateway.enabled","true");
        props.setProperty("spring.cloud.gateway.dynamic-routes.config-type","yaml"); //json|yml

        props.setProperty("spring.cloud.gateway.discovery.locator.enabled","true");
        //#开启小写验证：默认feign根据服GatewayCosrconfig查找都是用的全大写
        props.setProperty("spring.cloud.gateway.discovery.locator.lower-case-service-id","true");

        props.setProperty("spring.cloud.gateway.default-filters","StripPrefix=1,SwaggerHeaderFilter");

        props.setProperty("spring.cloud.gateway.discovery.locator.lower-case-service-id","true");
        props.setProperty("spring.cloud.gateway.httpclient.connect-timeout","1000");
        props.setProperty("spring.cloud.gateway.httpclient.response-timeout","20s");
        //#应答超时 java.time.Duration http状态码504

        if (!bootstrap.isProd()) {
            props.setProperty("springfox.documentation.enabled.swagger-ui.enabled", "false");
            props.setProperty("springfox.documentation.enabled", "true");
        }else{
            props.setProperty("springfox.documentation.enabled.swagger-ui.enabled", "false");
            props.setProperty("springfox.documentation.enabled", "false");
        }
        props.setProperty("csp.sentinel.app.type", "1");

    }



}

