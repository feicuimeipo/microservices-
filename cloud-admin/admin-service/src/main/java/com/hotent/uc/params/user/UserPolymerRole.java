/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.user;




//@ApiModel
public class UserPolymerRole {
	////@ApiModelProperty(name="code", notes="角色编码", required=true)
	private String code;
	////@ApiModelProperty(name="name", notes="角色名称", required=false)
	private String name;
	
	public UserPolymerRole(){}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
