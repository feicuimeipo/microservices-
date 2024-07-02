package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 测试：
 * ① 使用浏览器，访问 http://127.0.0.1:8080/bulkhead-demo/get_user?id=1 地址，成功返回结果为 User:1。
 * ② 立马使用浏览器再次访问，会阻塞等待 < 5 秒左右，降级返回 mock:User:1。同时，我们在 IDEA 控制台的日志中，可以看到被流控时抛出的 BulkheadFullException 异常。
 * 测试结果：
 *  2020-05-20 07:39:17.181  INFO 84230 --- [nio-8080-exec-2] c.i.s.l.r.c.BulkheadDemoController       : [getUserFallback][id(1) exception(BulkheadFullException)]
 */
@RestController
@RequestMapping("/bulkhead-demo")
@Slf4j
public class BulkheadDemoController {

    /**
     * ① 在 #getUser(Integer id) 方法中，我们直接返回 "User:{id}"，不进行任何逻辑。不过，这里为了模拟调用执行一定时长，通过 sleep 10 秒来实现。
     * ② 在 #getUser(Integer id) 方法上，添加了 Resilience4j 提供的 @Bulkhead 注解：
     * 通过 name 属性，设置对应的 Bulkhead 实例名为 "backendC"，就是我们在「4.1 配置文件」中所添加的。
     * 通过 type 属性，设置 Bulkhead 类型为信号量的方式。
     * 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     * 在请求被流控时，Resilience4j 不会执行 #getUser(Integer id) 方法，而是直接抛出 BulkheadFullException 异常，然后就进入 fallback 降级处理。
     * 友情提示：注意，@Bulkhead 注解的 fallbackMethod 属性对应的 fallback 方法，不仅仅处理被流控时抛出的 BulkheadFullException 异常，也处理 #getUser(Integer id) 方法执行时的普通异常。
     */
    @GetMapping("/get_user")
    @Bulkhead(name = "backendC", fallbackMethod = "getUserFallback", type = Bulkhead.Type.SEMAPHORE)
    public String getUser(@RequestParam("id") Integer id) throws InterruptedException {
        log.info("[getUser][id({})]", id);
        Thread.sleep(10 * 1000L); // sleep 10 秒
        return "User:" + id;
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        log.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }
}