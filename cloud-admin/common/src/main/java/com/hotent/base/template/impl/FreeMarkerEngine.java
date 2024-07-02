/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.template.impl;

import com.hotent.base.template.TemplateEngine;
import com.pharmcube.api.model.exception.BaseException;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import javax.annotation.Resource;
import java.io.StringWriter;

/**
 * FreemarkEngine解析引擎
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年6月4日
 */
//@Transactional
@Component
@ConfigurationProperties(prefix = "spring.freemarker")
public class FreeMarkerEngine implements InitializingBean, TemplateEngine{
	private Version version = Configuration.VERSION_2_3_28;
	private Configuration configuration;
	@Resource
	ResourceLoader resourceLoader;
	//@Value("${spring.freemarker.template-loader-path: classpath:/templates/ }")
	private String templateLoaderPath = "classpath:/templates/";
	//@Value("${spring.freemarker.charset}")
	private String charset = "UTF-8";
	// 当直接传入模板内容进行解析时，需要指定模板名称，这里使用该名称
	private String commonTemplateName = "common_template_freemark";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		configuration = new Configuration(version);
		TemplateLoader templateLoader = new SpringTemplateLoader(resourceLoader, templateLoaderPath);
		configuration.setTemplateLoader(templateLoader);
		configuration.setDefaultEncoding(charset);
	}

	@Override
	public String parseByTempName(String templateName, Object model) throws Exception {
		try {
			Template template = configuration.getTemplate(templateName);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		}
		catch(Exception ex) {
			throw new BaseException(String.format("模板解析异常，异常信息：%s", ExceptionUtils.getRootCauseMessage(ex)));
		}
	}

	@Override
	public String parseByTemplate(String template, Object model) throws Exception {
		try {
			// 避免污染全局配置，临时性的创建新的配置来装在模板
			Configuration cfg = new Configuration(version);
			StringTemplateLoader loader = new StringTemplateLoader();
			cfg.setDefaultEncoding(charset);
			cfg.setTemplateLoader(loader);
			cfg.setClassicCompatible(true);
			loader.putTemplate(commonTemplateName, template);
			Template templateObj = cfg.getTemplate(commonTemplateName);
			StringWriter writer = new StringWriter();
			templateObj.process(model, writer);
			// 用完移除该模板
			loader.removeTemplate(commonTemplateName);
			return writer.toString();
		}
		catch(Exception ex) {
			throw new BaseException(String.format("模板解析异常，异常信息：%s", ExceptionUtils.getRootCauseMessage(ex)));
		}
	}
	
	
	@Override
	public String parseByStringTemplate(String templateSource, Object model) throws Exception {
		try {
			Configuration cfg = new Configuration(version);
			StringTemplateLoader loader = new StringTemplateLoader();
			cfg.setTemplateLoader(loader);
			cfg.setClassicCompatible(true);
			loader.putTemplate("freemaker", templateSource);
			Template template = cfg.getTemplate("freemaker");
			StringWriter writer = new StringWriter();
			template.process(model, writer);
			return writer.toString();
		}
		catch(Exception ex) {
			throw new BaseException(String.format("模板解析异常，异常信息：%s", ExceptionUtils.getRootCauseMessage(ex)));
		}
	}
}
