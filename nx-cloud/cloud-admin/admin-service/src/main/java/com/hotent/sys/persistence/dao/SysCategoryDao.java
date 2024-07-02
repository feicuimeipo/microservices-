/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.dao;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.sys.persistence.model.SysCategory;


public interface SysCategoryDao extends BaseMapper<SysCategory> {
	int isKeyExist(Map<String,Object> params);
	/**
	 * 通过分类key 获取改分类
	 * @param key
	 * @return
	 */
	SysCategory getByKey(String key);
}
