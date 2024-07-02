package com.nx.gateway.conf.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ReactiveContextHolder
 * @Author NIANXIAOLING
 * @Description TODO
 * @Date 2020/10/21 14:22
 * @Version 1.0
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(value={org.springframework.http.server.reactive.ServerHttpRequest.class})
public class ReactiveContextHolder  {

    public static ServerHttpRequest getServerHttpRequest() {
        return getServerWebExchange().getRequest();
    }

    public static Mono<ServerWebExchange> getExchange() {
        return Mono.subscriberContext().map(ctx -> ctx.get(ServerWebExchange.class));
    }

    public static ServerWebExchange getServerWebExchange() {
        Mono<ServerWebExchange> mono = getExchange();
        ServerWebExchange exchange =  mono.block();
        return exchange;
    }

    public static ServerHttpResponse getResponse() {
        return getServerWebExchange().getResponse();
    }


    /**
     * 读取body内容
     * @param serverHttpRequest
     * @return
     */
    public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
//            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return formatStr(sb.toString());
    }

    /**
     * 去掉空格,换行和制表符
     * @param str
     * @return
     */
    private static String formatStr(String str){
        //Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
        if (str != null && str.length() > 0) {
            Matcher m = Pattern.compile("\\s*|\t|\r|\n").matcher(str);
            return m.replaceAll("");
        }
        return str;
    }


}





