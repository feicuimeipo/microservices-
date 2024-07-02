
# 内容
1. 概述
2. CircuitBreaker 熔断器
3. RateLimiter 限流器
4. Bulkhead 舱壁
5. Retry 重试
6. TimeLimiter 限时器
7. 执行顺序
8. 监控端点
9. 集成到 Feign(可集成)
10. 集成到 Dubbo(官方提无)


# 概述
> 文档: https://resilience4j.readme.io/docs/getting-started
> 源码：https://github.com/resilience4j/resilience4j

Resilience4j 是一个轻量级的容错组件，其灵感来自于 Hystrix，但主要为 Java 8 和函数式编程所设计。
轻量级体现在其只用 Vavr 库，没有任何外部依赖。而 Hystrix 依赖了 Archaius，Archaius 本身又依赖很多第三方包，例如 Guava、Apache Commons Configuration 等等。

要使用 Resilience4j，不需要引入所有依赖，只需要引入你所需的模块。

```aidl
    <!-- 引入 Resilience4j Starter 相关依赖，并实现对其的自动配置 -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot2</artifactId>
        <version>1.4.0</version>
    </dependency>

    <!-- 引入 Aspectj 依赖，支持 AOP 相关注解、表达式等等 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>1.9.5</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.5</version>
    </dependency>
```

- Core modules 核心模块
```
  - resilience4j-circuitbreaker: Circuit breaking 熔断器
  - resilience4j-ratelimiter: Rate limiting 限流
  - resilience4j-bulkhead: Bulkheading 舱壁隔离
  - resilience4j-retry: Automatic retrying (sync and async) 自动重试（同步和异步）
  - resilience4j-cache: Result caching 响应缓存
  - resilience4j-timelimiter: Timeout handling 超时处理
```

- Add-on modules
```
- resilience4j-retrofit: Retrofit adapter
- resilience4j-feign: Feign adapter
- resilience4j-consumer: Circular Buffer Event consumer
- resilience4j-kotlin: Kotlin coroutines support
```

- Frameworks modules
```
- resilience4j-spring-boot: Spring Boot Starter
- resilience4j-spring-boot2: Spring Boot 2 Starter
- resilience4j-ratpack: Ratpack Starter
- resilience4j-vertx: Vertx Future decorator
```

- Reactive modules
```
- resilience4j-rxjava2: Custom RxJava2 operators
- resilience4j-reactor: Custom Spring Reactor operators
```

- Metrics modules
```
- resilience4j-micrometer: Micrometer Metrics exporter
- resilience4j-metrics: Dropwizard Metrics exporter
- resilience4j-prometheus: Prometheus Metrics exporter
```

# CircuitBreaker 熔断器
> example: DemoController

CircuitBreaker 一共有 CLOSED、OPEN、HALF_OPEN 三种状态，通过状态机实现。转换关系如下图所示

```aidl
   <-> CLOSED <->  OPEN <-> HALF_OPEN <->    
```

- 当熔断器关闭(CLOSED)时，所有的请求都会通过熔断器。
- 如果失败率超过设定的阈值，熔断器就会从关闭状态转换到打开(OPEN)状态，这时所有的请求都会被拒绝。
- 当经过一段时间后，熔断器会从打开状态转换到半开(HALF_OPEN)状态，这时仅有一定数量的请求会被放入，并重新计算失败率。如果失败率超过阈值，则变为打开(OPEN)状态；如果失败率低于阈值，则变为关闭(CLOSE)状态。

```aidl
resilience4j:
  # Resilience4j 的熔断器配置项，对应 CircuitBreakerProperties 属性类
  circuitbreaker:
    ...
    instances:
      backendA:
        failure-rate-threshold: 50 # 熔断器关闭状态和半开状态使用的同一个失败率阈值，单位：百分比。默认为 50
        ring-buffer-size-in-closed-state: 5 # 熔断器关闭状态的缓冲区大小，不会限制线程的并发量，在熔断器发生状态转换前所有请求都会调用后端服务。默认为 100
        ring-buffer-size-in-half-open-state: 5 # 熔断器半开状态的缓冲区大小，会限制线程的并发量。例如，缓冲区为 10 则每次只会允许 10 个请求调用后端服务。默认为 10
        wait-duration-in-open-state : 5000 # 熔断器从打开状态转变为半开状态等待的时间，单位：微秒
        automatic-transition-from-open-to-half-open-enabled: true # 如果置为 true，当等待时间结束会自动由打开变为半开；若置为 false，则需要一个请求进入来触发熔断器状态转换。默认为 true
        register-health-indicator: true # 是否注册到健康监测
```

