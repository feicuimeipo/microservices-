package com.nx.skywalking.juc;


import ch.qos.logback.classic.util.LogbackMDCAdapter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class EnhanceMDCAdapterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        new LogbackMDCAdapter();
    }
}
