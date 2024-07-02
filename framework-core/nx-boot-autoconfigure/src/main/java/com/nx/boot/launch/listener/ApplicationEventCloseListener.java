package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.boot.launch.spi.NxApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 *
 *
 * @ClassName ApplicationEventClose
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/17 11:55
 * @Version 1.0
 **/
@Component
@Slf4j
@ConditionalOnMissingBean(ApplicationEventCloseListener.class)
public class ApplicationEventCloseListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            ApplicationEventManager.heartbeat(ApplicationEventManager.HeartBeatType.destory);
            ApplicationEventManager.releasebeat();
        }catch (Exception e){
            log.warn(e.getMessage(),e);
        }

        NxLaunchTools.printInfo(this.getClass(),"应用已关闭!");

        List<NxApplicationListener> listener = new ArrayList<>();
        ServiceLoader.load(NxApplicationListener.class).forEach(listener::add);
        listener.stream().sorted(Comparator.comparing(NxApplicationListener::getOrder)).collect(Collectors.toList())
                .forEach(launcherService -> launcherService.onApplicationEvent(event));
    }
}
