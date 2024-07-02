package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.boot.launch.spi.NxApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**顺序：应用容器、Started、Ready事件，
 * @ClassName Application6StartedEventListener
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/17 11:47
 * @Version 1.0
 **/
@Component
@Slf4j
@ConditionalOnMissingBean(Application6StartedEventListener.class)
public class Application6StartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    ApplicationContext applicationContext;


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        //printInfo(String.format("Application6StartedEventListener"));
        applicationContext = event.getApplicationContext();

        List<NxApplicationListener> listener = new ArrayList<>();
        ServiceLoader.load(NxApplicationListener.class).forEach(listener::add);
        listener.stream().sorted(Comparator.comparing(NxApplicationListener::getOrder)).collect(Collectors.toList())
                .forEach(launcherService -> launcherService.onApplicationEvent(event));
    }
}
