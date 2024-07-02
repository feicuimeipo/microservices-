package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.nx.cloud.protection.resilience4j.utils.ResilienceConstant.BACKEND;


/**
 * 启动 Resilience4j 示例项目。
 * 调用 http://127.0.0.1:8080/time-limiter-demo/get_user?id=1 接口，可以通过日志看出，看到执行超时抛出 TimeoutException 异常，而后进入 fallback 服务降级，因此最终返回结果为 mock:User:1。
 *
 * 2020-05-20 23:35:50.633  INFO 93020 --- [head-backendD-1] LimiterDemoController$TimeLimiterService : [getUser][id(1)]
 * 2020-05-20 23:35:51.637  INFO 93020 --- [pool-2-thread-1] LimiterDemoController$TimeLimiterService : [getUserFallback][id(1) exception(TimeoutException)]
 *
 *
 */
@RestController
@RequestMapping("/time-limiter-demo")
public class TimeLimiterDemoController {

    @Autowired
    private TimeLimiterService timeLimiterService;

    /**
     * ① 在 #getUser(Integer id) 方法中，直接调用 ThreadPoolBulkheadService 的 #getUser0(Integer id) 方法进行返回。     *
     * ② 在 #getUser0(Integer id) 方法上，添加了 Resilience4j 提供的 @TimeLimiter 注解：     *
     * - 通过 name 属性，设置对应的 Bulkhead 实例名为 "backendC"，就是我们在「4.4 配置文件」中所添加的。
     * - 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     *
     *   注意！！！方法的返回类型必须是 CompletableFuture 类型，包括 fallback 方法，否则会报异常，毕竟要提交线程池中执行。
     *   在请求执行超时时，Resilience4j 会抛出 TimeoutException 异常，然后就进入 fallback 降级处理。
     *   > 注意，@Bulkhead 注解的 fallbackMethod 属性对应的 fallback 方法，不仅仅处理被流控时抛出的 BulkheadFullException 异常，也处理 #getUser0(Integer id) 方法执行时的普通异常。
     *
     * ③ 在 #getUser0(Integer id) 方法上，添加了 Resilience4j 提供的 @Bulkhead 注解，并设置类型为线程池，原因上文我们也解释过了。
     */
    @GetMapping("/get_user")
    public String getUser(@RequestParam("id") Integer id) throws ExecutionException, InterruptedException {
        return timeLimiterService.getUser0(id).get();
    }

    /**
     * 友情提示：这里创建了 TimeLimiterService 的原因是，这里我们使用 Resilience4j 是基于注解 + AOP的方式，如果直接 this. 方式来调用方法，实际没有走代理，导致 Resilience4j 无法使用 AOP。
     */
    @Service
    public static class TimeLimiterService {

        private Logger logger = LoggerFactory.getLogger(TimeLimiterService.class);

        @Bulkhead(name = "backendD", type = Bulkhead.Type.THREADPOOL)
        @TimeLimiter(name = "backendF", fallbackMethod = "getUserFallback")
        public CompletableFuture<String> getUser0(Integer id) throws InterruptedException {
            logger.info("[getUser][id({})]", id);
            Thread.sleep(10 * 1000L); // sleep 10 秒
            return CompletableFuture.completedFuture("User:" + id);
        }

        public CompletableFuture<String> getUserFallback(Integer id, Throwable throwable) {
            logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
            return CompletableFuture.completedFuture("mock:User:" + id);
        }


        @CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
        @RateLimiter(name = BACKEND)
        @Bulkhead(name = BACKEND)
        @Retry(name = BACKEND, fallbackMethod = "fallback")
        @TimeLimiter(name = BACKEND)
        public String method(String param1) throws Exception{
          throw new RuntimeException();
        }

        private String fallback(String param1, IllegalArgumentException e) {
            return "test:IllegalArgumentException";
        }

        private String fallback(String param1, RuntimeException e) {
            return "test:RuntimeException";
        }

    }


}