/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.user;

//

public class UserRelFilterObject {

	////@ApiModelProperty(name="account",notes="用户账号",required=false)
	private String account;
	
	////@ApiModelProperty(name="userId",notes="用户id",required=false)
	private String userId;
	
	////@ApiModelProperty(name="typeCode",notes="汇报线分类编码",required=true)
	private String typeCode;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	
	
}
