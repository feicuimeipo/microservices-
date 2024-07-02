/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.dto;

import com.nx.api.model.BaseModel;
import com.nx.common.model.base.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 *
 * map.put("roleAlias", sysRoleAuth.getRoleAlias());
 * 			map.put("methodRequestUrl", sysRoleAuth.getMethodRequestUrl());
 * 			map.put("dataPermission", sysRoleAuth.getDataPermission());
 * 			result.add(map);
 *
 * 角色权限配置
 * 
 * <pre>
 *  
 * 描述：角色权限配置 实体对象
 * 构建组：x7
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-06-29 14:27:46
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Data
@Schema(description = "角色权限配置")
public class SysRoleAuthDTO extends BaseModel<SysRoleAuthDTO> {

	private static final long serialVersionUID = 1L;

	@Schema(name = "id", description = "主键")
	protected String id;

	@Schema(name = "roleAlias", description = "角色别名")
	protected String roleAlias;

	@Schema(name = "menuAlias", description = "菜单别名")
	protected String menuAlias;

	@Schema(name = "methodAlias", description = "请求方法别名")
	protected String methodAlias;

	@Schema(name = "dataPermission", description = "数据权限设置json")
	protected String dataPermission;

	@Schema(name = "methodRequestUrl", description = "请求方法地址")
	private String methodRequestUrl;



}