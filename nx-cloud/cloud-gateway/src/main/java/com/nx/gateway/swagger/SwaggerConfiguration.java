package com.nx.gateway.swagger;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 路由配置信息
 */

@Slf4j
@Configuration
@AllArgsConstructor
public class SwaggerConfiguration implements WebFluxConfigurer {

	/**
	 * 解决springboot2.0.5版本出现的 Only one connection receive subscriber allowed.
	 * 参考：https://github.com/spring-cloud/spring-cloud-gateway/issues/541
	 */
	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter() {
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
				return chain.filter(exchange);
			}
		};
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry){
		// 解决静态资源无法访问
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/static/");
		// 解决swagger无法访问
		registry.addResourceHandler("/swagger-ui/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
				.resourceChain(false);
		// 解决swagger的js文件无法访问
	registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");

	}


}
