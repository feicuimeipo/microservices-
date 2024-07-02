/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent>{
	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		System.err.println("------------------------");
		System.err.println(String.format("--------%s--------", "应用启动失败"));
		System.err.println("------------------------");
	}
}
