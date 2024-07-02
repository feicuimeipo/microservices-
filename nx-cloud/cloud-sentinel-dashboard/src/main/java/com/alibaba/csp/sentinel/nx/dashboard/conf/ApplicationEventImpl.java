package com.alibaba.csp.sentinel.nx.dashboard.conf;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.spi.NxApplicationListener;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * @ClassName ApplicationEventImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/26 22:35
 * @Version 1.0
 **/
@AutoService(NxApplicationListener.class)
public class ApplicationEventImpl implements NxApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {

    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

    }


}
