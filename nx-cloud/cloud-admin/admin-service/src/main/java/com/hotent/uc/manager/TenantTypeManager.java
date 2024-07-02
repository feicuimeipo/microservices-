/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager;

import java.util.List;

import com.pharmcube.mybatis.support.manager.BaseManager;
import org.nianxi.api.model.CommonResult;
import com.hotent.uc.model.TenantType;

/**
 * 
 * <pre> 
 * 描述：租户类型 处理接口
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2020-04-17 10:52:37
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
public interface TenantTypeManager extends BaseManager<TenantType>{
	
	/**
	 * 根据状态获取
	 * @param status
	 * @param authIds
	 * @return
	 */
	List<TenantType> getByStatus(String status,List<String> authIds);
	
	/**
	 * 根据类型编码获取
	 * @param code
	 * @return
	 */
	TenantType getByCode(String code);
	
	/**
	 * 获取默认分类
	 * @return
	 */
	TenantType getDefault();
	
	/**
	 * 设置默认租户类型
	 * @param code
	 * @return
	 */
	CommonResult<String> setDefault(String code);
}
