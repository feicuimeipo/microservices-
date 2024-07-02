/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.portal.model.SysLayoutSetting;

/**
 * 布局设置 处理接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月11日
 */
public interface SysLayoutSettingManager extends BaseManager<SysLayoutSetting>{
	
	/**
	 * 通过布局Id获取布局设置
	 * @param layoutId 布局id
	 * @return
	 */
	SysLayoutSetting getByLayoutId(String layoutId);
	
}
