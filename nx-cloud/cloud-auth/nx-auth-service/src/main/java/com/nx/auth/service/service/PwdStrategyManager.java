/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service;

import com.nx.auth.service.model.entity.PwdStrategy;
import com.nx.auth.context.AuthCache;
import com.nx.cache.annotation.Cacheable;
import com.nx.mybatis.support.manager.BaseManager;


public interface PwdStrategyManager extends BaseManager<PwdStrategy> {
	@Cacheable(cacheName = AuthCache.AUTH_CACHE_NAME, key = "pwdStrategy")
	PwdStrategy getDefault();
}
