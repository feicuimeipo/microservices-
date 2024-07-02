
package com.nx.prometheus.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("actuator/prometheus")
@RequiredArgsConstructor
@ConditionalOnClass(ServerProperties.Reactive.class)
public class ReactivePrometheusAlertApi {
	private final String activeProfile;
	private final ReactiveDiscoveryClient discoveryClient;
	private final ApplicationEventPublisher eventPublisher;


	@PostMapping("alerts")
	public ResponseEntity<Object> postAlerts(@RequestBody AlertMessage message) {
		eventPublisher.publishEvent(message);
		return ResponseEntity.ok().build();
	}

}
