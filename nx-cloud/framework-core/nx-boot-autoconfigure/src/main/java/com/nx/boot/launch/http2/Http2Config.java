package com.nx.boot.launch.http2;

import io.undertow.UndertowOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName Http2Config
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/23 11:46
 * @Version 1.0
 **/
@Configuration
@ConditionalOnProperty(prefix = "nx.http2",value = "enabled",havingValue = "true")
public class Http2Config {

    @Bean
    UndertowServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(
                builder -> {
                    builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                            .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH,true);
                });

        return factory;
    }
}