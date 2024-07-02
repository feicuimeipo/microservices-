

TODO:
```aidl
`1. 缓存组件的调试
 2. pageBean改为pageinfo之后，去掉rowbound依赖，程序是否还在转动。
 3. 默认的返回值json，在航天神州智慧config里找代码
 4. mybatis-将每个数据源置都好好测度一把
 5. 设置CurrentRuntimeContext的当前值
 6. actionLog对像增加skywalking的跟踪的上下文id
 
 admin
 6. admin中的ContextUtil
 7. SysLogsUtil
 8. 切面日志写入测试
```


# 计划中的组件
- 微信 - 李昊
- mongodb - 吴迪 
### 组件优化
-- commonUtils中的组件优化

# 立旧组件
- 

# 新研组件
### 已完成
-- 

### 进行中
--


# bootstrap

```aidl
org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties
org.springframework.web.cors.CorsEndpointProperties
```

```aidl
<dependency>
    <groupId>com.pharmcube.cloud</groupId>
    <artifactId>pharmcube-monitor-compatibility</artifactId>
    <version>0.9.9</version>
</dependency>
```