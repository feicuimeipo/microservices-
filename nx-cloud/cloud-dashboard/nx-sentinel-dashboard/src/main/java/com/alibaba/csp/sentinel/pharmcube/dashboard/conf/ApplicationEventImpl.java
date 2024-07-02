package com.alibaba.csp.sentinel.pharmcube.dashboard.conf;

import com.google.auto.service.AutoService;
import com.pharmcube.boot.launch.spi.PharmcubeApplicationListener;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * @ClassName ApplicationEventImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/26 22:35
 * @Version 1.0
 **/
@AutoService(PharmcubeApplicationListener.class)
public class ApplicationEventImpl implements PharmcubeApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {

    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

    }


}
