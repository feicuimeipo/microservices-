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


/**
 * UC_TENANT_MANAGE
 * <pre> 
 * 描述：租户管理实体对象
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2020-04-17 10:56:07
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
@Data
public class TenantManageDTO extends BaseModel<TenantManageDTO> {

	private static final long serialVersionUID = 1L;


	protected String id;

	@Schema(description="租户类型id")
	protected String typeId;

	@Schema(description="租户名称（编码）")
	protected String name;

	@Schema(description="租户别名（编码）")
	protected String code;


	@Schema(description="状态：启用（enable）、禁用（disabled）、draft(预设）")
	protected String status;

	private TenantHandlerDTO tenantHandlerDTO;


}