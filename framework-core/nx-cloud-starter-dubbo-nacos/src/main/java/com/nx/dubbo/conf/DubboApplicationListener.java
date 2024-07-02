package com.nx.dubbo.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.DubboShutdownHook;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.PriorityOrdered;


/**
 * TODO:优雅停机
 *
 * @ClassName DubboApplicationListener
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/17 11:43
 * @Version 1.0
 **/
@Configuration
@Slf4j
public class DubboApplicationListener   {


    @Bean
    DubboShutdownListener dubboShutdownListener() {
        return new DubboShutdownListener();
    }

    public static class DubboShutdownListener implements ApplicationListener, PriorityOrdered {

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            DubboShutdownHook dubboShutdownHook = new DubboShutdownHook(DubboBootstrap.getInstance().getApplicationModel());
            if (event instanceof ApplicationStartedEvent) {
                log.info("dubbo default shutdown hook removed,will be managed by spring");
                Runtime.getRuntime().removeShutdownHook(dubboShutdownHook);
            } else if (event instanceof ContextClosedEvent) {
                log.info("start destroy dubbo on spring close event");
                DubboBootstrap.getInstance().getApplicationModel().destroy();
                log.info("dubbo destroy finished");
            }
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }

}
