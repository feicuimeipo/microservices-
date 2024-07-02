package com.nx.xxx;

import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDubbo
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args)  {
        NxSpringBootApplicationBuilder.run(AdminApplication.class,args);
    }

}