# RateLimiter 限流器
> 例子RateLimiterDemoController

RateLimiter 一共有两种实现类：
```aidl
- AtomicRateLimiter：基于令牌桶限流算法实现限流。
- SemaphoreBasedRateLimiter：基于 Semaphore 实现限流。
```

默认情况下，采用 AtomicRateLimiter 基于令牌桶限流算法实现限流。

```aidl
resilience4j:
  # Resilience4j 的限流器配置项，对应 RateLimiterProperties 属性类
  ratelimiter:
    instances:
      backendB:
        limit-for-period: 1 # 每个周期内，允许的请求数。默认为 50
        limit-refresh-period: 10s # 每个周期的时长，单位：微秒。默认为 500
        timeout-duration: 5s # 被限流时，阻塞等待的时长，单位：微秒。默认为 5s
        register-health-indicator: true # 是否注册到健康监测
```
- ① resilience4j.ratelimiter 是 Resilience4j 的 RateLimiter 配置项，对应 RateLimiterConfigurationProperties 属性类。
- ② 在 resilience4j.ratelimiter.instances 配置项下，可以添加限流器实例的配置，其中 key 为限流器实例的名字，value 为限流器实例的具体配置，对应 RateLimiterConfigurationProperties.InstanceProperties 类。

# Bulkhead 舱壁 
> 例子BulkheadDemoController

Bulkhead 指的是船舶中的舱壁，它将船体分隔为多个船舱，在船部分受损时可避免沉船。
在 Resilience4j 中，提供了基于 Semaphore 信号量和 ThreadPool 线程池两种 Bulkhead 实现，隔离不同种类的调用，并提供流控的能力，从而避免某类调用异常时而占用所有资源，导致影响整个系统。

- 信号量方式Bulkhead
```aidl
resilience4j:
  # Resilience4j 的信号量 Bulkhead 配置项，对应 BulkheadConfigurationProperties 属性类
  bulkhead:
    instances:
      backendC:
        max-concurrent-calls: 1 # 并发调用数。默认为 25
        max-wait-duration: 5s # 并发调用到达上限时，阻塞等待的时长，单位：微秒。默认为 0

```

- 信号量方式 Bulkhead 
```aidl
resilience4j:
  # Resilience4j 的线程池 Bulkhead 配置项，对应 ThreadPoolBulkheadProperties 属性类
  thread-pool-bulkhead:
    instances:
      backendD:
        max-thread-pool-size: 1 # 线程池的最大大小。默认为 Runtime.getRuntime().availableProcessors()
        core-thread-pool-size: 1 # 线程池的核心大小。默认为 Runtime.getRuntime().availableProcessors() - 1
        queue-capacity: 100 # 线程池的队列大小。默认为 100
        keep-alive-duration: 100s # 超过核心大小的线程，空闲存活时间。默认为 20 毫秒
```
- ① resilience4j.thread-pool-bulkhead 是 Resilience4j 的线程池 Bulkhead 配置项，对应 ThreadPoolBulkheadProperties 属性类。
- ② 在 resilience4j.thread-pool-bulkhead.instances 配置项下，可以添加 Bulkhead 实例的配置，其中 key 为 Bulkhead 实例的名字，value 为 Bulkhead 实例的具体配置，对应 ThreadPoolBulkheadProperties.InstanceProperties 类。

# Retry 重试
> example: RetryDemoController

Resilience4j 提供了 Retry 组件，在执行失败时，进行重试的行为。
```aidl
resilience4j:
  # Resilience4j 的重试 Retry 配置项，对应 RetryProperties 属性类
  retry:
    instances:
      backendE:
        max-retry-Attempts: 3 # 最大重试次数。默认为 3
        wait-duration: 5s # 下次重试的间隔，单位：微秒。默认为 500 毫秒
        retry-exceptions: # 需要重试的异常列表。默认为空
        ingore-exceptions: # 需要忽略的异常列表。默认为空
```
- ① resilience4j.retry 是 Resilience4j 的 Retry 配置项，对应 RetryProperties 属性类。
- ② 在 resilience4j.retry.instances 配置项下，可以添加 Retry 实例的配置，其中 key 为 Retry 实例的名字，value 为 Retry 实例的具体配置，对应 RetryProperties.InstanceProperties 类。

