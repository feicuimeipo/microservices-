package com.nx.gateway;

import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @ClassName   MyGatewayApplication
 * @Description ....
 * @Author      NianXiaoLing
 * @Date         2022/6/12 17:51
 * @Version 1.0
 *
 **/
@EnableFeignClients
@EnableDiscoveryClient
@RestController
@SpringBootApplication
public class MyGatewayApplication  {
    public static void main(String[] args) {
        NxSpringBootApplicationBuilder.run(MyGatewayApplication.class, args);
    }
}
