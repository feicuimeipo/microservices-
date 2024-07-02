/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security.impl;

import com.nx.auth.api.AuthServiceApi;
import com.nx.auth.api.dto.UserFacadeDTO;
import com.nx.auth.api.dto.UserRoleDTO;
import com.nx.auth.api.service.IAuthUserService;
import com.nx.auth.context.PlatformConsts;
import com.nx.auth.context.UserFacade;
import com.nx.api.context.SpringAppUtils;
import com.nx.api.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * UCRestUserDetailsServiceImpl.java
 * 类 {@code UCCachingUserDetailsService} 实现缓存的用户详情服务
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService{
	private final UserCache userCache;

	@Autowired
	public UserDetailsServiceImpl(UserCache userCache){
		this.userCache = userCache;
	}

	/**
	 * 根据用户账号获取用户详情
	 * 获取用户详情
	 * @param username 用户账号
	 * @return 用户详情
	 * @throws UsernameNotFoundException
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserCacheImpl userCache = SpringAppUtils.getBean(UserCacheImpl.class);

		UserFacade userFacade = (UserFacade) userCache.getUserFromCache(username);

		if (userFacade == null) {
			try {
				AuthServiceApi serviceApi = SpringAppUtils.getBean(AuthServiceApi.class);
				R<UserFacadeDTO> ret = serviceApi.loadUserByUsername(username);
				if (ret.isOK() && ret.getData()!=null){
					 UserFacadeDTO dto = ret.getData();
					 userFacade = new UserFacade();

					BeanUtils.copyProperties(userFacade,userFacade);

					Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
					if (dto.isAdmin()){
						authorities.add(PlatformConsts.ROLE_GRANT_SUPER);
					}
					for (UserRoleDTO userRole : dto.getUserRoles()) {
						SimpleGrantedAuthority role=new SimpleGrantedAuthority(userRole.getAlias());
						authorities.add(role);
					}

					userFacade.setAuthorities(authorities);


					userCache.putUserInCache(userFacade);

				}else{
					throw new UsernameNotFoundException(ret.getMsg());
				}
				//UserFacade user = InternalJsonUtil.toBean(result, UserFacade.class);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new UsernameNotFoundException(e.getMessage(),e);
			}

		}

		Assert.notNull(userFacade,"从 UCFeignService returned null for username " + username + ". " + "This is an interface contract violation");

		return userFacade;
	}
}
