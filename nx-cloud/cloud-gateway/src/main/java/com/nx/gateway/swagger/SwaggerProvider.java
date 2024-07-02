package com.nx.gateway.swagger;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * swwagger 路由配置
 * </p>
 *
 * @author nianxiaoling
 * @version 1.0.0
 * @since 2020-03-06 9:34
 */
@Slf4j
@Primary
@Component
@AllArgsConstructor
public class SwaggerProvider implements SwaggerResourcesProvider {
	//public static final String SWAGGER2URL = "/v2/api-docs";
	//public static final String SWAGGER3URL = "/v3/api-docs";

	public static final String API_URI = "/v3/api-docs";
	/**
	 * swagger3默认的url后缀
	 *  要使用ui的话 改成v2 不然会出bug  比如有的地方 没有输入框
	 */
	private final RouteLocator routeLocator;
	private final GatewayProperties gatewayProperties;


	/**
	 * 网关应用名称
	 */
	@Value("${spring.application.name:''}")
	private String self;


	@Autowired
	public SwaggerProvider(GatewayProperties gatewayProperties,RouteLocator routeLocator) {
		this.gatewayProperties = gatewayProperties;
		this.routeLocator = routeLocator;
	}


	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<>();
		List<String> routeHosts = new ArrayList<>();
		// 获取所有可用的host：serviceId
		routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
				.filter(route -> !self.equals(route.getUri().getHost()))
				.subscribe(route -> routeHosts.add(route.getUri().getHost()));

		gatewayProperties.getRoutes().stream().filter(routeDefinition -> routeHosts.contains(routeDefinition.getId()));
//		FilterDefinition filterDefinition =
//				gatewayProperties.getDefaultFilters().stream()
//				.filter(definition ->("StripPrefix").equals(definition.getName())).findFirst().get();

		gatewayProperties.getRoutes().stream().filter(routeDefinition -> routeHosts.contains(routeDefinition.getId()));

		gatewayProperties.getRoutes().forEach(
				routeDefinition -> routeDefinition.getPredicates().stream()
						.filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
						.forEach(predicateDefinition -> resources.add(
										swaggerResource(
												routeDefinition.getId(),
												predicateDefinition.getArgs()
										)
								)//resources.add(
						)//.forEach(
		);//.forEach(routeDefinition

		return resources;
	}

	private SwaggerResource swaggerResource(String name, Map<String, String> predicateDefinitionArg) {
		String location = predicateDefinitionArg.get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**", API_URI);
		String url = location;
		log.info("swagger-ui-name:{},location:{}", name, url);
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(url);
		swaggerResource.setSwaggerVersion("3.0");
		return swaggerResource;
	}
}