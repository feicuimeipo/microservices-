package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 启动 Resilience4j 示例项目。
 * 调用 http://127.0.0.1:8080/thread-pool-bulkhead-demo/get_user?id=1 接口，可以通过日志看出，ThreadPoolBulkheadService 的 #getUser0(Integer id) 方法是“串行”执行的：
 *
 *  -- 输出 (相互之间间隔 10 秒，因为我们在 #getUser0(Integer id) 方法是 sleep 了 10 秒。)
 * 2020-05-20 8:25:24.059  INFO 85835 --- [head-backendD-1] DemoController$ThreadPoolBulkheadService : [getUser][id(1)]
 * 2020-05-20 8:25:34.066  INFO 85835 --- [head-backendD-1] DemoController$ThreadPoolBulkheadService : [getUser][id(1)]
 *
 */
@Slf4j
@RestController
@RequestMapping("/thread-pool-bulkhead-demo")
public class ThreadPoolBulkheadDemoController {

    @Autowired
    private ThreadPoolBulkheadService threadPoolBulkheadService;

    /**
     * ① 在 #getUser(Integer id) 方法中，我们调用了 2 次 ThreadPoolBulkheadService 的 #getUser0(Integer id) 方法，测试在线程池 Bulkhead 下，且线程池大小为 1 时，被流控成“串行”执行。
     * ② 在 #getUser0(Integer id) 方法上，添加了 Resilience4j 提供的 @Bulkhead 注解：
     * - 通过 name 属性，设置对应的 Bulkhead 实例名为 "backendC"，就是我们在「4.4 配置文件」中所添加的。
     * - 通过 type 属性，设置 Bulkhead 类型为线程池的方式。
     * - 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     * - 注意！！！方法的返回类型必须是 CompletableFuture 类型，包括 fallback 方法，否则会报异常，毕竟要提交线程池中执行。
     * - 在请求被流控时，Resilience4j 不会执行 #getUser0(Integer id) 方法，而是直接抛出 BulkheadFullException 异常，然后就进入 fallback 降级处理。不过艿艿测试了很久，都没触发抛出 BulkheadFullException 异常的情况，但是看 Resilience4j 源码又有这块逻辑，苦闷~
     *
     * 友情提示：注意，@Bulkhead 注解的 fallbackMethod 属性对应的 fallback 方法，不仅仅处理被流控时抛出的 BulkheadFullException 异常，也处理 #getUser0(Integer id) 方法执行时的普通异常。
     * 友情提示：这里创建了 ThreadPoolBulkheadService 的原因是，这里我们使用 Resilience4j 是基于注解 + AOP的方式，如果直接 this. 方式来调用方法，实际没有走代理，导致 Resilience4j 无法使用 AOP。
     *
     */
    @GetMapping("/get_user")
    public String getUser(@RequestParam("id") Integer id) throws ExecutionException, InterruptedException {
        threadPoolBulkheadService.getUser0(id);
        return threadPoolBulkheadService.getUser0(id).get();
    }

    @Service
    public static class ThreadPoolBulkheadService {

        @Bulkhead(name = "backendD", fallbackMethod = "getUserFallback", type = Bulkhead.Type.THREADPOOL)
        public CompletableFuture<String> getUser0(Integer id) throws InterruptedException {
            log.info("[getUser][id({})]", id);
            Thread.sleep(10 * 1000L); // sleep 10 秒
            return CompletableFuture.completedFuture("User:" + id);
        }

        public CompletableFuture<String> getUserFallback(Integer id, Throwable throwable) {
            log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
            return CompletableFuture.completedFuture("mock:User:" + id);
        }

    }


}