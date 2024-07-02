package com.nx.prometheus.config.custom.aop;

/**
 * @ClassName Metrics
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 19:01
 * @Version 1.0
 **/

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Counter.Builder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Consumer;

public class Metrics implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static Counter newCounter(String name, Consumer<Builder> consumer) {
        MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);
        return new CounterBuilder(meterRegistry, name, consumer).build();
    }

    public static Timer newTimer(String name, Consumer<Timer.Builder> consumer) {
        return new TimerBuilder(context.getBean(MeterRegistry.class), name, consumer).build();
    }
}