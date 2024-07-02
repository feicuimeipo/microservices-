package com.nx.gateway.conf.gateway;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
///**
// * @ClassName NianxiAuthDalaoyangAuthFilterFactory
// * @Description TODO
// * @Author NIANXIAOLING
// * @Date 2022/6/18 21:52
// * @Version 1.0
// **/
//@Component
//@Slf4j
//public class NianxiAuthDalaoyangAuthFilterFactory extends AbstractGatewayFilterFactory<Object> {
//
//    @Override
//    public GatewayFilter apply(Object config) {
//        return (exchange, chain) -> {
//            ServerHttpRequest host = exchange.getRequest().mutate().headers(httpHeaders -> {
//                httpHeaders.remove("authorization");
//                httpHeaders.add("authorization", "yes");
//                httpHeaders.add("realServerName",
//                        exchange.getRequest().getURI().getHost());
//                //logger.info("headers:" + httpHeaders.toString());
//            }).build();
//
//
//            //将现在的request 变成 change对象
//            ServerWebExchange build = exchange.mutate().request(host).build();
//            return chain.filter(build);
//        };
//    }
//}