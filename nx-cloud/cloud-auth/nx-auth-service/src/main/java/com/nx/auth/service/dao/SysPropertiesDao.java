/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.SysProperties;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统属性dao
 * 
 * @author:liyg
 * @date:2018年6月27日
 */
@Mapper
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