# TimeLimiter 限时器
> Example: TimeLimiterDemoController

Resilience4j 提供了 TimeLimiter 组件，限制任务的执行时长，在超过时抛出异常。


```aidl
resilience4j:
  # Resilience4j 的超时限制器 TimeLimiter 配置项，对应 TimeLimiterProperties 属性类
  timelimiter:
    instances:
      backendF:
        timeout-duration: 1s # 等待超时时间，单位：微秒。默认为 1 秒
        cancel-running-future: true # 当等待超时时，是否关闭取消线程。默认为 true
        
  # Resilience4j 的线程池 Bulkhead 配置项，对应 ThreadPoolBulkheadProperties 属性类
  thread-pool-bulkhead:
    instances:
      backendD:
        max-thread-pool-size: 1 # 线程池的最大大小。默认为 Runtime.getRuntime().availableProcessors()
        core-thread-pool-size: 1 # 线程池的核心大小。默认为 Runtime.getRuntime().availableProcessors() - 1
        queue-capacity: 200 # 线程池的队列大小。默认为 100
        keep-alive-duration: 100s # 超过核心大小的线程，空闲存活时间。默认为 20 毫秒
```
- ① resilience4j.timelimiter 是 Resilience4j 的 TimeLimiter 配置项，对应 TimeLimiterProperties 属性类。
- ② 在 resilience4j.timelimiter.instances 配置项下，可以添加 Retry 实例的配置，其中 key 为 TimeLimiter 实例的名字，value 为 TimeLimiter 实例的具体配置，对应 TimeLimiterConfigurationProperties.InstanceProperties 类。

# 组合使用
> Example:TimeLimiterDemoController

```aidl
@CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
@RateLimiter(name = BACKEND)
@Bulkhead(name = BACKEND)
@Retry(name = BACKEND, fallbackMethod = "fallback")
@TimeLimiter(name = BACKEND)
public String method(String param1) {
    throws new Exception("xxxx");
}

private String fallback(String param1, IllegalArgumentException e) {
    return "test:IllegalArgumentException";
}

private String fallback(String param1, RuntimeException e) {
    return "test:RuntimeException";
}

```
> 执行顺序: Retry > Bulkhead > RateLimiter > TimeLimiter > Bulkhead


# 监控端点
resilience4j-spring-boot2 基于 Spring Boot Actuator，提供了 Resilience4j 自定义监控端点：
- Metrics endpoint：纯 Endpoint 结尾。
- Health endpoint：HealthIndicator 结尾。
- Events endpoint：EventsEndpoint 结尾。

```aidl
management:
  endpoints:
    # Actuator HTTP 配置项，对应 WebEndpointProperties 配置类
    web:
      exposure:
        include: '*' # 需要开放的端点。默认值只打开 health 和 info 两个端点。通过设置 * ，可以开放所有端点。
  endpoint:
    # Health 端点配置项，对应 HealthProperties 配置类
    health:
      show-details: ALWAYS # 何时显示完整的健康信息。默认为 NEVER 都不展示。可选 WHEN_AUTHORIZED 当经过授权的用户；可选 ALWAYS 总是展示。
  # 健康检查配置项
  health:
    circuitbreakers.enabled: true
    ratelimiters.enabled: true
```

① 关于 Actuator 的配置项的作用，胖友看下艿艿添加的注释。如果还不理解的话，后续看下《芋道 Spring Boot 监控端点 Actuator 入门》文章。
② 默认情况下，Resilience4j 的 CircuitBreakersHealthIndicator 和 RateLimitersHealthIndicator 是关闭的，所以需要设置 management.health.circuitbreakers.enabled 和 management.health.ratelimiters.enabled 配置项为 true。

