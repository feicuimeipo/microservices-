/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.resouce;

import com.hotent.uc.util.UpdateCompare;




/**
 * 维度VO类
 * @author zhangxw
 *
 */
//@ApiModel
public class ResouceVo implements UpdateCompare {

	////@ApiModelProperty(name="roleCode",notes="角色编码",required=true)
	private String roleCode;
	
	////@ApiModelProperty(name="resouce",notes="资源",required=true)
	private String resouce;
	
	

	public String getRoleCode() {
		return roleCode;
	}



	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}



	public String getResouce() {
		return resouce;
	}



	public void setResouce(String resouce) {
		this.resouce = resouce;
	}



	@Override
	public String compare() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	



	
}
