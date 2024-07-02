/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 
 * <pre> 
 * 描述：组织参数 实体对象
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2016-11-04 11:39:44
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Data
@TableName("UC_ORG_PARAMS")
@Schema(description="组织参数")
public class OrgParams extends UcBaseModel<OrgParams> {
	private static final long serialVersionUID = 1L;

	/**
	* ID_
	*/
	@TableId("ID_")
	@Schema(name="id",description="参数id")
	protected String id; 
	
	/**
	* ORG_ID_
	*/
	@TableField("ORG_ID_")
	@Schema(name="orgId",description="组织id")
	protected String orgId; 
	
	
	/**
	* ALIAS_
	*/
	@TableField("CODE_")
	@Schema(name="alias",description="参数别名")
	protected String alias; 
	
	/**
	* VALUE_
	*/
	@TableField("VALUE_")
	@Schema(name="value",description="参数值")
	protected String value; 
	
	public void setId(String id) {
		this.id = id;
	}
	

}
