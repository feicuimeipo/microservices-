/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service;

/**
 * 接口 {@code IContextVar} 常用变量
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
public interface IContextVar {
	
	/**
	 * 获取命名
	 * @return 命名
	 */
	String getTitle();
	/**
	 * 获取别名
	 * @return 别名
	 */
	String getAlias();
	
	/**
	 * 获取值
	 * @return 值
	 */
	String getValue();

}
