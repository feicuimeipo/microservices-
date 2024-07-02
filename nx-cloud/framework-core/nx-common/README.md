
# 包功能说明
- 微服务的最小集合
- 日志
- sp-api (auto-service-annotations)
- transmittable-thread-local
- snakeyaml



# skywaling

```aidl
./cloud-alibaba-gateway-filter/src/main/resources/logback.xml
./cloud-alibaba-server-provider/src/main/resources/logback.xml
./cloud-alibaba-server-consumer/src/main/resources/logback.xml
./cloud-alibaba-server-consumer-feign/src/main/resources/logback.xml

-------------------
    <property name="FILE_LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss} | %-5level | %thread | %tid | %logger{50} %L\ | %msg%n" />

-------------------
    <appender name="grpc-log" class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.log.GRPCLogClientAppender" >
        <!-- 对日志进行格式化 -->
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <pattern>${FILE_LOG_PATTERN}</pattern>
            </layout>
        </encoder>
    </appender>


```

```aidl
2021-11-23 14:44:56 | INFO  | http-nio-8604-exec-8 | TID:9db5a370aa5847569b036d44ce972999.707.16376498966050097 | c.l.d.s.a.s.c.f.controller.NacosConsumerController 26| /hello request data: HelloParam(name=gateway-feign-德玛西亚)
2021-11-23 14:44:56 | INFO  | http-nio-8604-exec-8 | TID:9db5a370aa5847569b036d44ce972999.707.16376498966050097 | c.l.d.s.a.s.c.f.controller.NacosConsumerController 28| response: Hello,gateway-feign-德玛西亚
```