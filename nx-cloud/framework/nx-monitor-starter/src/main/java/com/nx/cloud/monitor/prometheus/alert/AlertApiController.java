package com.nx.cloud.monitor.prometheus.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * prometheus http sd
 *
 * @author L.cm
 */
@RestController
@RequestMapping("actuator/prometheus")
@RequiredArgsConstructor
public class AlertApiController {
	private final String activeProfile;
	private final DiscoveryClient discoveryClient;

	private final ApplicationEventPublisher eventPublisher;



	@PostMapping("alerts")
	public ResponseEntity<Object> postAlerts(@RequestBody AlertMessage message) {
		eventPublisher.publishEvent(message);
		return ResponseEntity.ok().build();
	}



}
