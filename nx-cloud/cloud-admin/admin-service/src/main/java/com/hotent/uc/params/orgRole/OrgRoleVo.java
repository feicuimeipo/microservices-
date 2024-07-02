/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.orgRole;




//@ApiModel(description="组织角色视图对象")
public class OrgRoleVo {
	
	////@ApiModelProperty(name="orgCode",notes="组织编码",required=true)
	private String orgCode;
	
	////@ApiModelProperty(name="roleCodes",notes="角色编码，多个用英文逗号隔开",required=true)
	private String roleCodes;
	
	////@ApiModelProperty(name="isInherit",notes="子组织是否可继承，0不可以，1可以",required=true)
	private int isInherit;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public int getIsInherit() {
		return isInherit;
	}

	public void setIsInherit(int isInherit) {
		this.isInherit = isInherit;
	}

}
