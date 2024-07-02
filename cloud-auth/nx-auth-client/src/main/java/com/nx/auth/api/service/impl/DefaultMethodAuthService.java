/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service.impl;

import com.nx.auth.api.AuthServiceApi;
import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.auth.context.AuthCache;
import com.nx.auth.context.CacheKeyConst;
import com.nx.auth.api.service.IMethodAuthService;
import com.nx.api.model.R;
import com.nx.cache.annotation.CachePut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DefaultMethodAuthService implements IMethodAuthService {

	@Autowired
	AuthServiceApi authServiceApi;


	@Override
	public List<SysRoleAuthDTO> getMethodAuth(){
		//其他服务模块需要引用这个包  这个包是从portal服务模块中获取url的认证信息
		R<List<SysRoleAuthDTO>> ret = authServiceApi.getMethodRoleAuth();
		if (ret.isOK() && ret.getData()!=null){
			return ret.getData();
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<SysRoleAuthDTO> getMethodAuthFromCache() {
		List<SysRoleAuthDTO> methodAuth = getMethodAuth();
		return methodAuth;
	}

	@Override
	public String getDataPermissionInCache(String roleAlias, String methodRequestUrl){
		List<SysRoleAuthDTO> list= getMethodAuthFromCache();
		String result = "";
		String key = roleAlias + methodRequestUrl;
		for(SysRoleAuthDTO mapAuth : list) {
			if(StringUtils.isEmpty( mapAuth.getRoleAlias()) || StringUtils.isEmpty(mapAuth.getMethodRequestUrl()) ) {
				continue;
			}
			String curkey =  mapAuth.getRoleAlias() + mapAuth.getMethodRequestUrl();
			//if( mapAuth.containsKey("dataPermission") && StringUtils.isNotEmpty(mapAuth.get("dataPermission")) ){
			if(StringUtils.isNotEmpty(mapAuth.getDataPermission())){
				putDataPermissionInCache( mapAuth.getRoleAlias() ,curkey ,mapAuth.getMethodRequestUrl());
				//bean.putDataPermissionInCache(CacheKeyConst.DATA_PERMISSION+roleAlias+key, mapAuth.get("dataPermission"));
				if (curkey.equalsIgnoreCase(key)){
					result = mapAuth.getDataPermission();
				}
			}

		}
		return result;
	}


	@Override
	@CachePut(cacheName = AuthCache.AUTH_CACHE_NAME, key=CacheKeyConst.DATA_PERMISSION+"#roleAlias#methodRequestUrl")
	public String putDataPermissionInCache(String roleAlias, String methodRequestUrl, String data) {
		return data;
	}


}
