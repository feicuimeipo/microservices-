package com.nx.gateway.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @ClassName RouterConfig
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/12 21:49
 * @Version 1.0
 **/
public class GatewayRouterConfig {
    @Autowired
    private RouterHandler routerHandler;

    @Bean
    public RouterFunction<ServerResponse> demoRouter(){
        //路由函数的编写
        return route(GET("/contact"),routerHandler::contact)
                .andRoute(GET("/times"),routerHandler::times);
    }


    @Component
    public static class RouterHandler {

        public Mono<ServerResponse> contact(ServerRequest request){
            return ok().contentType(MediaType.TEXT_PLAIN)
                    .body(Mono.just("xlnian@163.com"),String.class);
        }

        public Mono<ServerResponse> times(ServerRequest request){
            //每隔一秒发送当前的时间
            return ok().contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(Flux.interval(Duration.ofSeconds(1))
                            .map(it -> new SimpleDateFormat("HH:mm:ss").format(new Date())),String.class);
        }
    }

//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder){
//        //String str = "http://localhost:8096/csdn";
//        return builder.routes()
//                //id 表示被转发到uri地址的id名，
//                .route("id",p -> p
//                        //predicates，当访问的连接满足http://localhost:8096/csdn时即转发到https://blog.csdn.net
//                        .path("/csdn")
//                        .uri("https://blog.csdn.net"))
//                .build();
//    }
}
