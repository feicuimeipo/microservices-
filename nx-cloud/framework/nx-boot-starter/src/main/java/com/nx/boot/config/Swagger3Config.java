/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: nianxiMicro
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.boot.config;


import com.google.common.collect.ImmutableList;
import com.nx.boot.annotation.ApiGroup;
import com.nx.boot.filter.CorsFilter;
import com.nx.common.banner.BannerUtils;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Swagger配置
 *
 * 	 	
 * @author nianxiaoling
 * @email xlnian@163.com
 * @date 2018年4月19日
 */
@Slf4j
@Configuration
@EnableOpenApi
@ConfigurationProperties(prefix = "springfox.documentation")
@Data
@ConditionalOnProperty(prefix = "springfox.documentation",value = "enabled",havingValue = "true")
public class Swagger3Config extends DocumentationPluginsManager implements ApplicationContextAware {
	@Value("${spring.profiles.active:'dev'}")
	private String acceptsProfiles;
	private String applicationName="api";
	private String description="文档api";
	private String applicationVersion="1.0.0";
	private Map<String,String> groups=Collections.singletonMap("default","默认分组");

	private String tryHost="localhost：8080";


	@Autowired(required = false)
	private ISwagger3Group swagger3Group;

	public Swagger3Config(){
		BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter：Swagger3 enabled"});
	}

	@Override
	public Collection<DocumentationPlugin> documentationPlugins() throws IllegalStateException {
		List<DocumentationPlugin> plugins = registry().getPlugins();
		ensureNoDuplicateGroups(plugins);
		return plugins.isEmpty() ? Collections.singleton(this.defaultDocumentationPlugin()) : plugins;
	}

	private void ensureNoDuplicateGroups(List<DocumentationPlugin> allPlugins) throws IllegalStateException {
		Map<String, List<DocumentationPlugin>> plugins = allPlugins.stream().collect(Collectors.groupingBy((input) -> {
			return Optional.ofNullable(input.getGroupName()).orElse("default");
		}, LinkedHashMap::new, Collectors.toList()));
		Iterable<String> duplicateGroups = plugins.entrySet().stream().filter((input) -> {
			return (input.getValue()).size() > 1;
		}).map(Map.Entry::getKey).collect(Collectors.toList());
		if (StreamSupport.stream(duplicateGroups.spliterator(), false).count() > 0L) {
			throw new IllegalStateException(String.format("Multiple Dockets with the same group name are not supported. The following duplicate groups were discovered. %s", String.join(",", duplicateGroups)));
		}
	}

	private DocumentationPlugin defaultDocumentationPlugin() {
		return new Docket(DocumentationType.OAS_30);
	}

	public SwaggerPluginRegistry registry(){
		if (swagger3Group!=null && swagger3Group.getGroups()!=null){
			swagger3Group.getGroups().keySet().stream().forEach(key->{
				if (!groups.containsKey(key)){
					groups.put(key, swagger3Group.getGroups().get(key));
				}
			});
		}
		if (groups==null){
			groups= Collections.singletonMap("default","默认分组");
		}
		if (!groups.containsKey(ApiGroup.DEFAULT)){
			groups.put(ApiGroup.DEFAULT,"默认分组");
		}

		//log.info("=============文档分组:"+groups+"=============");

		List<Docket> list =new ArrayList();
		for (String key : groups.keySet()) {
			String desc = groups.get(key);
			String groupId = key;
			if (groupId.indexOf(".")>-1) {
				groupId = groupId.substring(groupId.indexOf(".")+1);
			}
			Docket docket = getDocket(groupId,desc);
			list.add(docket);
			//beanFactory.registerSingleton(name,docket);
		}
		return new SwaggerPluginRegistry(list, new AnnotationAwareOrderComparator());
	}



