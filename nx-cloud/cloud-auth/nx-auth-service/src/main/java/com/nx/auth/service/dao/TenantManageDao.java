/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.TenantManage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * <pre> 
 * 描述：租户管理 DAO接口
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2020-04-17 10:56:07
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
@Mapper
public interface TenantManageDao extends BaseMapper<TenantManage> {
	/**
	 * 根据编码获取租户
	 * @param code
	 * @return
	 */
	TenantManage getByCode(@Param("code")String code);

	/**
	 * 根据域名获取租户
	 * @param domain
	 * @return
	 */
	TenantManage getByDomain(@Param("domain") String domain);
	


}
