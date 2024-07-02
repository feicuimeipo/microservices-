/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.nianxi.boot.annotation.IgnoreOnAssembly;



/**
 * @company 广州宏天软件股份有限公司
 * @author:liyg
 * @date:2018年6月7日
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@Configuration
@MapperScan(basePackages={"com.hotent.**.dao"})
@ComponentScan(basePackages={"com.hotent.*","org.activiti.engine.*"}, 
excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {IgnoreOnAssembly.class})})
public class Application {
	public static void main( String[] args )
	{
		SpringApplication.run(Application.class, args);
	}
}