	/**
	 * 设置分组信息
	 * @return docket
	 */
	private Docket getDocket(final String groupId,String desc) {

		// 设置显示 swagger的环境
		List<String> notAllowProfiles = Arrays.asList(new String[]{"prod"});
		boolean flag = !Arrays.asList(acceptsProfiles.split(",")).containsAll(notAllowProfiles);
		ApiSelectorBuilder apiSelectorBuilder =
				new Docket(DocumentationType.OAS_30)
						.produces(Collections.singleton("application/json"))
						.consumes(Collections.singleton("application/json"))
				.apiInfo(apiInfo(desc, description,applicationVersion,tryHost))
				.groupName(groupId)
				.useDefaultResponseMessages(false)
				.forCodeGeneration(false)
				.tags(new Tag(groupId,desc))
				.select()
				.paths(PathSelectors.any())
				.apis(input -> {
						if (input.groupName()!=null && input.groupName().equalsIgnoreCase(groupId)){
							//log.info("groupName="+input.groupName());
							return true;
						}
						if (input.findControllerAnnotation(ApiGroup.class).isPresent()) {
							boolean isContainer = input.findControllerAnnotation(ApiGroup.class).filter(item -> Arrays.asList(item.group()).contains(groupId)).isPresent();
							//ApiGroup apiGroup = input.findControllerAnnotation(ApiGroup.class).get();
							//log.debug("++++++++++item.groupId="+ apiGroup.group().toString() +"+++++groupId=" + groupId+",isContainer="+isContainer);
							return isContainer;
						}else if (input.findAnnotation(ApiGroup.class).isPresent()){
							boolean isContainer = input.findAnnotation(ApiGroup.class).filter(item -> Arrays.asList(item.group()).contains(groupId)).isPresent();
							return isContainer;
						}else{
							return input.findControllerAnnotation(Api.class).isPresent();
						}
					}
				);
		return apiSelectorBuilder
				.build()
				.ignoredParameterTypes(HttpServletResponse.class, HttpServletRequest.class)
				.enable(flag)
				//使Knife4j的增强配置生效
				//.extensions(openApiExtensionResolver.buildSettingExtensions())
				.protocols(Stream.of("https", "http").collect(Collectors.toSet()))
				.securityContexts(securityContext())
				.securitySchemes(securitySchemes())
				;
	}



	private List<SecurityContext> securityContext() {
		return ImmutableList.of(
				SecurityContext.builder()
				.securityReferences(defaultAuth())
						//.operationSelector(o -> !o.findAnnotation(PassToken.class).isPresent())
				.build()
		);
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("BearerToken", authorizationScopes));
	}


	private List<SecurityScheme> securitySchemes() {
		//return Collections.singletonList(new ApiKey("BearerToken", "Authorization", "header"));
		return Collections.singletonList(new ApiKey("BearerToken", "Authorization", In.HEADER.toValue()));
	}



	@SuppressWarnings("rawtypes")
	private ApiInfo apiInfo(String title, String description,String version,String tryHost) {
		Contact contact = new Contact("nianxiaoling", "www.nianxi.com", "xlnian@163.com");
		ApiInfo apiInfo = new ApiInfo(title,
				description,
				version,
				tryHost,
				contact,
				"license",
				"license url",
				new ArrayList<VendorExtension>());
		return apiInfo;
	}


	ApplicationContext cxt;
	ConfigurableListableBeanFactory beanFactory;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		cxt = applicationContext;
	}


	class SwaggerPluginRegistry extends OrderAwarePluginRegistry<DocumentationPlugin, DocumentationType> implements PluginRegistry<DocumentationPlugin, DocumentationType> {

		protected SwaggerPluginRegistry(List<Docket> plugins, Comparator<? super DocumentationPlugin> comparator) {
			super(plugins, comparator);
		}

		@Override
		public List<DocumentationPlugin> getPlugins() {
			return super.getPlugins();
		}
	}

	public interface ISwagger3Group {
		public Map<String,String> getGroups();
	}


}