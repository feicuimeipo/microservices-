/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.role;

import com.hotent.uc.manager.RoleManager;
import com.hotent.uc.model.Role;
import com.hotent.uc.util.OperateLogUtil;
import com.hotent.uc.util.UpdateCompare;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;




/**
 * 角色vo类
 * @author zhangxw
 *
 */
//@ApiModel
public class RoleVo implements UpdateCompare {

	////@ApiModelProperty(name="name",notes="角色名称",required=true)
	private String name;
	
	////@ApiModelProperty(name="code",notes="角色编码",required=true)
	private String code;
	
	////@ApiModelProperty(name="enabled",notes="是否禁用 0:禁用；1：正常（默认为正常）")
	private Integer enabled;
	
	////@ApiModelProperty(name="description",notes="角色描述")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static Role parse(RoleVo roleVo){
		Role role = new Role();
		role.setName(roleVo.getName());
		role.setCode(roleVo.getCode());
		role.setDescription(roleVo.getDescription());
		role.setEnabled(BeanUtils.isEmpty(roleVo.getEnabled())?1:roleVo.getEnabled());
		return role;
	}

	@Override
	public String compare() throws Exception {
	    RoleManager roleService =	AppUtil.getBean(RoleManager.class);
	    Role oldRole=roleService.getByAlias(this.code);
		return OperateLogUtil.compare(this,changeVo(oldRole));
	}


	public RoleVo changeVo(Role role) {
		RoleVo roleVo=new RoleVo();
		if (BeanUtils.isEmpty(role)) return roleVo;
		roleVo.setCode(role.getCode());  
		roleVo.setDescription(role.getDescription());
		roleVo.setEnabled(role.getEnabled());
		roleVo.setName(role.getName());
		return roleVo;
	}
}