### Metrics endpoint
① 访问 http://127.0.0.1:8080/actuator/metrics 端点，可以看到 Resilience4j 提供的所有 Metrics 监控指标。
② 访问 http://127.0.0.1:8080/actuator/metrics/resilience4j.bulkhead.core.thread.pool.size 地址，查看resilience4j.bulkhead.core.thread.pool.size 监控指标。
```aidl
{
    "name": "resilience4j.bulkhead.core.thread.pool.size",
    "description": "The core thread pool size",
    "baseUnit": null,
    "measurements": [
        {
            "statistic": "VALUE",
            "value": 1
        }
    ],
    "availableTags": [
        {
            "tag": "name",
            "values": [
                "backendD"
            ]
        }
    ]
}
```
③ 一般情况下，我们可以通过 Prometheus 采集 Resilience4j 提供的 Metrics 监控数据，然后使用 Grafana 制作监控仪表盘。

### Health endpoint

访问 http://127.0.0.1:8080/actuator/health 端点，可以看到 CircuitBreaker 和 RateLimiter 的健康状态。结果如下：
```aidl
{
    "status": "UP",
    "components": {
        "circuitBreakers": {
            "status": "UP",
            "details": {
                "backendA": {
                    "status": "UP",
                    "details": {
                        ...s
                    }
                }
            }
        },
        "rateLimiters": {
            "status": "UP",
            "details": {
                "backendB": {
                    "status": "UP",
                    "details": {
                        "availablePermissions": 1,
                        "numberOfWaitingThreads": 0
                    }
                }
            }
        },
        // ... 省略其它
    }
}

```


### Events endpoint
① 访问 http://127.0.0.1:8080/actuator/ 接口，可以看到每个类型的 Resilience4j 组件，都有自己的 Events endpoint。
② 我们来测试下 CircuitBreaker 的 Event 事件。
首先，请求 http://127.0.0.1:8080/demo/get_user?id=1 接口，触发一次 fallback 降级，因为我们没有启动用户服务，所以调用显然会失败。
然后，请求 http://127.0.0.1:8080/actuator/circuitbreakerevents 地址，查看 CircuitBreaker 的 Event 事件。返回结果如下：
```aidl
{
    "circuitBreakerEvents": [
        {
            "circuitBreakerName": "backendA",
            "type": "ERROR",
            "creationTime": "2020-05-21T07:47:18.168+08:00[Asia/Shanghai]",
            "errorMessage": "org.springframework.web.client.ResourceAccessException: I/O error on GET request for \"http://127.0.0.1:18080/user/get\": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)",
            "durationInMs": 32,
            "stateTransition": null
        }
    ]
}
```

# 集成到集成到 Feign
resilience4j-feign 提供 Resilience4j 针对 Feign 的集成。

### Decorating Feign Interfaces
```aidl
public interface MyService {
    @RequestLine("GET /greeting")
    String getGreeting();
            
    @RequestLine("POST /greeting")
    String createGreeting();
}

//调用时
// For decorating a feign interface
CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
RateLimiter rateLimiter = RateLimiter.ofDefaults("backendName");
FeignDecorators decorators = FeignDecorators.builder()
                                 .withRateLimiter(rateLimiter)
                                 .withCircuitBreaker(circuitBreaker)
                                 .build();
MyService myService = Resilience4jFeign.builder(decorators).target(MyService.class, "http://localhost:8080/")
```

### Fallback
```aidl
public interface MyService {
    @RequestLine("GET /greeting")
    String greeting();
}

// For decorating a feign interface with fallback
MyService requestFailedFallback = () -> "fallback greeting";
MyService circuitBreakerFallback = () -> "CircuitBreaker is open!";
CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
FeignDecorators decorators = FeignDecorators.builder()
                                  .withFallback(requestFailedFallback, FeignException.class)
                                  .withFallback(circuitBreakerFallback, CircuitBreakerOpenException.class)
                                  .build();
MyService myService = Resilience4jFeign.builder(decorators).target(MyService.class, "http://localhost:8080/", fallback);
```
- MyFallback
```aidl
public interface MyService {
    @RequestLine("GET /greeting")
    String greeting();
}

public class MyFallback implements MyService {
    private Exception cause;

    public MyFallback(Exception cause) {
        this.cause = cause;
    }

    public String greeting() {
        if (cause instanceOf FeignException) {
            return "Feign Exception";
        } else {
            return "Other exception";
        }
    }
}

FeignDecorators decorators = FeignDecorators.builder()
                                 .withFallbackFactory(MyFallback::new)
                                 .build();
```

