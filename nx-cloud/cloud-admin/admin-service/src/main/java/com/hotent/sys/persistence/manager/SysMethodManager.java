/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import java.util.List;
import java.util.Map;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.hotent.sys.persistence.model.SysMethod;

/**
 * 
 * <pre> 
 * 描述：系统请求方法的配置 （用于角色权限配置） 处理接口
 * 构建组：x7
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-06-29 14:23:28
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface SysMethodManager extends BaseManager<SysMethod>{
	
	/**
	 * 判断是否已经存在
	 * @param alias
	 * @return
	 */
	boolean isExistByAlias(String alias);

	List<String> getCurrentUserMethodAuth();
	
	/**
	 * 获取角色授权的方法
	 * @param roleAlias
	 * @return
	 */
	List<Map<String, Object>> getAllMethodByRoleAlias(String roleAlias);
	
	/**
	 * 获取角色已有权限的后台接口集
	 * @param roleAlias
	 * @return
	 */
	PageList<SysMethod> getRoleMethods(String roleAlias,QueryFilter queryFilter);

}
