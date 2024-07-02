/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
/**
 * 
 */
package com.hotent.portal.persistence.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;


import com.hotent.portal.PortalTestCase;
import com.hotent.uc.api.model.IPermission;
import org.junit.jupiter.api.Test;

/**
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月20日
 */
public class AuthorityManagerTest extends PortalTestCase{
	@Resource
	AuthorityManager am;
	
	@Test
	public void testSysTypeManager(){
		
		String beanId = null;
		//获取授权的实现方法，这里返回对应实现类列表。
		List<IPermission> serviceList = am.getCurUserServiceList(beanId );
		
		Map<String, Set<String>> listMap = new HashMap<>();
		Map<String, String> list = am.getMapStringByMayList(listMap);
	}
}
