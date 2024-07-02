/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import com.hotent.uc.dao.TenantLogsDao;
import com.hotent.uc.manager.TenantLogsManager;
import com.hotent.uc.model.TenantLogs;

/**
 * 
 * <pre> 
 * 描述：租户操作日志 处理实现类
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2020-04-17 14:53:55
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
@Service("tenantLogsManager")
public class TenantLogsManagerImpl extends BaseManagerImpl<TenantLogsDao, TenantLogs> implements TenantLogsManager{

	@Override
	@Transactional
	public void deleteByTenantId(String tenantId) {
		baseMapper.deleteByTenantId(tenantId);
	}
	
}
