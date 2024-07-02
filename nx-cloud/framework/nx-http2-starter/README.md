- 配置
```aidl
server:
  port: 8443
  ssl:
    key-store: classpath:keystore.jks
    key-store-password: xxx
    key-password: xxx
    protocol: TLSv1.2
  http2:
    enabled: true
  use-forward-headers: true
```
- 工具
```
keytool -genkey -keyalg RSA -alias selfsigned -keystore src/main/resources/keystore.jks -storepass xxx -validity 360 -keysize 2048
```

- 代码
```
  @Configuration
public class Http2Config {

    @Bean
    UndertowServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(
                builder -> {
                    builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                            .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH,true);
                });

        return factory;
    }
}
  ```

# curl
```aidl
curl -I --http2 https://localhost:8443/hello
curl: (60) SSL certificate problem: self signed certificate
More details here: http://curl.haxx.se/docs/sslcerts.html
```