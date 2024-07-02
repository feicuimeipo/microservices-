package com.nx.prometheus.config.httpsd;

//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * prometheus http sd
 *
 * @author L.cm
 */
@RestController
@RequestMapping("actuator/prometheus")
public class PrometheusHttpSdApi {
	private final String activeProfile;
	private final DiscoveryClient discoveryClient;
	private final ApplicationEventPublisher eventPublisher;

	public PrometheusHttpSdApi(String profile, DiscoveryClient discoveryClient, ApplicationEventPublisher eventPublisher) {
		this.activeProfile = profile;
		this.discoveryClient = discoveryClient;
		this.eventPublisher = eventPublisher;
	}


	@GetMapping("sd")
	public List<TargetGroup> getList() {
		List<String> serviceIdList = discoveryClient.getServices();
		if (serviceIdList == null || serviceIdList.isEmpty()) {
			return Collections.emptyList();
		}
		List<TargetGroup> targetGroupList = new ArrayList<>();
		for (String serviceId : serviceIdList) {
			List<ServiceInstance> instanceList = discoveryClient.getInstances(serviceId);
			List<String> targets = new ArrayList<>();
			for (ServiceInstance instance : instanceList) {
				targets.add(String.format("%s:%d", instance.getHost(), instance.getPort()));
			}
			Map<String, String> labels = new HashMap<>(4);
			// 1. 环境
			if (StringUtils.hasText(activeProfile)) {
				labels.put("profile", activeProfile);
			}
			// 2. 服务名
			labels.put("__meta_prometheus_job", serviceId);
			targetGroupList.add(new TargetGroup(targets, labels));
		}
		return targetGroupList;
	}

}
