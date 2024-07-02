package com.nx.xxx.common.spimpl;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.spi.NxApplicationListener;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;

/**
 * @ClassName AppEventListener
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/10 18:07
 * @Version 1.0
 **/
@AutoService(NxApplicationListener.class)
public class AppEventListener implements NxApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        //启动失败
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //应用启动
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        //容器启动
    }
}
