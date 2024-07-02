/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.context;

import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.api.dto.OrgDTO;
import com.nx.auth.api.dto.TenantManageDTO;
import com.nx.auth.api.utils.InternalBeanUtils;
import com.nx.auth.support.AuthenticationUtil;
import com.nx.api.context.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;


public class ContextUtil  {
//
//	/**
//	 * 当前用户的主组织
//	 */
//	public final static String CURRENT_USER_MAIN_ORGID = "CURRENT_USER_MAIN_ORGID";
//	/**
//	 * 当前用户的组织idS  1,2,3
//	 */
//	public final static String CURRENT_USER_ORGIDS = "CURRENT_USER_ORGIDS";
//
//	/**
//	 * 当前用户的组织idS  1,2,3 以及下级组织id
//	 */
//	public final static String CURRENT_USER_SUB_ORGIDS = "CURRENT_USER_SUB_ORGIDS";



	public static boolean authenticationEmpty() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication==null;
	}


	/**
	 * 获取当前执行人
	 * @return 用户详情
	 */
	public static UserFacade getCurrentUser(){
		return AuthenticationUtil.getCurrentUser();
	}

	public static TenantManageDTO getTenantManageDTO(){
		return getCurrentUser().getTenantManageDTO();
	}


    /**
     * 设置当前用户ID
     */
	public static String getCurrentUserId(){
		UserFacade user = getCurrentUser();
		return user.getId();
	}

	/**
     * 获取当前用户组
     * @return 用户组
     */
	public static OrgDTO getCurrentGroup(){
		OrgDTO orgObj = getCurrentUser().getMainOrg();
		if(orgObj!=null){
			boolean isParent = Boolean.valueOf(orgObj.getParentId()).booleanValue();
			orgObj.setIsParent(isParent?1:0);
			orgObj.setParentId(null);
			return orgObj;
		}
		return null;
	}

	/**
     * 获取当前用户组Id，用户组为空则返回空
     * @return 用户组Id
     */
	public static String getCurrentGroupId(){
		OrgDTO org =  getCurrentGroup();
		if(org!=null){
			return org.getId();
		}else{
			return "";
		}
	}


	/**
	 * 设置当前执行人
	 * @param account 用户信息
	 */
//	public static void setCurrentUser(String account){
//
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if (authentication!=null) {
//
//			Assert.isTrue(InternalBeanUtils.isNotEmpty(account), "传入的用户帐号不能为空");
//
//			UserFacade user = new UserFacade();
//			UserDetailDTO userDTO = getUserDetailByAccount(account);
//			if (user!=null) {
//				BeanUtils.copyProperties(user,user);
//
//				UsernamePasswordAuthenticationToken usernamePwdAuth = new UsernamePasswordAuthenticationToken(user, null);
//				SecurityContextHolder.getContext().setAuthentication(usernamePwdAuth);
//
//			}
//		}
//	}


	/**
	 * 获取当前用户的名称
	 * @return
	 */
	public static String getCurrentUserFullname(){
		return getCurrentUser().getFullname();
//		JsonNode jsonNode = getUserThreadLocal().get("fullname");
//		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
//		return jsonNode.asText();
	}

	/**
	 * 获取当前用户的账号信息
	 * @return
	 */
	public static String getCurrentUsername(){
		return getCurrentUser().getAccount();
//		JsonNode jsonNode = getUserThreadLocal().get("account");
//		if(InternalBeanUtils.isEmpty(jsonNode) || !jsonNode.isTextual()) return null;
//		return jsonNode.asText();
	}

	/**
	 * 获取当前用户的主组织
	 * @return
	 */
	public static OrgDTO getCurrentUserMainOrgId(){
		return getCurrentUser().getMainOrg();
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_MAIN_ORGID)){
//			return jsonNode.get(CURRENT_USER_MAIN_ORGID).asText();
//		}
//		return null;
	}

	/**
	 * 获取当前用户所有组织 1,2,3
	 * @return
	 */
	public static OrgDTO[] getCurrentUserOrgIds(){
		return getCurrentUser().getOrgs();
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_ORGIDS)){
//			return jsonNode.get(CURRENT_USER_ORGIDS).asText();
//		}
//		return null;
	}

	/**
	 * 获取当前用户所有组织 1,2,3以及下级组织
	 * @return
	 */
	public static OrgDTO[] getCurrentUserSubOrgIds(){
		return getCurrentUser().getSubOrgs();
//		JsonNode jsonNode = getUserThreadLocal().get("attributes");
//		if(InternalBeanUtils.isNotEmpty(jsonNode) && jsonNode.has(CURRENT_USER_SUB_ORGIDS)){
//			return jsonNode.get(CURRENT_USER_SUB_ORGIDS).asText();
//		}
//		return null;
	}

	static String contextTemplateIdName = "tempTenantId";

	public static String getCurrentTenantId(){

		String tenantId = ContextUtil.Request.getTenantIdFromRequestInfo();

		if(InternalBeanUtils.isNotEmpty(tenantId)) {
			return tenantId;
		}

		String tempTenantId = CurrentRuntimeContext.getContextHeader(contextTemplateIdName); //

		if (StringUtils.isNotEmpty(tempTenantId)) {
			return tempTenantId;
		}

		if(ContextUtil.authenticationEmpty()) {
			return BootConstant.PLATFORM_TENANT_ID;
		}

		UserFacade currentUser = ContextUtil.getCurrentUser();

		return currentUser.getTenantId();
	}

	/**
	 * 根据用户账户获取用户信息
	 * @return 用户详情
	 */
