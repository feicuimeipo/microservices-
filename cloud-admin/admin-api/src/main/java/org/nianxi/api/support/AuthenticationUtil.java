/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotent.ucapi.utils.InternalBeanUtils;
import com.hotent.ucapi.utils.InternalJsonUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * 用于获取用户信息
 * @author liyanggui
 *
 */
public class AuthenticationUtil {
	
	/**
	 * 当前用户的主组织
	 */
	public final static String CURRENT_USER_MAIN_ORGID = "CURRENT_USER_MAIN_ORGID";
	/**
	 * 当前用户的组织idS  1,2,3
	 */
	public final static String CURRENT_USER_ORGIDS = "CURRENT_USER_ORGIDS";
	
	/**
	 * 当前用户的组织idS  1,2,3 以及下级组织id
	 */
	public final static String CURRENT_USER_SUB_ORGIDS = "CURRENT_USER_SUB_ORGIDS";
	
	// 用户信息的线程变量
	private static  ThreadLocal<JsonNode> userThreadLocal = new ThreadLocal<JsonNode>();
	
	// security认证对象的线程变量 Authentication
	private static ThreadLocal<Authentication> authenticationThreadLocal = new ThreadLocal<Authentication>();
	
	// 数据过滤的线程变量
	private static ThreadLocal<String[]> msIdsThreadLocal = new ThreadLocal<String[]>();
	private static ThreadLocal<Map<String,Object>> mapThreadLocal = new ThreadLocal<Map<String,Object>>();
	
	public static void setMapThreadLocal(Map<String,Object> map){
		mapThreadLocal.set(map);
	}
	public static Map<String,Object> getMapThreadLocal(){
		Map<String,Object> resultMap = mapThreadLocal.get();
		if(InternalBeanUtils.isEmpty(resultMap)){
			resultMap = new HashMap<String, Object>();
		}
		return resultMap;
	}
	
	public static void removeMapThreadLocal(){
		mapThreadLocal.remove();
	}
	
	public static void setMsIdsThreadLocal( String[] msIds ){
		msIdsThreadLocal.set(msIds);
	}
	
	public static String[] getMsIdsThreadLocal(){
		return msIdsThreadLocal.get();
	}
	
	public static void removeMsIdsThreadLocal(){
		msIdsThreadLocal.remove();
	}
	
	public static JsonNode getUserThreadLocal() {
		JsonNode jsonNode = userThreadLocal.get();
		if(InternalBeanUtils.isEmpty(jsonNode)) {
			return InternalJsonUtil.getMapper().createObjectNode();
		}
		return jsonNode;
	}
	
	public static void setAuthentication( Authentication authentication ){
		authenticationThreadLocal.set(authentication);
		
		Object principal = authentication.getPrincipal();
		try {
			if(principal instanceof UserDetails) {
				UserDetails ud = (UserDetails)principal;
				String json = InternalJsonUtil.toJson(ud);
				JsonNode jsonNode = InternalJsonUtil.toJsonNode(json);
				userThreadLocal.set(jsonNode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		
	}
	public static String getCurrentUserId(){
		JsonNode jsonNode = getUserThreadLocal().get("userId");
		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
		return jsonNode.asText();
	}
	
	/**
	 * 获取当前用户的名称
	 * @return
	 */
	public static String getCurrentUserFullname(){
		JsonNode jsonNode = getUserThreadLocal().get("fullname");
		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
		return jsonNode.asText();
	}
	
	/**
	 * 获取当前用户的账号信息
	 * @return
	 */
	public static String getCurrentUsername(){
		JsonNode jsonNode = getUserThreadLocal().get("account");
		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
		return jsonNode.asText();
	}
	
	/**
	 * 获取当前用户的主组织
	 * @return
	 */
	public static String getCurrentUserMainOrgId(){
		JsonNode jsonNode = getUserThreadLocal().get("attributes");
		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_MAIN_ORGID)){
			return jsonNode.get(CURRENT_USER_MAIN_ORGID).asText();
		}
		return null;
	}
	
	/**
	 * 获取当前用户所有组织 1,2,3
	 * @return
	 */
	public static String getCurrentUserOrgIds(){
		JsonNode jsonNode = getUserThreadLocal().get("attributes");
		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_ORGIDS)){
			return jsonNode.get(CURRENT_USER_ORGIDS).asText();
		}
		return null;
	}
	
	/**
	 * 获取当前用户所有组织 1,2,3以及下级组织
	 * @return
	 */
	public static String getCurrentUserSubOrgIds(){
		JsonNode jsonNode = getUserThreadLocal().get("attributes");
		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_SUB_ORGIDS)){
			return jsonNode.get(CURRENT_USER_SUB_ORGIDS).asText();
		}
		return null;
	}
	
	/**
	 * 获取当前用户具有的角色别名
	 * @return
	 */
	public static Set<String> getCurrentUserRolesAlias(){
		Authentication authentication = authenticationThreadLocal.get();
		Set<String> set = new HashSet<String>();
		if(InternalBeanUtils.isEmpty(authentication)) {
			return set;
		}
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		for (SimpleGrantedAuthority simpleGrantedAuthority : authorities) {
			set.add(simpleGrantedAuthority.getAuthority());
		}
		return set;
	}
	
	/**
	 * 
	 */
	public static void removeAll(){
		userThreadLocal.remove();
		authenticationThreadLocal.remove();
	}
	
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
}
