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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 类 {@code CurrentUserAccountVar} 当前用户账号
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
@Component
@ConditionalOnMissingBean(CurrentUserAccountVar.class)
public class CurrentUserAccountVar implements IContextVar {

	@Override
	public String getTitle() {
		return "当前用户账号";
	}

	@Override
	public String getAlias() {
		return "curUserAccount";
	}

	@Override
	public String getValue() {
		return ContextUtil.getCurrentUser().getAccount();
	}

}
