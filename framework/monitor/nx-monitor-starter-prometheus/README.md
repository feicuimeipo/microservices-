# prometheus
- `Spring cloud` 对接 Prometheus `http_sd`和alert webhook，支持 `servlet` 和 `webflux`，建议集成到类非业务服务中。
## http-sd 使用
1.工程中引入依赖 
2. 在prometheus-exporter中引入依赖
```yaml
- job_name: micax-cloud
  honor_timestamps: true
  scrape_interval: 15s
  scrape_timeout: 10s
  metrics_path: /actuator/prometheus
  scheme: http
  http_sd_configs:
  - url: 'http://{ip}:{port}/actuator/prometheus/sd'
```

# alert使用（用法试一试）
1. 引入依赖
2. alert web hook
```aidl
receivers:
- name: "alerts"
  webhook_configs:
  - url: 'http://{ip}:{port}/actuator/prometheus/alerts'
    send_resolved: true
```
3. 程序中
- 自定义监听事件并处理
```aidl
@EventListener
public void onAlertEvent(AlertMessage message) {
	// 处理 alert webhook message
}
```

