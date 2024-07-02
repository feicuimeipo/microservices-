/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api;

import com.nx.api.conf.feign.FeignConfig;
import com.nx.api.model.R;
import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.auth.api.dto.UserFacadeDTO;
import com.nx.auth.api.fallbck.AuthServiceApiFallbackFactory;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name= "${remote.feign.auth-service.name}",url="${pharmcube.auth-service.url}",path = "/api/auth",contextId = "${remote.feign.auth-service.name}",fallbackFactory= AuthServiceApiFallbackFactory.class, configuration= FeignConfig.class)
public interface AuthServiceApi {
	public static String PathPrefix = "/api/auth";

	//PortalApi
	@ApiResponse(description="获取角色跟请求权限的关系")
	@RequestMapping(value="/role/v1/sysRoleAuth/v1/getMethodRoleAuth",method=RequestMethod.GET)
	public R<List<SysRoleAuthDTO>> getMethodRoleAuth();


	/**
	 * 根据用户账号获取用户信息
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/user/v1/user/loadUserByUsername",method= RequestMethod.POST)
	@ApiResponse(description = "根据账号获取userDetails信息")
	public R<UserFacadeDTO> loadUserByUsername(@RequestParam(value = "account", required = true) String account);



	/**
	 * 根据用户账号获取用户信息
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/user/v1/user/changePwd",method= RequestMethod.POST)
	@ApiResponse(description = "根据账号获取userDetails信息")
	public R<UserFacadeDTO> changePwd(String account,String oldPassword,String newPassword);


	/**
	 * 根据用户账号获取用户信息
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/user/v1/user/changePhoto",method= RequestMethod.POST)
	@ApiResponse(description = "修改头像")
	public R<UserFacadeDTO> changePhoto(String account,String photoUrl);


//	/**
//	 * protocolCode: dubbo|feign 对应refere头信息
//	 * @param appId
//	 * @return
//	 */
//	@RequestMapping(value="/cloud/app/enabled/v1",method= RequestMethod.GET)
//	@ApiResponse(description = "接口认证，返回ticket")
//	public R<String> apiEnabled(String appId,String accessToken,String encrypt,String protocol);
//
//
//	@RequestMapping(value="/cloud/app/resources/v1",method= RequestMethod.GET)
//	@ApiResponse(description = "获得资源列表")
//	public R<List<ApiResourceDTO>> getApiResources(String appId, String protocol, String ticketId);


//	@RequestMapping(value="/properties/v1/",method= RequestMethod.GET)
//	@ApiResponse(description = "获得资源列表")
//	public R<String> getProperty(String alias, String defaultValue);
}
