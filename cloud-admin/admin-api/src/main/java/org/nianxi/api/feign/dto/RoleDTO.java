/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.nianxi.api.feign.constant.GroupStructEnum;
import org.nianxi.api.feign.constant.GroupTypeConstant;
import org.nianxi.api.feign.constant.IdentityType;
import java.util.Map;


/**
 * 
 * <pre> 
 * 描述：角色管理 实体对象
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-30 10:28:04
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Schema(description="角色管理")
@Data
@ToString
public class RoleDTO implements java.io.Serializable {


	/**
	 * id_
	 */
	@Schema(name="id",description="角色id")
	protected String id;


	/**
	 * 角色名称
	 */
	@Schema(name="name",description="角色名称")
	protected String name;

	/**
	 * 角色别名
	 */
	@Schema(name="code",description="角色编码")
	protected String code;

	/**
	 * 0：禁用，1：启用
	 */
	@Schema(name="enabled",description="0：禁用，1：启用")
	protected Integer enabled;

	/**
	 * 角色描述
	 */
	@Schema(name="description",description="角色描述")
	protected String description="";


	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 返回 id_
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 返回 角色名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * 返回角色编码
	 * @return
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	/**
	 * 返回 0：禁用，1：启用
	 * @return
	 */
	public Integer getEnabled() {
		return this.enabled;
	}


	public String getGroupId() {
		return this.id;
	}

	public String getGroupCode() {
		return this.code;
	}

	public Long getOrderNo() {
		return Long.valueOf(1);
	}

	public String getParentId() {
		return "";
	}

	public String getPath() {
		return this.name;
	}

	public Map<String, Object> getParams() {

		return null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getIdentityType() {
		return IdentityType.GROUP;
	}


	public String getGroupType() {
		return GroupTypeConstant.ROLE.key();
	}


	public GroupStructEnum getStruct() {
		return null;
	}
}