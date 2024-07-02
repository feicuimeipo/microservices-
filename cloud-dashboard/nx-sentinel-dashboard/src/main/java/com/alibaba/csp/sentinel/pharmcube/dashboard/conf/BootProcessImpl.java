package com.alibaba.csp.sentinel.pharmcube.dashboard.conf;

import com.google.auto.service.AutoService;
import com.pharmcube.boot.launch.conf.AbstractPharmcubeBootstrap;
import com.pharmcube.boot.launch.spi.PharmcubeBootProcessor;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * @ClassName BootProcessImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/25 15:18
 * @Version 1.0
 **/
@AutoService(PharmcubeBootProcessor.class)
public class BootProcessImpl implements PharmcubeBootProcessor {
    @Override
    public void launcher(String applicationName, AbstractPharmcubeBootstrap bootstrap, Properties props, Class mainClass) {
        bootstrap.setSentinelAddr(null);
        bootstrap.setDubboEnabled(false);
        bootstrap.setDiscoveryEnabled(false);

        props.setProperty("nacos.namespace",bootstrap.getCloudNamespace());
        props.setProperty("nacos.groupId",bootstrap.getCloudGroup());
        props.setProperty("nacos.address",bootstrap.getCloudServerAddr());
        props.setProperty("nacos.username",bootstrap.getCloudServerUserName());
        props.setProperty("nacos.password",bootstrap.getCloudServerPassword());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE-1000;
    }
}
