/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.tenant;




/**
 * 租户管理员参数类
 * @author zhangxw
 *
 */
//@ApiModel
public class TenantAuthAddObject{

	////@ApiModelProperty(name="typeId",notes="租户类型id",required=true)
	private String typeId;
	
	////@ApiModelProperty(name="tenantId",notes="租户id",required=false)
	private String tenantId;
	
	////@ApiModelProperty(name="accounts",notes="用户帐号，多个用“,”号隔开")
	private String accounts;

	
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}
	
}
