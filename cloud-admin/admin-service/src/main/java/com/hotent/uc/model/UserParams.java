/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.model;




import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
@TableName("UC_USER_PARAMS")
@Schema(description="用户参数")
public class UserParams extends UcBaseModel<UserParams>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2233174804869288487L;

	/**
	* ID_
	*/
	@TableId("ID_")
	@Schema(name="id",description="参数id")
	protected String id; 
	
	/**
	* USER_ID_
	*/
	@TableField("USER_ID_")
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
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
	


}
