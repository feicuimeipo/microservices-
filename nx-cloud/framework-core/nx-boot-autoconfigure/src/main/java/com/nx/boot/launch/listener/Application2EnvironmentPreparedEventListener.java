package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 这个事件在Environment对象创建之后，Context对象创建之前，抛出。
 */
@Component
@Slf4j
@ConditionalOnMissingBean(Application2EnvironmentPreparedEventListener.class)
public class Application2EnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    //
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    }


}
