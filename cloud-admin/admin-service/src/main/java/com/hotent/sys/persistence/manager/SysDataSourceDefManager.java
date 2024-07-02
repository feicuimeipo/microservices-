/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import java.util.List;
import java.util.Map;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysDataSourceDef;

public interface SysDataSourceDefManager extends BaseManager<SysDataSourceDef>{

	/**
	 * 获取这个className的拥有setter的字段
	 * @param className
	 * @return 
	 * JSONArray
	 * @exception 
	 * @since  1.0.0
	 */
	List<Map<String,String>> getHasSetterFieldsJsonArray(String classPath);
	
}
