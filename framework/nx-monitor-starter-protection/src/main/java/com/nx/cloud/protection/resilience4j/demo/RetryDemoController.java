package com.nx.cloud.protection.resilience4j.demo;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 启动 Resilience4j 示例项目。
 * 调用 http://127.0.0.1:8080/retry-demo/get_user?id=1 接口，可以通过日志看出，一共执行 + 重试共 3 次，每次间隔 5 秒：
 *
 * // 执行 + 重试共 3 次 输出结果
 * 2020-05-20 20:07:34.693  INFO 89536 --- [nio-8080-exec-1] c.i.s.l.r.c.RetryDemoController          : [getUser][准备调用 user-service 获取用户(1)详情]
 * 2020-05-20 20:07:39.722  INFO 89536 --- [nio-8080-exec-1] c.i.s.l.r.c.RetryDemoController          : [getUser][准备调用 user-service 获取用户(1)详情]
 * 2020-05-20 20:07:44.727  INFO 89536 --- [nio-8080-exec-1] c.i.s.l.r.c.RetryDemoController          : [getUser][准备调用 user-service 获取用户(1)详情]
 *
 * // 最终 fallback
 * 2020-05-20 20:07:44.729  INFO 89536 --- [nio-8080-exec-1] c.i.s.l.r.c.RetryDemoController          : [getUserFallback][id(1) exception(ResourceAccessException)]
 *
 */
@RestController
@RequestMapping("/retry-demo")
public class RetryDemoController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    /**
     * ① 在 #getUser(Integer id) 方法中，我们使用 RestTemplate 调用用户服务提供的 /user/get 接口，获取用户详情。     *
     * ② 在 #getUser(Integer id) 方法上，添加了 Resilience4j 提供的 @Retry 注解：     *
     * 通过 name 属性，设置对应的 Retry 实例名为 "backendE"，就是我们在「5.1 配置文件」中所添加的。
     * 通过 fallbackMethod 属性，设置执行发生 Exception 异常时，执行对应的 #getUserFallback(Integer id, Throwable throwable) 方法。注意，fallbackMethod 方法的参数要和原始方法一致，最后一个为 Throwable 异常。
     * 在多次重试失败到达上限时，Resilience4j 会抛出 MaxRetriesExceeded 异常，然后就进入 fallback 降级处理。
     *
     */
    @GetMapping("/get_user")
    @Retry(name = "backendE", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}