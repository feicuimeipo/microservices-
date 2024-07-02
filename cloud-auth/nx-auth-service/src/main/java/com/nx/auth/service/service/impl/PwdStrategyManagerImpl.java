/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.impl;

import com.nx.auth.service.dao.PwdStrategyDao;
import com.nx.auth.service.model.entity.PwdStrategy;
import com.nx.auth.service.service.PwdStrategyManager;
import com.nx.mybatis.support.manager.impl.BaseManagerImpl;
import org.springframework.stereotype.Service;

@Service("pwdStrategyManager")
public class PwdStrategyManagerImpl extends BaseManagerImpl<PwdStrategyDao, PwdStrategy> implements PwdStrategyManager {

	@Override
	public PwdStrategy getDefault() {
		return baseMapper.getDefault();
	}

//	@Override
//	public JsonNode getJsonDefault() {
//		try {
//			PwdStrategy pwdStrategy = baseMapper.getDefault();
//			return JsonUtil.toJsonNode(pwdStrategy);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BaseException("获取默认密码策略失败");
//		}
//	}

}
