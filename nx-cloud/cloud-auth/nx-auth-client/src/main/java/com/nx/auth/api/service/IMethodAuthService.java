/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service;

import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.auth.context.AuthCache;
import com.nx.auth.context.CacheKeyConst;
import com.nx.cache.annotation.CachePut;
import com.nx.cache.annotation.Cacheable;

import java.util.List;

public interface IMethodAuthService {
	
	/**
	 * 返回请求资源跟角色的映射关系 
	 * @return
	 */
	public List<SysRoleAuthDTO> getMethodAuth();


	@Cacheable(cacheName = AuthCache.AUTH_CACHE_NAME, keyPrefix = CacheKeyConst.METHOD_AUTH_CACHENAME, key = CacheKeyConst.SYS_METHOD_ROLE_AUTH, pureKey = true,getAndSet = true)
	public List<SysRoleAuthDTO> getMethodAuthFromCache();

	@Cacheable(cacheName = AuthCache.AUTH_CACHE_NAME, keyPrefix = CacheKeyConst.DATA_PERMISSION_CACHENAME, key="#roleAlias#methodRequestUrl",getAndSet = true)
	String getDataPermissionInCache(String roleAlias,String methodRequestUrl);

	@CachePut(cacheName = AuthCache.AUTH_CACHE_NAME, keyPrefix = CacheKeyConst.DATA_PERMISSION, key="#roleAlias#methodRequestUrl")
	String putDataPermissionInCache(String roleAlias, String methodRequestUrl, String data);
}
