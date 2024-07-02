/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.support;

import com.hotent.auth.security.CustomPwdEncoder;
import com.hotent.ucapi.context.BootConstant;
import com.hotent.ucapi.model.IUser;
import com.pharmcube.api.conf.SpringAppUtils;
import com.pharmcube.api.context.AuthUser;
import org.apache.commons.lang3.StringUtils;
import org.nianxi.api.model.exception.BaseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * spring security 工具类
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月6日
 */
public class SecurityUtil {
	private static SessionRegistry sessionRegistry;
	
	
	/**
	 * 登录系统。
	 * @param request
	 * @param userName		用户名
	 * @param pwd			密码
	 * @param isIgnorePwd	是否忽略密码。
	 * @return
	 */
	public static Authentication login(HttpServletRequest request, String userName, String pwd, boolean isIgnorePwd){
		AuthenticationManager authenticationManager = SpringAppUtils.getBean(AuthenticationManager.class);
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, pwd);
		authRequest.setDetails(new WebAuthenticationDetails(request));
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if(isIgnorePwd) {
			PasswordEncoder passwordEncoder = SpringAppUtils.getBean(PasswordEncoder.class);
			if(passwordEncoder instanceof CustomPwdEncoder) {
				CustomPwdEncoder customPwdEncoder = (CustomPwdEncoder)passwordEncoder;
				customPwdEncoder.setIngore(true);
			}
			else {
				throw new BaseException("PasswordEncoder can not support ignorePwd login.");
			}
		}
		Authentication authentication = authenticationManager.authenticate(authRequest);
		securityContext.setAuthentication(authentication);

		return authentication;
	}


	private static void setAuthUser(IUser iUser){
		AuthUser authUser = new AuthUser();
		authUser.setId(iUser.getUserId());
		authUser.setTenantId(iUser.getTenantId());
		authUser.setEnabled(iUser.isEnabled());
		authUser.setFrom(BootConstant.FROM_RESTFUL);
		authUser.setStatus(iUser.getStatus());
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
