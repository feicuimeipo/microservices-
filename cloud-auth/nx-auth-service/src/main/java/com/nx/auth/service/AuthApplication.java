package com.nx.auth.service;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * http://www.bubuko.com/infodetail-3773981.html
 * https://www.cnblogs.com/xiaoqi/p/spring-security-rabc.html
 * https://blog.csdn.net/tiancxz/article/details/108856337
 * https://www.cnblogs.com/cnblog-user/p/16375282.html
 */
@Slf4j
@MapperScan("com.nx.auth.service.dao")
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class AuthApplication {
    public static void main(String[] args)  {
        new SpringApplication(AuthApplication.class).run(args);
    }

}
