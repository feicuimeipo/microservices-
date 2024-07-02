package com.nx.boot.config;


import com.nx.boot.handler.GlobalExceptionHandler;
import com.nx.boot.handler.GlobalResponseBodyHandler;
import com.nx.boot.i18n.I18nInterceptor;
import com.nx.boot.support.AppUtil;
import com.nx.boot.support.WebFrameworkUtils;
import com.nx.boot.tenant.TenantIgnoreAspect;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.spi.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;



/**
 * WEB配置
 */
@Slf4j
@Configuration
@Import(WebProperties.class)
public class NxWebAutoConfiguration implements WebMvcConfigurer {

	@Autowired
	private WebProperties webProperties;

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurePathMatch(configurer, webProperties.getAdminApi());
		configurePathMatch(configurer, webProperties.getAppApi());

		//BannerUtils.push(this.getClass(),String.format("管理端：url前缀名为:{}，包名板名：{}", webProperties.getAdminApi().getPrefix(),webProperties.getAdminApi().getController()));
		//BannerUtils.push(this.getClass(),String.format("应用端：url前缀名为:{}，包名板名：{}", webProperties.getAppApi().getPrefix(),webProperties.getAppApi().getController()));
	}


	@Bean
	public AppUtil appUtil(){
		return new AppUtil();
	}


	/**
	 * 设置 API 前缀，仅仅匹配 controller 包下的
	 *
	 * @param configurer 配置
	 * @param api API 配置
	 */
	private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
		AntPathMatcher antPathMatcher = new AntPathMatcher(".");
		configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
				&& antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅仅匹配 controller 包




	}

	@Bean
	GlobalExceptionHandler globalExceptionHandler(){
		return new GlobalExceptionHandler();
	}

	@Bean
	GlobalResponseBodyHandler globalResponseBodyHandler(){
		return new GlobalResponseBodyHandler();
	}

	@Bean
	@SuppressWarnings("InstantiationOfUtilityClass")
	public WebFrameworkUtils webFrameworkUtils(@Autowired WebProperties webProperties) {
		// 由于 WebFrameworkUtils 需要使用到 webProperties 属性，所以注册为一个 Bean
		return new WebFrameworkUtils(webProperties);
	}

	@Bean
	TenantIgnoreAspect tenantIgnoreAspect(){
		return new TenantIgnoreAspect();
	}

	//--国际化--
	@Bean
	I18nInterceptor i18nInterceptor(){
		return new I18nInterceptor();
	}


	/**
	 * MessageSource
	 */
	@Resource
	private MessageSource messageSource;

	/**
	 * Validation message i18n
	 * @return Validator
	 */
	@Bean
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(this.messageSource);
		return validator;
	}

	/**
	 * 解决加入jackson-dataformat-xml包，默认spring返回由json变为xml
	 * @param configurer
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML);
	}


}
