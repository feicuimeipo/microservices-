/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager.impl;

import org.springframework.stereotype.Service;

import com.hotent.uc.dao.OperateLogDao;
import com.hotent.uc.manager.OperateLogManager;
import com.hotent.uc.model.OperateLog;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import org.nianxi.api.model.CommonResult;
import org.nianxi.utils.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 城市业务逻辑实现类
 *
 */
@Service
public class OperateLogManagerImpl extends BaseManagerImpl<OperateLogDao,OperateLog> implements OperateLogManager {

  

	@Override
    @Transactional
	public CommonResult<String> removeByIdStr(String ids) {
		if (BeanUtils.isNotEmpty(ids)) {
			String[]  idArray=ids.split(",");
			for (String id : idArray) {
				baseMapper.removePhysicalById(id);
			}
		}
		 return new CommonResult<String>(true, "删除日志成功！", "");
	}

	@Override
    @Transactional
	public Integer removePhysical() {
		return baseMapper.removePhysical();
	}

}