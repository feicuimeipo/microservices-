package com.nx.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * 跨域
 * @Author NianXiaoLing
 * @Description TODO
 * @Date 2020/10/20 17:56
 * @Version 1.0
 */
@Slf4j
//@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsWebFilter implements WebFilter {
    private static final String ALL = "*";
    private static final String ICON = "/favicon.ico";
    private static final String MAX_AGE = "86400";

    @Override
    public Mono<Void> filter(ServerWebExchange ctx, WebFilterChain chain) {
        ServerHttpRequest request = ctx.getRequest();
        String path = request.getPath().value();

        //log.info("跨域验证");
        ServerHttpResponse response = ctx.getResponse();
        if (ICON.equals(path)) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(ctx);
        }
        HttpHeaders requestHeaders = request.getHeaders();
        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
        headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
            Collections.singletonList(
                "Origin, No-Cache, X-Requested-With, If-Modified-Since,x_requested_with," +
                "Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,Authorization,token")
        );
        if (requestMethod != null) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "PUT,DELETE,POST,GET,OPTIONS");
        }
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

        if (request.getMethod() == HttpMethod.OPTIONS) {
            log.info("option:"+request.getMethod());
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        return chain.filter(ctx);
    }
}