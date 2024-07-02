/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户参数对象
 * 
 * @author liangqf
 * 来自userVO
 */
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO  implements java.io.Serializable{

	@Schema(name = "id",description="用户id")
	private String id;

	@Schema(name = "account",description="登录帐号（更新时不会更新）", required = true)
	private String account;

	@Schema(name = "fullname",description="用户名", required = true)
	private String fullname;

	@Schema(name = "userNumber",description="工号", required = true)
	private String userNumber;

	@Schema(name = "password",description="登录密码（更新时不会更新）", required = true)
	private String password;

	@Schema(name = "email",description="邮箱地址")
	private String email;

	@Schema(name = "mobile",description="手机号码")
	private String mobile;

	@Schema(name = "address",description="地址")
	private String address;

	@Schema(name = "sex",description="性别", allowableValues = "男,女,未知")
	private String sex;

	@Schema(name = "photo",description="头像（更新时不会更新）", allowableValues = "用户的头像")
	protected String photo;

	@Schema(name = "status",description="状态 1：正常；0：禁用；-1：待激活；-2：离职（默认为正常）")
	private Integer status;

	@Schema(name = "idCard",description="身份证号")
	private String idCard;

	@Schema(name = "phone",description="办公电话")
	private String phone;

	@Schema(name = "birthday",description="生日")
	private String birthday;

	@Schema(name = "entryDate",description="入职日期")
	private String entryDate;

	@Schema(name = "leaveDate",description="离职日期")
	private LocalDate leaveDate;

	@Schema(name = "education",description="学历")
	private String education;

	@Schema(name = "updateTime",description="更新时间")
	private LocalDateTime updateTime;

	@Schema(name = "isDelete",description="是否已删除 1已删除 0未删除（更新时不会更新）")
	private String isDelete;

	@Schema(name = "version",description="版本号（更新时不会更新）")
	private Integer version;

	@Schema(name = "from",description="来源")
	private String from;

	@Schema(name = "params",description="用户参数（获取单个用户时才会有值）")
	private Map<String, Object> params;

	@Schema(name="tenantId",description="租户id")
	protected String tenantId;



	public String toString() {
		return "{" + "\"" + "account" + "\"" + ":" + "\"" + this.account + "\"," + "\"" + "fullname" + "\"" + ":" + "\""
				+ this.fullname + "\"," + "\"" + "userNumber" + "\"" + ":" + "\"" + this.userNumber + "\"," + "\""
				+ "password" + "\"" + ":" + "\"" + this.password + "\"," + "\"" + "email" + "\"" + ":" + "\""
				+ this.email + "\"," + "\"" + "mobile" + "\"" + ":" + "\"" + this.mobile + "\"," + "\"" + "address"
				+ "\"" + ":" + "\"" + this.address + "\"," + "\"" + "sex" + "\"" + ":" + "\"" + this.sex + "\"," + "\""
				+ "status" + "\"" + ":" + "\"" + this.status + "\"," + "\"" + "idCard" + "\"" + ":" + "\"" + this.idCard
				+ "\"," + "\"" + "phone" + "\"" + ":" + "\"" + this.phone + "\"," + "\"" + "birthday" + "\"" + ":"
				+ "\"" + this.birthday + "\"," + "\"" + "entryDate" + "\"" + ":" + "\"" + this.entryDate + "\"," + "\"" + "leaveDate" + "\"" + ":" + "\"" + this.leaveDate + "\"," + "\""
				+ "education" + "\"" + ":" + "\"" + this.education + "\"" + "\"" + "updateTime" + "\"" + ":" + "\""
				+ this.updateTime + "\"" + "\"" + "isDelete" + "\"" + ":" + "\"" + this.isDelete + "\"" + "\""
				+ "version" + "\"" + ":" + "\"" + this.version + "\"" + "\"" + "from" + "\"" + ":" + "\"" + this.from
				+ "\"" + "}";
	}


}
