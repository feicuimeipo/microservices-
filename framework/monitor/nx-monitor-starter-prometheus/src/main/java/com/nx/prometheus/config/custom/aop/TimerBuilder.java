package com.nx.prometheus.config.custom.aop;

/**
 * @ClassName TimerBuilder
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 19:03
 * @Version 1.0
 **/

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Builder;
import java.util.function.Consumer;

public class TimerBuilder {

    private final MeterRegistry meterRegistry;

    private Timer.Builder builder;

    private Consumer<Builder> consumer;

    public TimerBuilder(MeterRegistry meterRegistry, String name, Consumer<Timer.Builder> consumer) {
        this.builder = Timer.builder(name);
        this.meterRegistry = meterRegistry;
        this.consumer = consumer;
    }

    public Timer build() {
        this.consumer.accept(builder);
        return builder.register(meterRegistry);
    }
}