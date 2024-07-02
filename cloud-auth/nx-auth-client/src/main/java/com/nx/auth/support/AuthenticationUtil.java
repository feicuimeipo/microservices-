/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.support;

import com.nx.auth.context.UserFacade;
import com.nx.auth.api.utils.InternalBeanUtils;
import com.nx.auth.api.utils.InternalJsonUtil;
import com.nx.api.context.CurrentRuntimeContext;
import com.nx.api.context.SpringAppUtils;
import com.nx.api.exception.UnauthorizedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用于获取用户信息
 * @author liyanggui
 *
 */
public class AuthenticationUtil {


//	// 用户信息的线程变量
//	private static  ThreadLocal<JsonNode> userThreadLocal = new ThreadLocal<JsonNode>();
//
//	// security认证对象的线程变量 Authentication
//	private static ThreadLocal<Authentication> authenticationThreadLocal = new ThreadLocal<Authentication>();
//
//	// 数据过滤的线程变量
//	private static ThreadLocal<String[]> msIdsThreadLocal = new ThreadLocal<String[]>();
//	private static ThreadLocal<Map<String,Object>> mapThreadLocal = new ThreadLocal<Map<String,Object>>();
//
//	public static void setMapThreadLocal(Map<String,Object> map){
//		mapThreadLocal.set(map);
//	}
//	public static Map<String,Object> getMapThreadLocal(){
//		Map<String,Object> resultMap = mapThreadLocal.get();
//		if(InternalBeanUtils.isEmpty(resultMap)){
//			resultMap = new HashMap<String, Object>();
//		}
//		return resultMap;
//	}
//
//	public static void removeMapThreadLocal(){
//		mapThreadLocal.remove();
//	}
//
//	public static void setMsIdsThreadLocal( String[] msIds ){
//		msIdsThreadLocal.set(msIds);
//	}
//
//	public static String[] getMsIdsThreadLocal(){
//		return msIdsThreadLocal.get();
//	}
//
//	public static void removeMsIdsThreadLocal(){
//		msIdsThreadLocal.remove();
//	}
	
//	public static JsonNode getUserThreadLocal() {
//		JsonNode jsonNode = userThreadLocal.get();
//		if(InternalBeanUtils.isEmpty(jsonNode)) {
//			return InternalJsonUtil.getMapper().createObjectNode();
//		}
//		return jsonNode;
//	}
	
