package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 *
 * 启动 Resilience4j 示例项目
 *
 * ① 使用浏览器，访问 http://127.0.0.1:8080/demo/get_user?id=1 地址，成功调用用户服务，返回结果为 User:1
 * ② 停止 UserServiceApplication 关闭用户服务。 *
 * 使用浏览器，访问 http://127.0.0.1:8080/demo/get_user?id=1 地址，失败调用用户服务，返回结果为 mock:User:1。
 * 此时我们会看到如下日志，可以判断触发 Resilience4j 的 fallback 服务降级的方法。
 *  * 2020-05-19 08:46:44.094  INFO 65728 --- [nio-8080-exec-1] c.i.s.l.r.controller.DemoController      : [getUser][准备调用 user-service 获取用户(1)详情]
 *  * 2020-05-19 08:46:44.119  INFO 65728 --- [nio-8080-exec-1] c.i.s.l.r.controller.DemoController      : [getUserFallback][id(1) exception(ResourceAccessException)]
 *
 * ③ 疯狂使用浏览器，访问 http://127.0.0.1:8080/demo/get_user?id=1 地址，会触发 Hystrix 熔断器熔断（打开），不再执行#getUser(Integer id) 方法，而是直接 fallback 触发 #getUserFallback(Integer id, Throwable throwable) 方法。日志内容如下：
 *  * 2020-05-19 08:47:03.107  INFO 65728 --- [nio-8080-exec-7] c.i.s.l.r.controller.DemoController      : [getUserFallback][id(1) exception(CallNotPermittedException)]
 *  * 2020-05-19 08:47:03.204  INFO 65728 --- [nio-8080-exec-8] c.i.s.l.r.controller.DemoController      : [getUserFallback][id(1) exception(CallNotPermittedException)]
 *  * 2020-05-19 08:47:03.275  INFO 65728 --- [nio-8080-exec-9] c.i.s.l.r.controller.DemoController      : [getUserFallback][id(1) exception(CallNotPermittedException)]
 *
 *  ④ 重新执行 UserServiceApplication 启动用户服务。
 *  使用浏览器，多次访问 http://127.0.0.1:8080/demo/get_user?id=1 地址，Hystrix 熔断器的状态逐步从打开 => 半开 => 关闭。日志内容如下：
 *
 *  // 打开
 * 2020-05-19 08:47:39.408  INFO 65728 --- [nio-8080-exec-1] c.i.s.l.r.controller.DemoController      : [getUserFallback][id(1) exception(CallNotPermittedException)]
 * ...
 *
 * // 半开
 * 2020-05-19 08:48:23.095  INFO 65728 --- [nio-8080-exec-5] c.i.s.l.r.controller.DemoController      : [getUser][准备调用 user-service 获取用户(1)详情]
 *
 * // 关闭
 * 2020-05-19 08:48:32.488  INFO 65728 --- [nio-8080-exec-6] c.i.s.l.r.controller.DemoController      : [getUser][准备调用 user-service 获取用户(1)详情]
 * 2020-05-19 08:48:32.666  INFO 65728 --- [nio-8080-exec-7] c.i.s.l.r.controller.DemoController      : [getUser][准备调用 user-service 获取用户(1)详情]
 * 2020-05-19 08:48:33.230  INFO 65728 --- [nio-8080-exec-9] c.i.s.l.r.controller.DemoController      : [getUser][准备调用 user-service 获取用户(1)详情]
 * ...
 *
 */
@RestController
@RequestMapping("/demo")
public class DemoController {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    /**
     * ① 在 #getUser(Integer id) 方法中，我们使用 RestTemplate 调用用户服务提供的 /user/get 接口，获取用户详情。
     * ② 在 #getUser(Integer id) 方法上，添加了 Resilience4j 提供的 @CircuitBreaker 注解：
     *
     * 通过 name 属性，设置对应的 CircuitBreaker 熔断器实例名为 "backendA"，就是我们在「2.2.2 配置文件」中所添加的。
     * 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     * 通过不同的 Throwable 异常，我们可以进行不同的 fallback 降级处理。极端情况下，Resilience4j 熔断器打开时，不会执行 #getUser(Integer id) 方法，而是直接抛出 CallNotPermittedException 异常，然后也是进入 fallback 降级处理。
     *
     */
    @GetMapping("/get_user")
    @CircuitBreaker(name = "backendA", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}