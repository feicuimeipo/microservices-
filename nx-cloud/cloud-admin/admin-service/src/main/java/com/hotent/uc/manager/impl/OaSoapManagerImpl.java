/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.hotent.uc.manager.OaSoapManager;
import com.hotent.uc.params.org.SoapConfig;

@Configuration
public class OaSoapManagerImpl implements OaSoapManager {
	@Value("${soap.url:''}")
	private String url;
	@Value("${soap.name:''}")
	private String name;
	@Value("${soap.password:''}")
	private String password;
	@Value("${soap.demCode:''}")
	private String demCode;
	@Value("${soap.jobCode:''}")
	private String jobCode;
	
	@Override
	public SoapConfig getSoapConfig() {
		SoapConfig config = new SoapConfig();
		config.setUrl(url);
		config.setName(name);
		config.setPassword(password);
		config.setDemCode(demCode);
		config.setJobCode(jobCode);
		return config;
	}

}
