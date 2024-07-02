/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.dao;
import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hotent.uc.model.OrgRole;

/**
 * 
 * <pre> 
 * 描述：组织角色管理 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2017-12-25 10:25:20
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface OrgRoleDao extends BaseMapper<OrgRole> {

	/**
	 *删除所有已逻辑删除的实体（物理删除）
	 * @param entityId 实体对象ID
	 */
	Integer removePhysical();
	
	OrgRole getByOrgIdAndRoleId(@Param("orgId") String orgId,@Param("roleId") String roleId);
	
	void delByOrgIdAndRoleId(@Param("orgId") String orgId,@Param("roleId") String roleId,@Param("updateTime")LocalDateTime updateTime);

	IPage<OrgRole> query(IPage<OrgRole> convert2iPage,@Param(Constants.WRAPPER) Wrapper<OrgRole> wrapper);
}