//	public static UserDetailDTO getUserDetailByAccount(){
//		Assert.isTrue(StringUtils.isNotEmpty(account), "必须传入用户账号");
//		IUserService userServiceImpl= SpringAppUtils.getBean(IUserService.class);
//		UserDetailDTO user = userServiceImpl.getUserByAccount(account);
//		Assert.isTrue(InternalBeanUtils.isNotEmpty(user), String.format("账号为：%s的用户不存在", account));
//		return user;
//	}

//    /**
//     * 设置当前用户账号
//     * @param account 用户账号
//     */
//	public static void setCurrentUserByAccount(String account){
//
//		UserFacade userFacade = new UserFacade();
//		setCurrentUser(getUserByAccount(account));
//	}


	public static class Request extends SpringAppUtils.Request{

		/**
		 * <pre>
		 * 访问进入controller层时 设置enterController属性
		 * 如果有tenantId 参数则从参数中获取
		 *
		 * 没有进入controller层时 从jwttoken中获取用户认证的tenantId
		 * </pre>
		 * @return
		 */
		public static String getTenantIdFromRequestInfo() {
			HttpServletRequest request = SpringAppUtils.Request.getContextRequest();
			if(request==null) {
				return null;
			}
			Boolean enterController = (Boolean) request.getAttribute("enterController");
			String tenantId = null;
			// 只有request属性中有enterController信息时才进行判断，即只有请求Controller方法（且必须有@ApiOperation注解）时判断。
			if(enterController!=null &&  enterController.booleanValue()) {
				// 1.获取url地址后面是否有tenantId参数（临时租户ID，在某些情况下，只在访问某个接口时以指定租户身份来访问）
				// TODO 只有平台管理用户才能使用，防止租户之间水平越权
				tenantId = SpringAppUtils.Request.getRequestParameter("tenantId");
				if(StringUtils.isNotEmpty(tenantId)) {
					return tenantId;
				}
				// 2.获取url地址后面是否有token参数（携带token进行单点登录时）
				// TODO 目前只处理了jwt为token的情况，对于cas、oauth2.0的token还需要处理
				String token = SpringAppUtils.Request.getRequestParameter("ticket");
				tenantId = getTenantIdFromJwt(token);
				if(StringUtils.isNotEmpty(tenantId)) {
					return tenantId;
				}
			}
			// 3.获取请求头部的Authorization，从中解析出租户ID
			tenantId = getTenantIdByAuthorization();
			if(StringUtils.isNotEmpty(tenantId)) {
				return tenantId;
			}
			return tenantId;
		}

		/**
		 * 从jwt中获取当前登录用户的tenantId
		 * @return
		 */
		private static String getTenantIdByAuthorization() {
			HttpServletRequest request = SpringAppUtils.Request.getContextRequest();
			if(request==null) {
				return null;
			}
			String requestHeader = request.getHeader(CustomRequestHeaders.HEADER_JWT_AUTHORITIES);
			if(StringUtils.isNoneBlank(requestHeader) &&  requestHeader.startsWith(GlobalConstants.HEADER_TOKEN_PREFIX)) {
				String authToken = requestHeader.substring(7);
				return getTenantIdFromJwt(authToken);

			}
			return null;
		}

		/**
		 * 从jwt中解析tenantId
		 * @return
		 */
		private static String getTenantIdFromJwt(String jwt) {
			if(StringUtils.isEmpty(jwt)) {
				return null;
			}
			JwtTokenHandler jwtTokenHandler = SpringAppUtils.getBean(JwtTokenHandler.class);
			String tenantId = jwtTokenHandler.getTenantIdFromToken(jwt);
			if(StringUtils.isNotEmpty(tenantId)) {
				return tenantId;
			}
			return null;
		}


		// \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
		// 字符串在编译时会被转码一次,所以是 "\\b"
		// \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
		private static final String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
				+"|windows (phone|ce)|blackberry"
				+"|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
				+"|laystation portable)|nokia|fennec|htc[-_]"
				+"|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

		private static final String tabletReg = "\\b(ipad|tablet|(Nexus 7)|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

		//移动设备正则匹配：手机端、平板
		private static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
		private static Pattern tabletPat = Pattern.compile(tabletReg, Pattern.CASE_INSENSITIVE);
		/**
		 * 检测是否是移动设备访问
		 *
		 * @param request 浏览器标识
		 * @return true:移动设备接入，false:pc端接入
		 */
		public static boolean isMobile(HttpServletRequest request){

			String userAgent = request.getHeader("user-agent");
			if(null == userAgent){
				userAgent = "";
			}
			return phonePat.matcher(userAgent).find() || tabletPat.matcher(userAgent).find();
		}

	}




}
