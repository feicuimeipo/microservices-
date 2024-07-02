/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.service;

import com.hotent.base.security.MethodAuthService;
import com.hotent.sys.persistence.manager.SysRoleAuthManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.hotent.portal.service.SysMethodAuthService
 * 获取请求方法的授权信息
 * @author liyg
 *
 */
@Service
@Primary
public class SysMethodAuthService implements MethodAuthService {

	@Resource
	private SysRoleAuthManager sysRoleAuthManager;
	
	@Override
	public List<Map<String, String>> getMethodAuth() {
		List<Map<String, String>> sysRoleAuthAll = sysRoleAuthManager.getSysRoleAuthAll();
		List<Map<String, String>> result = new ArrayList<>();
		for (Map<String, String> map : sysRoleAuthAll) {
			result.add(map);
		}
		return result;
	}
}
