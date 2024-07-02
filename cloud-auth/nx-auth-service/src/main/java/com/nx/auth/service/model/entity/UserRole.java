/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName("UC_USER_ROLE")
@Schema(description="用户角色关系表")
public class UserRole extends UcBaseModel<UserRole>  {

	private static final long serialVersionUID = -3390031053071859598L;
	
	/**
	* id_
	*/
	@TableId("ID_")
	@Schema(name="id",description="id")
	protected String id; 
	
	/**
	* role_id_
	*/
	@TableField("ROLE_ID_")
	@Schema(name="roleId",description="角色id")
	protected String roleId; 
	
	/**
	* user_id_
	*/
	@TableField("USER_ID_")
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
	/**
	 * 以下是扩展字段，用于关联显示。
	 */
	
	//用户名
	@TableField(exist=false)
	@Schema(name="fullname",description="用户名称")
	protected String fullname; 
	
	
	// 角色名称
	@TableField(exist=false)
	@Schema(name="roleName",description="角色名称")
	protected String roleName; 
	
	
	//角色别名
	@TableField(exist=false)
	@Schema(name="alias",description="角色别名")
	protected  String alias;
	
	
	//账号
	@TableField(exist=false)
	@Schema(name="account",description="用户账号")
	protected String account="";
	

}
