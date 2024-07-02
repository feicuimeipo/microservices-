/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security.impl;


import com.nx.auth.api.service.IAuthUserService;
import com.nx.auth.context.AuthCache;
import com.nx.auth.context.CacheKeyConst;
import com.nx.auth.context.UserFacade;
import com.nx.cache.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * 类 {@code UserCacheImpl} 用户详情的缓存器
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
@Service
@Primary
public class UserCacheImpl implements UserCache {
	@Autowired
	IAuthUserService userService;
	/**
	 * 根据用户账号删除缓存用户信息
	 * @param username 用户账号
	 */
	@Override
	@CacheEvict(cacheName = AuthCache.AUTH_CACHE_NAME,keyPrefix = CacheKeyConst.USER_NAME_CACHENAME, key = "#username")
	public void removeUserFromCache(String username) {
		UserFacade userFacade = userService.getUserFacade(username);
		userService.removeUserFromCache(userFacade.getUsername());
		userService.removeUserNameFromCache(userFacade.getUserId());
	}

    /**
     * 根据用户账号获取缓存的用户详情
     * @param username 用户账号
     * @return 用户详情
     */
	@Override
	public UserDetails getUserFromCache(String username) {
		return userService.getUserFacade(username);
	}

    /**
     * 根据用户信息把用户信息添加到缓存里面
     * @param user 用户信息
     */
	@Override
	public void putUserInCache(UserDetails user) {
		String username = user.getUsername();
		if(user instanceof UserFacade) {
			UserFacade userFacade = (UserFacade)user;
			String id = userFacade.getId();
			userService.putUserNameInCache(id, username);
			userService.putUserFacadeInCache(userFacade.getUsername(),userFacade);
		}
	}



}
