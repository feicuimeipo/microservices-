package com.nx.http2.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @ClassName Http2AutoConfiguration
 * @Description ...
 * @Author NianXiaoLing
 * @Date 2022/6/23 11:46
 * @Version 1.0
 **/
@Import(Http2AutoConfiguration.class)
public class Http2AutoConfiguration {
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