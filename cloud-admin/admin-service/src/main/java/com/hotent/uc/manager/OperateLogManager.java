/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager;

import com.hotent.uc.model.OperateLog;
import com.pharmcube.mybatis.support.manager.BaseManager;
import org.nianxi.api.model.CommonResult;

/**
 * 城市业务逻辑接口类
 *
 */
public interface OperateLogManager extends   BaseManager<OperateLog>{
	
	/**
	 * 删除所有已逻辑删除的实体（物理删除）
	 * @param entityId 实体对象ID
	 */
	Integer removePhysical();
	
	
	CommonResult<String>  removeByIdStr(String ids);
	
}