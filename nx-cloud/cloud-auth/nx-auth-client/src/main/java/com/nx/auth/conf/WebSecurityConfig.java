/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.conf;

import com.nx.auth.security.CustomPwdEncoder;
import com.nx.auth.security.NxAuthenticationManager;
import com.nx.auth.security.NxAccessDecisionManagerFilter;
import com.nx.auth.security.NxDecisionManager;
import com.nx.auth.security.denied.JwtAccessDeniedHandler;
import com.nx.auth.security.denied.JwtUnAuthenticationEntryPoint;
import com.nx.auth.security.denied.MyHttpSessionRequestCache;
import com.nx.auth.security.impl.UserCacheImpl;
import com.nx.auth.security.impl.UserDetailsServiceImpl;
import com.nx.auth.security.jwt.JwtAuthorizationFilter;
import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.support.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Import({AuthConfig.class})
@EnableConfigurationProperties({AuthConfig.class})
public class WebSecurityConfig  {
	private final AuthConfig authConfig;

	@Autowired
	private  UserDetailsService userDetailsService;
	@Autowired
	private  AccessDecisionManager accessDecisionManager;
	@Autowired
	private  JwtTokenHandler jwtTokenHandler;
	@Autowired
	private HttpSessionRequestCache httpSessionRequestCache;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	public WebSecurityConfig(AuthConfig authConfig) {
		authConfig.setPermitAll(authConfig.getAuthenticationPath());
		this.authConfig = authConfig;
//		authenticationManager = authenticationManager();
//		this.jwtTokenHandler = jwtTokenHandler();
//		this.userDetailsService = userDetailsService();
//		this.accessDecisionManager = accessDecisionManager();
//		this.httpSessionRequestCache = myHttpSessionRequestCache();
	}




	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		List<String> permitAlls = Arrays.asList(authConfig.getPermitAll().split(","));
		List<String> denyAlls  = Arrays.asList(authConfig.getDenyAll().split(","));
		denyAlls.addAll(AuthenticationUtil.getDenyUri());
		permitAlls.addAll(AuthenticationUtil.getAllowUri());

		// Custom JWT based security filter
		httpSecurity
				.requestCache()
					.requestCache(httpSessionRequestCache)
			.and()
				.addFilterBefore(corsFilter(),ChannelProcessingFilter.class)
				.addFilterBefore(new JwtAuthorizationFilter(jwtTokenHandler, authConfig,userDetailsService),UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new NxAccessDecisionManagerFilter(accessDecisionManager), FilterSecurityInterceptor.class)
				.exceptionHandling()
				.authenticationEntryPoint(new JwtUnAuthenticationEntryPoint())
				.accessDeniedHandler(new JwtAccessDeniedHandler())
			.and() // don't create session
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.authorizeRequests()
				.accessDecisionManager(accessDecisionManager) //后台权限控制制
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers(permitAlls.toArray(new String[]{})).permitAll()
				.antMatchers(denyAlls.toArray(new String[]{})).denyAll()
				.anyRequest()
				.authenticated()
				.and()
				.authenticationManager(authenticationManager)
		;

			httpSecurity
				.csrf()
				.disable()  // we don't need CSRF because our token is invulnerable
				.headers()
				.frameOptions()
				.sameOrigin() //.disable()			.
				.cacheControl();

		return httpSecurity.build();
	}



//	@Bean
//	public WebSecurityCustomizer webSecurityCustomizer() {
//		return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**","/static/**");
//	}


	/**
	 * 允许跨域访问的源
	 * @return
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();  
		corsConfiguration.addAllowedOrigin("*");  
		corsConfiguration.addAllowedHeader("*");  
		corsConfiguration.addAllowedMethod("*");  
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);  
	}

	@Bean
	@ConditionalOnMissingBean(JwtTokenHandler.class)
	JwtTokenHandler jwtTokenHandler(@Autowired AuthConfig authConfig){
		return new JwtTokenHandler(authConfig);
	}


	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	UserCache userCache(){
		return new UserCacheImpl();
	}

	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	UserDetailsService userDetailsService(){
		UserDetailsService userDetailsService = new UserDetailsServiceImpl(userCache());
		return userDetailsService;
	}

	@Bean
	@Primary
	public PasswordEncoder passwordEncoder() {
		return new CustomPwdEncoder();
	}


	//注册后台权限控制器
	@Bean
	@ConditionalOnMissingBean(AccessDecisionManager.class)
	public AccessDecisionManager accessDecisionManager() {
		return new NxDecisionManager();
	}

	@Bean
	@ConditionalOnMissingBean(JwtTokenHandler.class)
	JwtTokenHandler jwtTokenHandler(){
		return new JwtTokenHandler(authConfig);
	}


	@Bean("myHttpSessionRequestCache")
	MyHttpSessionRequestCache myHttpSessionRequestCache(){
		return new MyHttpSessionRequestCache();
	}

	@Bean
	@ConditionalOnMissingBean(NxAuthenticationManager.class)
	NxAuthenticationManager authenticationManager(){
		return new NxAuthenticationManager();
	}

}
