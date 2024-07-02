/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 服务启动成功的监听器
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2019年6月27日
 */
@Component
public class ApplicationReadyEventListener implements  ApplicationListener<ApplicationReadyEvent>{
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("***********************");
		System.out.println(String.format("********%s********", "应用已启动"));
		System.out.println("***********************");
	}
}
