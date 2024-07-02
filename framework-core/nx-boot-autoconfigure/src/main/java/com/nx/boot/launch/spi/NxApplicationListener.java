package com.nx.boot.launch.spi;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;

public interface NxApplicationListener extends Ordered, Comparable<NxApplicationListener>  {

    //启动失败
    public void onApplicationEvent(ApplicationFailedEvent event);

    //准备就绪
    public void onApplicationEvent(ApplicationReadyEvent event);

    //准备就绪
    default void onApplicationEvent(WebServerInitializedEvent event){}


    // 准备就绪
    default void onApplicationEvent(ApplicationStartedEvent event){}


    default void onApplicationEvent(ContextClosedEvent event){}

    //获取排列顺序
    @Override
    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    // 对比排序
    @Override
    default int compareTo(NxApplicationListener o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }

}
