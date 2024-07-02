/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.PwdStrategy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PwdStrategyDao extends BaseMapper<PwdStrategy>{
	
	PwdStrategy getDefault();
}
