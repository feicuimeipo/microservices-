package com.hotent.base.conf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 原文件：AopCacheHelper
 * WEB配置
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Order(Ordered.HIGHEST_PRECEDENCE)

public class WebMvcConfiguration implements WebMvcConfigurer {


	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//argumentResolvers.add(new TokenArgumentResolver());
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 设置 SerializationFeature.FAIL_ON_EMPTY_BEANS 为 false
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 忽略未知属性
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormat));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormat));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormat));

		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormat));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormat));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormat));
		om.registerModule(javaTimeModule);

		return om;
	}


}
