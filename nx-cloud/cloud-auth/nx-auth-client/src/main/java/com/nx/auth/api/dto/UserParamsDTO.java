/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.dto;


import com.nx.api.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
* 
* <pre> 
* 描述：用户参数 实体对象
* 构建组：x5-bpmx-platform
* 作者:liyg
* 邮箱:liyg@jee-soft.cn
* 日期:2016-11-01 17:11:33
* 版权：广州宏天软件有限公司
* </pre>
*/
@Data
@Schema(description = "用户参数")
public class UserParamsDTO extends BaseModel<UserParamsDTO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2233174804869288487L;

	/**
	* ID_
	*/
	@Schema(name="id",description="参数id")
	protected String id; 
	
	/**
	* USER_ID_
	*/
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
	/**
	* ALIAS_
	*/
	@Schema(name="alias",description="参数别名")
	protected String alias; 
	
	/**
	* VALUE_
	*/
	@Schema(name="value",description="参数值")
	protected String value; 
	
	



}
