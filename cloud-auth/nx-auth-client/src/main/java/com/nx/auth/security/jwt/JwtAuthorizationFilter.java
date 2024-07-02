/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security.jwt;

import com.nx.api.context.CurrentRuntimeContext;
import com.nx.api.context.CustomRequestHeaders;
import com.nx.api.context.GlobalConstants;
import com.nx.auth.api.utils.InternalJsonUtil;
import com.nx.auth.conf.AuthConfig;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.auth.model.CommonResult;
import com.nx.auth.security.NxAuthenticationManager;
import com.nx.auth.support.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;



public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final UserDetailsService userDetailsService;
	private final JwtTokenHandler jwtTokenHandler;
	private final AuthConfig authConfig;

	public JwtAuthorizationFilter(JwtTokenHandler jwtTokenHandler, AuthConfig authConfig,UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenHandler = jwtTokenHandler;
		this.authConfig = authConfig;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		//如果是feign接口，不走该过滤芯器
		String refer = request.getHeader(CustomRequestHeaders.HEADER_REFERER); //"Referer"
		for(GlobalConstants.REFERER r: GlobalConstants.REFERER.values()){
			if (r.name().equals(refer)){
				chain.doFilter(request, response);
				return;
			}
		}


		//用户认证
		if(CurrentRuntimeContext.getCurrentUser()!=null) {
			CurrentRuntimeContext.removeCurrentUser();
		}
//		ThreadMsgContext.clean();
//		ThreadMsgContext.cleanMapMsg();

		final String requestHeader = request.getHeader(authConfig.getTokenHeader());
		String username = null;
		String authToken = null;
		if (requestHeader != null && requestHeader.startsWith(GlobalConstants.HEADER_TOKEN_PREFIX)) {
			//jwt
			authToken = requestHeader.substring(7);
			try {
				username = jwtTokenHandler.getUsernameFromToken(authToken);
			} catch(Exception e) {
				logger.warn("the token valid exception", e);
				send401Error(response, e.getMessage());
				return;
			}
		} else {
			logger.warn("couldn't find bearer string, will ignore the header");
		}

		logger.debug("checking authentication for user '{}'", username);
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			logger.debug("security context was null, so authorizating user");

			UserFacade userFacade = (UserFacade) this.userDetailsService.loadUserByUsername(username);

			if (jwtTokenHandler.validateToken(authToken, userFacade)) {
				//处理单用户登录
				try {
					handleSingleLogin(request, username, authToken, userFacade);
				} catch (Exception e) {
					logger.warn("the token valid exception", e);
					send401Error(response, e.getMessage());
					return;
				}

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userFacade, null, userFacade.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				NxAuthenticationManager.setAuthenticate(authentication);
				AuthenticationUtil.setAuthentication(authentication);
			}
		}

		chain.doFilter(request, response);
		//AuthenticationUtil.removeAll();
		
	}
	
	// 中止请求并返回401错误
	private void send401Error(HttpServletResponse response, String message) throws IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		CommonResult<String> result = new CommonResult<>(false, message);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		PrintWriter writer = response.getWriter();
		writer.print(InternalJsonUtil.toJson(result));
		writer.flush();
	}
	
	/**
	 * 只能一种帐户，一种租户用户登录
	 * @param request
	 * @param username
	 * @param token
	 * @throws Exception
	 */
	private void handleSingleLogin(HttpServletRequest request,String username,String token,UserDetails userDetails) throws Exception{
		//如果是单用户登录
		if (authConfig.isSingle()) {
			boolean isMobile = ContextUtil.Request.isMobile(request);
			String userAgent = isMobile ? "mobile" : "pc";
			String tenantId = ContextUtil.Request.getTenantIdFromRequestInfo();
			int overtime = authConfig.getOverTime();
			//String overTime = propertyService.getProperty("overTime","30");
			//int overtime = Integer.valueOf(overTime) * 60; //* 60 分钟转换秒
			// 从缓存中获取token
			String oldToken = jwtTokenHandler.getTokenFromCache(userAgent, tenantId, username, overtime);

			if (StringUtils.isNotEmpty(oldToken)) {
				if (jwtTokenHandler.validateToken(oldToken, userDetails) && !oldToken.equals(token)) {
					throw new Exception("当前账号已在另一地方登录，若不是本人操作，请注意账号安全！");
				}
			}
		}
	}
}
