/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.user;




import java.util.List;

/**
 * 用户状态变更参数
 * @author heyifan
 * @date 2018年1月22日
 */
//@ApiModel
public class UserStatusVo {
	////@ApiModelProperty(name="status", notes="状态  1：正常；0：禁用；-1：待激活；-2：离职", required=true, example="1")
	private Integer status;
	////@ApiModelProperty(name="accounts", notes="账号列表", required=true)
	private List<String> accounts;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public List<String> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
	}
}
