/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.dto;


import com.nx.api.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description="用户角色关系表")
public class UserRoleDTO extends BaseModel<UserRoleDTO> {

	private static final long serialVersionUID = -3390031053071859598L;
	
	/**
	* id_
	*/
	@Schema(name="id",description="id")
	protected String id; 
	
	/**
	* role_id_
	*/
	@Schema(name="roleId",description="角色id")
	protected String roleId; 
	
	/**
	* user_id_
	*/
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
	/**
	 * 以下是扩展字段，用于关联显示。
	 */

	
	// 角色名称
	@Schema(name="roleName",description="角色名称")
	protected String roleName; 
	
	
	//角色别名
	@Schema(name="alias",description="角色别名")
	protected  String alias;
	

	

}
