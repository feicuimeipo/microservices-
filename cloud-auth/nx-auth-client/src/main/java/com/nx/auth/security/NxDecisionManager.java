/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security;

import com.nx.auth.context.PlatformConsts;
import com.nx.auth.support.AuthenticationUtil;
import com.nx.auth.api.utils.InternalBeanUtils;
import com.nx.api.context.CustomRequestHeaders;
import com.nx.api.context.GlobalConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 授权管理器
 */
public class NxDecisionManager implements AccessDecisionManager {


	/**
	 * Collection<ConfigAttribute> configAttributes: configAttributes是用户登录后，获取
	 * @param authentication
	 * @param object
	 * @param configAttributes
	 * @throws AccessDeniedException
	 * @throws InsufficientAuthenticationException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		try {
			if (InternalBeanUtils.isEmpty(configAttributes))
				return;
			HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
			String refer = request.getHeader(CustomRequestHeaders.HEADER_REFERER); //"Referer"
			for(GlobalConstants.REFERER r: GlobalConstants.REFERER.values()){
				if (r.name().equals(refer)){
					return;
				}
			}
			//如果是接口过来的，则由接口判断


			Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
			for (GrantedAuthority grantedAuthority : authorities) {
				//超级管理员
				if (PlatformConsts.ROLE_SUPER.equals(grantedAuthority.getAuthority())) {
					//todo: 限制超级管理员在某些域名可以登录
					return;
				}
			}

			for (ConfigAttribute configAttribute : configAttributes) {
				if(InternalBeanUtils.isEmpty(configAttribute)) continue;
				String configVal = configAttribute.toString();

				// 匿名资源允许访问
				if(PlatformConsts.PERMIT_All.equals(configVal)) {
					return;
				}else if(PlatformConsts.AUTHENTICATED.equals(configVal)) {
					// 受权限控制的资源
					// 匿名访问时抛出 401异常
					if(AuthenticationUtil.isAnonymous(authentication)) {
						throw new InsufficientAuthenticationException("需要提供jwt授权码");
					}else {
						// 如果该资源未加入权限管理列表，则允许访问
						String attribute = configAttribute.getAttribute();
						if(StringUtils.isEmpty(attribute)) {
							return;
						}
					}
				}
			}
			
			for (GrantedAuthority grantedAuthority : authorities) {
				for (ConfigAttribute configAttribute : configAttributes) {
					if (grantedAuthority.getAuthority().equals(
							configAttribute.getAttribute())) {
						// 有权限
						return;
					}
				}
			}

			throw new AccessDeniedException("您没有权限， 请联系系统管理员");
			
		}
		finally {
			// 完成权限校验后，清理线程变量中的权限数据。
			HtInvocationSecurityMetadataSourceService.clearMapThreadLocal();
		}
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}
}
