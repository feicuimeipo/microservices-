/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.nianxi.api.feign.constant.GroupStructEnum;
import org.nianxi.api.feign.constant.GroupTypeConstant;
import org.nianxi.api.feign.constant.IdentityType;

import java.util.Map;


/**
* 
* <pre> 
* 描述：职务  实体对象
* 构建组：x5-bpmx-platform
* 作者:ray
* 邮箱:zhangyg@jee-soft.cn
* 日期:2016-06-29 18:00:43
* 版权：广州宏天软件有限公司
* </pre>
 * 来自com.hotent.uc.model.orgjob
*/
//@TableName("UC_ORG_JOB")
@Schema(description="职务")
public class OrgJobDTO implements java.io.Serializable{

	
	/**
	* id_
	*/
	//@TableId("ID_")
	@Schema(name="id",description="职务id")
	protected String id; 
	
	/**
	* 名称
	*/
	//@TableField("NAME_")
	@Schema(name="name",description="职务名称")
	protected String name; 
	
	/**
	* 编码
	*/
	//@TableField("CODE_")
	@Schema(name="code",description="职务编码")
	protected String code; 
	
	/**
	* 职务级别
	*/
	//@TableField("JOB_LEVEL_")
	@Schema(name="postLevel",description="职务级别")
	protected String postLevel; 
	
	/**
	* 描述
	*/
	//@TableField("DESCRIPTION_")
	@Schema(name="description",description="描述")
	protected String description; 
	
	
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
	 * 返回 名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * 返回 编码
	 * @return
	 */
	public String getCode() {
		return this.code;
	}
	
	public void setPostLevel(String postLevel) {
		this.postLevel = postLevel;
	}
	
	/**
	 * 返回 职务级别
	 * @return
	 */
	public String getPostLevel() {
		return this.postLevel;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 返回 描述
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}
	 

	
	public String getIdentityType() {
		return IdentityType.GROUP;
	}

	
	public String getGroupId() {
		return this.id;
	}

	
	public String getGroupCode() {
		return this.code;
	}

	
	public Long getOrderNo() {
		return null;
	}

	
	public String getGroupType() {
		return GroupTypeConstant.JOB.key();
	}

	
	public GroupStructEnum getStruct() {
		return null;
	}

	
	public String getParentId() {
		return null;
	}

	
	public String getPath() {
		return null;
	}

	
	public Map<String, Object> getParams() {
		return null;
	}


}
