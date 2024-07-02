package com.nx.gateway.swagger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.Optional;

/**
 * <p>
 * 因为Gateway里没有配置SwaggerConfig，而运行Swagger-ui又需要依赖一些接口， 建立相应的swagger-resource端点。
 * </p>
 *
 * @author nianxiaoling
 * @version 1.0.0
 * @since 2020-03-06 9:39
 */
@RestController
@RequestMapping(value = "/swagger-resources" ,
		produces = {MediaType.ALL_VALUE},
		consumes = "application/json;charset=utf-8")
public class SwaggerHandler {

	@Autowired(required = false)
	private SecurityConfiguration securityConfiguration;

	@Autowired(required = false)
	private UiConfiguration uiConfiguration;

	private final SwaggerProvider swaggerResources;

	@Autowired
	public SwaggerHandler(SwaggerProvider swaggerResources) {
		this.swaggerResources = swaggerResources;
	}

	@GetMapping("/configuration/security")
	public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
		return Mono.just(new ResponseEntity<>(
				Optional.ofNullable(securityConfiguration)
				.orElse(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK));
	}

	@GetMapping("/configuration/ui")
	public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
		return Mono.just(new ResponseEntity<>(
				Optional.ofNullable(uiConfiguration)
				.orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
	}

	@GetMapping(value = "")
	public Mono<ResponseEntity> swaggerResources() {
		return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
	}

}
