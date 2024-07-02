/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.context;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class PlatformConsts {
	
	public final static String ROLE_SUPER  = "ROLE_SUPER";//超级
	public final static String ROLE_PUBLIC  = "ROLE_PUBLIC";//公共角色
	public final static String ROLE_ANONYMOUS  = "ROLE_ANONYMOUS";//匿名级
	public final static String ROLE_NONE  = "ROLE_NONE";//没有任何角色
	public final static String PERMIT_All  = "permitAll";// 允许访问
	public final static String AUTHENTICATED = "authenticated";// 需要授权
	
	public final static SimpleGrantedAuthority ROLE_GRANT_SUPER=new SimpleGrantedAuthority(ROLE_SUPER);
	public final static ConfigAttribute  ROLE_CONFIG_PUBLIC=new SecurityConfig(ROLE_PUBLIC);
	public final static ConfigAttribute  ROLE_CONFIG_ANONYMOUS=new SecurityConfig(ROLE_ANONYMOUS);
	public final static ConfigAttribute  PERMIT_All_CONFIG=new SecurityConfig(PERMIT_All);
	public final static ConfigAttribute  ROLE_CONFIG_NONE=new SecurityConfig(ROLE_NONE);

}
