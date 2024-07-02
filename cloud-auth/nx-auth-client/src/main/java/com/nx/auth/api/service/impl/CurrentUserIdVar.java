/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service.impl;

import com.nx.auth.api.service.IContextVar;
import com.nx.auth.context.ContextUtil;
import org.springframework.stereotype.Component;

/**
 * 接口 {@code CurrentUserIdVar} 当前用户ID
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
@Component
public class CurrentUserIdVar implements IContextVar {

	@Override
	public String getTitle() {
		return "当前用户ID";
	}

	@Override
	public String getAlias() {
		return "curUserId";
	}

	@Override
	public String getValue() {
		return ContextUtil.getCurrentUserId();
	}

}
