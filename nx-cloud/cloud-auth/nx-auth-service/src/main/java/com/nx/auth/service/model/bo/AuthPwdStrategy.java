/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.bo;

import com.nx.api.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
* uc_pwd_strategy
* <pre> 
* 描述：uc_pwd_strategy 实体对象
* 构建组：x7
* 作者:tangxin
* 邮箱:tangxin@jee-soft.cn
* 日期:2020-05-19 10:52:36
* 版权：广州宏天软件有限公司
* </pre>
*/
@Schema(description = "密码策略")
@Data
public class AuthPwdStrategy extends BaseModel<AuthPwdStrategy> {

	private static final long serialVersionUID = 1L;

	@Schema(description="ID_")
	protected String id;

	@Schema(description="初始化密码")
	protected String initPwd;

	@Schema(description="密码策略")
	protected Short pwdRule;

	@Schema(description="密码长度")
	protected Short pwdLength;

	@Schema(description="密码可用时长")
	protected Short duration;

	@Schema(description="启用策略	0:停用，1:启用")
	protected Short enable = 1;

	public AuthPwdStrategy() {
	}

	public AuthPwdStrategy(String initPwd, Short pwdRule, Short pwdLength, Short duration, Short enable) {
		this.initPwd = initPwd;
		this.pwdRule = pwdRule;
		this.pwdLength = pwdLength;
		this.duration = duration;
		this.enable = enable;
	}

	public AuthPwdStrategy(String id, String initPwd, Short pwdRule, Short pwdLength, Short duration, Short enable) {
		this.id = id;
		this.initPwd = initPwd;
		this.pwdRule = pwdRule;
		this.pwdLength = pwdLength;
		this.duration = duration;
		this.enable = enable;
	}


	
}
