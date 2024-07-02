

# 调用链
完美的请求的链路含有：
```
前端http调用 -> 后端接受(当前为Java) -> 后端接受处理 -> 发送MQ || 开启异步线程 || 调用RPC接口(dubbo为例)
```

# traceId

通过实现traceId功能,获取当前操作链路的以下日志信息:

1. 当前操作http请求伴有traceId的日志信息
2. 当前操作java服务端伴有traceId的日志信息
3. 当前操作java服务端伴有traceId的dubbo consumer日志信息
4. 当前操作java服务端伴有traceId的dubbo provider日志信息
5. 其他可以串起来的服务日志信息，如MQ、异步线程等......
> 只要能将执行链路上每个链路节点信息的通过其可传递性将当前操作的traceId串起来,那么问题就解决了.

# 后端方案

1. 前端http请求的入参、响应状态、响应时间等信息
2. 调用dubbo provider服务的入参、ip、响应参数、耗时等信息
3. 接受dubbo consumer服务的入参、ip、响应参数等信息
4. 异步线程的有效信息等

> 当前选用springboot+spring-cloud+alibaba-cloud+dubbo作为框架模板，日志输出使用logback.
> 核心思想利用slf4j的MDC进行服务端日志串联操作.




