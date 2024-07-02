package com.nx.boot.launch.spimpl;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @ClassName BootProcessorListenerImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@AutoService(NxBootProcessor.class)
public class DubboNxBootProcessorImpl implements NxBootProcessor {

    @Override
    public void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass) {
        if (!bootstrap.isDubboEnabled()) {
            props.setProperty("dubbo.enabled","false");
            return;
        }else{
            try{
                Class.forName("org.apache.dubbo.config.ApplicationConfig");
            }catch (Exception e) {
                bootstrap.setDubboEnabled(false);
                props.setProperty("dubbo.enabled","false");
                return;
            }
        }

        if (!bootstrap.isDubboEnabled()){
            props.setProperty("dubbo.enabled","false");
            //props.setProperty("dubbo.application.register-mode","");
            return;
        }
        if (bootstrap.isDubboEnabled()){
            props.setProperty("dubbo.enabled","true");
            props.setProperty("dubbo.service.shutdown.wait","15000");
            props.setProperty("dubbo.register.mode.",bootstrap.isDubboDiscoveryEnabled()?"服务发现模式":"直连模式！");

            if (bootstrap.isDubboDiscoveryEnabled()){
                props.setProperty("spring.dubbo.server", "true");
                props.setProperty("dubbo.nacos.address", "N/A");
                props.setProperty("dubbo.metadata-report.address", "N/A");
            }else{

                String serverAddr = bootstrap.getDubboServerAddr();

                if (StringUtils.hasLength(serverAddr)){
                    String host = serverAddr.substring(0,serverAddr.indexOf(":"));
                    String port = serverAddr.substring(serverAddr.indexOf(":")+1);
                    //# 将注册中心地址、元数据中心地址等配置集中管理，可以做到统一环境、减少开发侧感知。

                    props.setProperty("dubbo.nacos.address",  host);
                    props.setProperty("dubbo.nacos.port",  port);
                    props.setProperty("dubbo.registry.simplified", "true");
                }

                if (StringUtils.hasLength(bootstrap.getCloudNamespace())){
                    props.setProperty("dubbo.nacos.namespace",bootstrap.getCloudNamespace());
                }

                if (StringUtils.hasLength(bootstrap.getCloudGroup())){
                    props.setProperty("dubbo.nacos.group",bootstrap.getCloudGroup());
                }

                if (StringUtils.hasLength(bootstrap.getCloudServerUserName())){
                    props.setProperty("dubbo.nacos.username",  bootstrap.getDubboServerUserName());
                }

                if (StringUtils.hasLength(bootstrap.getCloudServerPassword())){
                    props.setProperty("dubbo.nacos.password",  bootstrap.getDubboServerPassword());
                }

                if (StringUtils.hasLength(bootstrap.getDubboProtocolName())){
                    props.setProperty("dubbo.protocolName",  bootstrap.getDubboProtocolName());
                }

                if (bootstrap.getDubboPort()!=0){
                    props.setProperty("dubbo.protocolPort",  String.valueOf(bootstrap.getDubboPort()));
                }

                //props.setProperty("dubbo.application.register-mode","");
                props.setProperty("dubbo.registry.timeout",  "50000");
                props.setProperty("dubbo.scan.base-packages", bootstrap.getDubboScanPackages());

                props.setProperty("dubbo.protocol.threadpool", "fixed");
                props.setProperty("dubbo.protocol.threads", "500");
                props.setProperty("dubbo.protocol.payload", "500");
                props.setProperty("dubbo.naccos.version", bootstrap.getVersion());
            }
        }
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+3;
    }
}
