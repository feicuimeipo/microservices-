package com.nx.boot.launch.listener;

import com.nx.boot.launch.spimpl.ProfileIncludeNxBootProcessorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnMissingBean(Application1StartingEventListener.class)
public class Application1StartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {


        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new SimpleCommandLinePropertySource(applicationStartingEvent.getArgs()));
        environment.getPropertySources().addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
        environment.getPropertySources().addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));

        ProfileIncludeNxBootProcessorImpl.configIncludeProperties(System.getProperties());


    }


}
