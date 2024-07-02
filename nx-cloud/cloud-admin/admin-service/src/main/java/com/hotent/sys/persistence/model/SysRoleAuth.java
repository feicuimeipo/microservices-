/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pharmcube.mybatis.support.model.DbBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;



/**
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
@TableName("portal_sys_role_auth")
public class SysRoleAuth extends DbBaseModel<SysRoleAuth> {

	private static final long serialVersionUID = 1L;

	@Schema(name = "id", description = "主键")
	@TableId("id_")
	protected String id;

	@Schema(name = "roleAlias", description = "角色别名")
	@TableField("role_alias_")
	protected String roleAlias;

	@Schema(name = "menuAlias", description = "菜单别名")
	@TableField("menu_alias_")
	protected String menuAlias;

	@Schema(name = "methodAlias", description = "请求方法别名")
	@TableField("method_alias_")
	protected String methodAlias;

	@Schema(name = "dataPermission", description = "数据权限设置json")
	@TableField("data_permission_")
	protected String dataPermission;


	@Schema(name = "methodRequestUrl", description = "请求方法地址")
	@TableField(exist=false)
	private String methodRequestUrl;


}