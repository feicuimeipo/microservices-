/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.dao;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.sys.persistence.model.SysProperties;

/**
 * 系统属性dao
 * @company 广州宏天软件股份有限公司
 * @author:liyg
 * @date:2018年6月27日
 */
public interface SysPropertiesDao extends BaseMapper<SysProperties> {
	
	/**
	 * 分组列表。
	 * @return
	 */
	List<String> getGroups();
	
	
	/**
	 * 判断别名是否存在。
	 * @param sysProperties
	 * @return
	 */
	boolean isExist(SysProperties sysProperties);
}
