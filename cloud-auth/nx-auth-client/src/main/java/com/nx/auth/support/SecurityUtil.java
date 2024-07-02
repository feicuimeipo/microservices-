/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.support;

/**
 * spring security 工具类
 *
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月6日
 */
//public class SecurityUtil {
//	private static SessionRegistry sessionRegistry;
//
//
//	/**
//	 * 登录系统。
//	 * @param request
//	 * @param userName		用户名
//	 * @param pwd			密码
//	 * @param isIgnorePwd	是否忽略密码。
//	 * @return
//	 */
//	public static Authentication login(HttpServletRequest request, String userName, String pwd, boolean isIgnorePwd){
//		AuthenticationManager authenticationManager = SpringAppUtils.getBean(AuthenticationManager.class);
//		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, pwd);
//		authRequest.setDetails(new WebAuthenticationDetails(request));
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		if(isIgnorePwd) {
//			PasswordEncoder passwordEncoder = SpringAppUtils.getBean(PasswordEncoder.class);
//			if(passwordEncoder instanceof CustomPwdEncoder) {
//				CustomPwdEncoder customPwdEncoder = (CustomPwdEncoder)passwordEncoder;
//				customPwdEncoder.setIngore(true);
//			}
//			else {
//				throw new BaseException("PasswordEncoder can not support ignorePwd login.");
//			}
//		}
//		Authentication authentication = authenticationManager.authenticate(authRequest);
//		securityContext.setAuthentication(authentication);
//
//		return authentication;
//	}
//
//
//
//
//	/**
//	 * 踢出用户
//	 * @param account 账号
//	 */
//	public static void kickoutUser(String account){
//		if(StringUtils.isEmpty(account)) return;
//		if(sessionRegistry==null){
//			sessionRegistry = SpringAppUtils.getBean(SessionRegistry.class);
//		}
//		List<Object> objects = sessionRegistry.getAllPrincipals();
//        for (Object o : objects) {
//        	User user = (User) o;
//            if (account.equals(user.getUsername())) {
//                List<SessionInformation> sis = sessionRegistry.getAllSessions(o, false);
//                if (sis != null) {
//                    for (SessionInformation si : sis) {
//                        si.expireNow();
//                    }
//                }
//            }
//        }
//	}
//
//
//}
