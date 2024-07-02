//package com.pharmcube.gateway.controller;
//
//import com.alibaba.csp.sentinel.SphU;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import com.pharmcube.api.model.R;
//import io.netty.util.internal.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//import reactor.core.publisher.Mono;
//
//import javax.naming.ServiceUnavailableException;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * 服务发现控制器
// */
//@Slf4j
//@RestController
//@RequestMapping("/discovery")
//public class DiscoveryClientController {
//	private final DiscoveryClient discoveryClient;
//	private final RestTemplate restTemplate;
//
//	@Value("${spring.application.name:''}")
//	private String applicationName;
//
//	public DiscoveryClientController(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
//		this.discoveryClient = discoveryClient;
//		this.restTemplate = restTemplate;
//	}
//
//
//	/**
//	 * 获取服务实例
//	 */
//	@GetMapping("/instances")
//	public Map<String, List<ServiceInstance>> instances() {
//		Map<String, List<ServiceInstance>> instances = new HashMap<>(16);
//		List<String> services = discoveryClient.getServices();
//		services.forEach(s -> {
//			List<ServiceInstance> list = discoveryClient.getInstances(s);
//			instances.put(s, list);
//		});
//		return instances;
//	}
//
//
//	private Optional<URI> serviceUrl() {
//		if (discoveryClient!=null && StringUtil.isNullOrEmpty(applicationName)) {
//			return discoveryClient.getInstances(applicationName).stream().map(si->si.getUri()).findFirst();
//		}
//		return Optional.empty();
//	}
//
//
//
//	@GetMapping("/client")
//	public R<String> discoveryPing() throws RestClientException, ServiceUnavailableException {
//		URI service = serviceUrl().map(s -> s.resolve("/ping")).orElseThrow(ServiceUnavailableException::new);
//		return R.OK(restTemplate.getForEntity(service,String.class).getBody());
//	}
//
//	@GetMapping("/ping")
//	public R<String> ping() {
//		return R.OK("Client ping");
//	}
//
//
//	@GetMapping("/sentinel")
//	public Mono<R<String>> sentinel(){
//		try (com.alibaba.csp.sentinel.Entry entry = SphU.entry("HelloWorld")) {
//			// Your business logic here.
//			System.out.println("hello world:sentinel");
//			return Mono.just(R.OK("hello world:sentinel"));
//		} catch (BlockException e) {
//			// Handle rejected request.
//			log.error(e.getMessage(),e);
//			return Mono.just(R.FAIL(500,e.getMessage()));
//		}
//	}
//}
