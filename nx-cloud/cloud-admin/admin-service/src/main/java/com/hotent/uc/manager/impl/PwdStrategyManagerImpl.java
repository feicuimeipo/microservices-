/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager.impl;

import com.hotent.base.service.PwdStrategyService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.nianxi.api.exception.BaseException;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import org.nianxi.utils.JsonUtil;
import com.hotent.uc.dao.PwdStrategyDao;
import com.hotent.uc.manager.PwdStrategyManager;
import com.hotent.uc.model.PwdStrategy;

@Service("pwdStrategyManager")
public class PwdStrategyManagerImpl extends BaseManagerImpl<PwdStrategyDao, PwdStrategy> implements PwdStrategyManager, PwdStrategyService {

	@Override
	public PwdStrategy getDefault() {
		return baseMapper.getDefault();
	}

	@Override
	public JsonNode getJsonDefault() {
		try {
			PwdStrategy pwdStrategy = baseMapper.getDefault();
			return JsonUtil.toJsonNode(pwdStrategy);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException("获取默认密码策略失败");
		}
	}

}