	public static void setAuthentication( Authentication authentication ){
		Object principal = authentication.getPrincipal();
		try {
			if(principal instanceof UserFacade) {
				//UserFacade ud = (UserFacade)principal;
				//String json = InternalJsonUtil.toJson(ud);
				//JsonNode jsonNode = InternalJsonUtil.toJsonNode(json);
				//userThreadLocal.set(jsonNode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static UserFacade getCurrentUser(){
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Assert.notNull(authentication, "当前登录用户不能为空");
			Object principal = authentication.getPrincipal();
			if(principal instanceof UserFacade) {
				return (UserFacade)principal;
			}else if(principal instanceof UserDetails) {
				UserFacade ud = (UserFacade)principal;
				UserFacade user = InternalJsonUtil.toBean(InternalJsonUtil.toJson(ud), UserFacade.class);
				return user;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}


//	public static String getCurrentUserId(){
//		JsonNode jsonNode = getUserThreadLocal().get("userId");
//		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
//		return jsonNode.asText();
//	}
	
//	/**
//	 * 获取当前用户的名称
//	 * @return
//	 */
//	public static String getCurrentUserFullname(){
//		JsonNode jsonNode = getUserThreadLocal().get("fullname");
//		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
//		return jsonNode.asText();
//	}
//
//	/**
//	 * 获取当前用户的账号信息
//	 * @return
//	 */
//	public static String getCurrentUsername(){
//		JsonNode jsonNode = getUserThreadLocal().get("account");
//		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
//		return jsonNode.asText();
//	}
//
//	/**
//	 * 获取当前用户的主组织
//	 * @return
//	 */
//	public static String getCurrentUserMainOrgId(){
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_MAIN_ORGID)){
//			return jsonNode.get(CURRENT_USER_MAIN_ORGID).asText();
//		}
//		return null;
//	}
//
//	/**
//	 * 获取当前用户所有组织 1,2,3
//	 * @return
//	 */
//	public static String getCurrentUserOrgIds(){
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_ORGIDS)){
//			return jsonNode.get(CURRENT_USER_ORGIDS).asText();
//		}
//		return null;
//	}
//
//	/**
//	 * 获取当前用户所有组织 1,2,3以及下级组织
//	 * @return
//	 */
//	public static String getCurrentUserSubOrgIds(){
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_SUB_ORGIDS)){
//			return jsonNode.get(CURRENT_USER_SUB_ORGIDS).asText();
//		}
//		return null;
//	}
	
	/**
	 * 获取当前用户具有的角色别名
	 * @return
	 */
//	public static Set<String> getCurrentUserRolesAlias(){
//		Authentication authentication = authenticationThreadLocal.get();
//		Set<String> set = new HashSet<String>();
//		if(InternalBeanUtils.isEmpty(authentication)) {
//			return set;
//		}
//		@SuppressWarnings("unchecked")
//		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
//		for (SimpleGrantedAuthority simpleGrantedAuthority : authorities) {
//			set.add(simpleGrantedAuthority.getAuthority());
//		}
//		return set;
//	}

	public static Set<String> getCurrentUserRolesAlias(){
		Set<String> set = new HashSet<String>();
//		Authentication authentication = authenticationThreadLocal.get();
//		Set<String> set = new HashSet<String>();
//		if(InternalBeanUtils.isEmpty(authentication)) {
//			return set;
//		}
		@SuppressWarnings("unchecked")

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		for (SimpleGrantedAuthority simpleGrantedAuthority : authorities) {
			set.add(simpleGrantedAuthority.getAuthority());
		}
		return set;
	}
	
	/**
	 * 
	 */
//	public static void removeAll(){
//		userThreadLocal.remove();
//		authenticationThreadLocal.remove();
//	}
	
	/**
	 * 判断当前是否为匿名请求
	 * @param authentication
	 * @return
	 */
	public static boolean isAnonymous(Authentication authentication) {
		if(InternalBeanUtils.isEmpty(authentication)) return true;
		if(authentication instanceof AnonymousAuthenticationToken) return true;
		return false;
	}


	private static SessionRegistry sessionRegistry;


	public static List<String> getDenyUri(){
		return Arrays.asList(new String[]{});
	}
	public static List<String> getAllowUri(){
		return  Arrays.asList(new String[]{
				"/",
				"/error",
				"/*.html",
				"/img/**",
				"/images/**",
				"/favicon.ico",
				"*.html",
				"*.css",
				"*.js",
				"*.png",
				"*.gif",
				"*.svg",
				"*.jpeg",
				"/*/api-docs",
				"/swagger-*/**",
				"/swagger-ui.html",
				"/swagger-ui",
				"/druid/**",
				"/auth/**",
				"/sso/**",
				"/ueditor/**",
				"/actuator/**",
				"/druid/**",
				"/swagger/**",
				"/*/api-docs/**"
		});
	}

	/**
	 * 登录系统。
	 * @param request
	 * @param userName		用户名
	 * @param pwd			密码
	 * @param isIgnorePwd	是否忽略密码。
	 * @return
	 */
	public static Authentication login(HttpServletRequest request, String userName, String pwd, boolean isIgnorePwd){
		try {
			//UserDetailsService userDetailsService = SpringAppUtils.getBean(UserDetailsService.class);
			//CurrentRuntimeContext.addContextHeader("ingorePwd", String.valueOf(isIgnorePwd));

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, isIgnorePwd ? null : pwd);
			//authRequest.setDetails(new WebAuthenticationDetails(request));
			authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			UserCache userCache = SpringAppUtils.getBean(UserCache.class);
			userCache.removeUserFromCache(userName);

			AuthenticationManager authenticationManager = SpringAppUtils.getBean(AuthenticationManager.class);
			Authentication authentication = authenticationManager.authenticate(authRequest);
			if (authentication != null) {
				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(authentication);
			}
			return authentication;
		}catch (Exception e){
			throw new UnauthorizedException(e);
		}finally {
			CurrentRuntimeContext.removeContextHeader("ingorePwd");
		}
	}



	/**
	 * 踢出用户
	 * @param account 账号
	 */
	public static void kickoutUser(String account){
		if(StringUtils.isEmpty(account)) return;
		if(sessionRegistry==null){
			sessionRegistry = SpringAppUtils.getBean(SessionRegistry.class);
		}
		List<Object> objects = sessionRegistry.getAllPrincipals();
		for (Object o : objects) {
			User user = (User) o;
			if (account.equals(user.getUsername())) {
				List<SessionInformation> sis = sessionRegistry.getAllSessions(o, false);
				if (sis != null) {
					for (SessionInformation si : sis) {
						si.expireNow();
					}
				}
			}
		}
	}
}
