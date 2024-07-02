package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 *  启动 Resilience4j 示例项目。
 *  - ① 使用浏览器，访问 http://127.0.0.1:8080/rate-limiter-demo/get_user?id=1 地址，成功返回结果为 User:1。
 *  - ② 立马使用浏览器再次访问，会阻塞等待 < 5 秒左右，降级返回 mock:User:1。同时，我们在 IDEA 控制台的日志中，可以看到被限流时抛出的 RequestNotPermitted 异常。
 *    [ 2020-05-19 21:50:42.585  INFO 79815 --- [nio-8080-exec-1] c.i.s.l.r.c.RateLimiterDemoController    : [getUserFallback][id(1) exception(RequestNotPermitted)] ]
 *  我们将 @RateLimiter 和 @CircuitBreaker 注解添加在相同方法上，进行组合使用，来实现限流和断路的作用。但是要注意，需要添加 resilience4j.circuitbreaker.instances.<instance_name>.ignoreExceptions=io.github.resilience4j.ratelimiter.RequestNotPermitted 配置项，忽略限流抛出的 RequestNotPermitted 异常，避免触发断路器的熔断。
 *
 *
 */
@RestController
@RequestMapping("/rate-limiter-demo")
public class RateLimiterDemoController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * ① 在 #getUser(Integer id) 方法中，我们直接返回 "User:{id}"，不进行任何逻辑。     *
     * ② 在 #getUser(Integer id) 方法上，添加了 Resilience4j 提供的 @RateLimiter 注解：
     * 通过 name 属性，设置对应的 RateLimiter 实例名为 "backendB"，就是我们在「3.1 配置文件」中所添加的。
     * 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     * 在请求被限流时，Resilience4j 不会执行 #getUser(Integer id) 方法，而是直接抛出 RequestNotPermitted 异常，然后就进入 fallback 降级处理。
     *
     * 友情提示：注意，@RateLimiter 注解的 fallbackMethod 属性对应的 fallback 方法，不仅仅处理被限流时抛出的 RequestNotPermitted 异常，也处理 #getUser(Integer id) 方法执行时的普通异常。
     */
    @GetMapping("/get_user")
    @RateLimiter(name = "backendB", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        return "User:" + id;
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}