/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nx.auth.service.model.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * <pre> 
 * 描述：用户角色管理 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-30 10:28:34
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRole> {

	/**
	 *删除所有已逻辑删除的实体（物理删除）
	 * @param
	 */
	Integer removePhysical();
	
	/**
	 * 根据用户和角色id 查询 关联关系。
	 * @param roleId
	 * @param userId
	 * @return
	 */
	UserRole getByRoleIdUserId(@Param("roleId") String roleId,@Param("userId") String userId);
	
	
	/**
	 * 根据角色ID查询关联的用户。
	 * @param roleId
	 * @return
	 */
	List<UserRole> getListByRoleId(@Param("roleId") String roleId);
	
	/**
	 * 根据角色别名查询关联的用户。
	 * @return
	 */
	List<UserRole> getListByCode(@Param("code") String code);

	/**
	 * 根据用户的id删除该用户的角色
	 */
	void removePhysicalById(@Param("roleId") String roleId);

	/**
	 * 根据用户的id删除该用户的角色
	 */
	void removeByUserId(@Param("userId") String userId,@Param("updateTime")LocalDateTime updateTime);
	
	/**
	 * 根据roleId，删除关联的用户
	 * @param roleId
	 */
	void removeByRoleId(@Param("roleId") String roleId,@Param("updateTime")LocalDateTime updateTime);
	
	/**
	 * 根据自定义sql查询
	 * @param iPage 
	 * @return
	 */
	IPage<UserRole> queryByParams(IPage<UserRole> iPage, @Param(Constants.WRAPPER)Wrapper<UserRole> userRole);
	
	List<UserRole> queryByParams(@Param(Constants.WRAPPER) Wrapper<UserRole> userRole);
	
	/**
	 * 根据账号获取用户角色关联记录(包括逻辑删除的记录)
	 * @param account
	 * @return
	 */
	List<UserRole> getByAccount(String account);
}
