/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysCategory;


public interface SysCategoryManager extends BaseManager<SysCategory>{
	
	/**
	 * 是否包含指定的groupKey。
	 * @param id
	 * @param groupKey
	 * @return  Boolean
	 */
	Boolean isKeyExist(String id, String groupKey);

	/**
	 * 根据typeKey获取类型分类对象。
	 * @param typeKey
	 * @return  SysCategory
	 */
	SysCategory getByTypeKey(String typeKey);
}
