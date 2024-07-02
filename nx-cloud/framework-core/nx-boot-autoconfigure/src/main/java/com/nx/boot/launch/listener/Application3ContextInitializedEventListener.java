package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 这个事件是在一个SpringApplication对象的初始化和监听器的注册之后，抛出的
 */
@Component
@Slf4j
@ConditionalOnMissingBean(Application3ContextInitializedEventListener.class)
public class Application3ContextInitializedEventListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
    }
}
