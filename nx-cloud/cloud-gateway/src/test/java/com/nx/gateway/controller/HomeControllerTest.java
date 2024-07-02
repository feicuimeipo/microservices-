package com.nx.gateway.controller;

import com.nx.gateway.MyGatewayApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 *  测试get请求 @SpringBootTest 会根据路径寻找 @SpringBootApplication 或 @SpringBootConfiguration所以请保持test目录结构与main目录结构相同。
 *  否则要Controller接口测试
 *  AutoConfigureMockMvc“：使用MockMvc
 * @ClassName HomeControllerTest
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/19 16:16
 * @Version 1.0
 **/
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc //使用MockMvc
@SpringBootTest(
        classes = MyGatewayApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
public class HomeControllerTest {


    @Autowired
    WebTestClient webTestClient;

    @Test
    public void test1() throws Exception {

        webTestClient.get().uri("/").exchange().expectStatus().isOk();
    }
}
