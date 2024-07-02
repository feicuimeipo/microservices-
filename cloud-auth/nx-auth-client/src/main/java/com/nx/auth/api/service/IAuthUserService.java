/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service;

import com.nx.auth.api.dto.UserDetailDTO;
import com.nx.auth.context.CacheKeyConst;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.auth.context.AuthCache;
import com.nx.cache.annotation.*;

/**
 * 接口 {@code IUserService} 用户服务
 *
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月5日
 */
public interface IAuthUserService {
	
	/**
	 * 根据用户ID获取用户的对象
	 * @return 用户的对象
	 */
	UserDetailDTO getUserDetail();

	@CacheEvict(cacheName = AuthCache.AUTH_CACHE_NAME, keyPrefix = "user:UserFacade", key = "#username")
	default void removeUserFromCache(String username) {}

	@CacheEvict(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = CacheKeyConst.USER_NAME_CACHENAME, key = "#userId")
	default void removeUserNameFromCache(String userId) {}

	@CachePut(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = "user:UserFacade", key = "#username")
	default UserFacade putUserFacadeInCache(String username,UserFacade user){return user;}

	@CachePut(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = CacheKeyConst.USER_NAME_CACHENAME, key = "#userId")
	default String putUserNameInCache(String userId, String username){ return username;}


	@Cacheable(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = "user:UserFacade", key = "#username")
	default UserFacade getUserFacade(String username){return ContextUtil.getCurrentUser();}


	@Cacheable(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = CacheKeyConst.USER_NAME_CACHENAME, key = "#userId")
	default String getUserNameFromCache(String userId){ return ContextUtil.getCurrentUser().getUsername();}


}
