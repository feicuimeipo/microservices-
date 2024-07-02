
package com.nx.prometheus.config.httpsd;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("actuator/prometheus")
public class ReactivePrometheusHttpSdApi {
	private String activeProfile;
	private ReactiveDiscoveryClient discoveryClient;
	private ApplicationEventPublisher eventPublisher;

	public ReactivePrometheusHttpSdApi(String profile, ReactiveDiscoveryClient discoveryClient, ApplicationEventPublisher eventPublisher) {
		this.activeProfile = profile;
		this.discoveryClient = discoveryClient;
		this.eventPublisher = eventPublisher;
	}


	@GetMapping("sd")
	public Flux<TargetGroup> getList() {
		return discoveryClient.getServices()
			.flatMap(discoveryClient::getInstances)
			.groupBy(ServiceInstance::getServiceId, instance ->
				String.format("%s:%d", instance.getHost(), instance.getPort())
			).flatMap(instanceGrouped -> {
				Map<String, String> labels = new HashMap<>(4);
				// 1. 环境
				if (StringUtils.hasText(activeProfile)) {
					labels.put("profile", activeProfile);
				}
				// 2. 服务名
				String serviceId = instanceGrouped.key();
				labels.put("__meta_prometheus_job", serviceId);
				return instanceGrouped.collect(Collectors.toList()).map(targets -> new TargetGroup(targets, labels));
			});
	}

}
