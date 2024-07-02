package com.nx.boot.launch;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import org.springframework.core.Ordered;
import java.util.Properties;

/**
 * @ClassName BootProcessImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/23 17:55
 * @Version 1.0
 **/
@AutoService(NxBootProcessor.class)
public class BootProcessImpl implements NxBootProcessor {

    @Override
    public void launcher(String s, NxBootstrap nxBootstrap, Properties properties, Class aClass) {
        properties.setProperty("spring.cloud.sentinel.enabled", "false");
    }

    @Override
    public int getOrder() {
        //先执行
        return Ordered.HIGHEST_PRECEDENCE - 1000;
    }
}