/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.dto;

import com.nx.auth.context.BootConstant;
import com.nx.api.model.BaseModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.nx.auth.context.BootConstant.FROM_SYSTEM;

/**
  * 类 {@code UserFacade} 用户的默认实现
  * 
  * @author heyifan
  * @email heyf@jee-soft.cn
  * @date 2018年7月5日	
  */
@Data
public class UserFacadeDTO extends BaseModel {
	private static final long serialVersionUID = -47458854186013987L;

	public enum AttributeType{
		//当前用户的主组织
		CURRENT_USER_MAIN_ORGID("CURRENT_USER_MAIN_ORGID"),
		//当前用户的组织idS  1,2,3
		CURRENT_USER_ORGIDS("CURRENT_USER_ORGIDS"),
		//当前用户子组织idS  1,2,3 以及下级组织id
		CURRENT_USER_SUB_ORGIDS("CURRENT_USER_SUB_ORGIDS");

		private String code;
		AttributeType(String code) {
			this.code = code;
		}
	}

	
	/**
	* 主键ID
	*/
	protected String id; 
	
	/**
	* 姓名
	*/
	protected String fullname; 
	
	/**
	* 账号
	*/
	protected String account; 
	
	/**
	* 密码
	*/
	protected String password; 
	
	/**
	* 邮箱
	*/
	protected String email;

	
	/**
	* 手机号码
	*/
	protected String mobile; 
	
	/**
	* 微信号
	*/
	protected String weixin;

	/**
	 * 微信用户唯一识别号
	 */
	protected String openId;


	/**
	* 创建时间
	*/
	protected LocalDateTime createTime; 


	/**
	* 性别：男，女，未知
	*/
	protected String sex; 
	
	/**
	* 来源
	*/
	protected String from= FROM_SYSTEM;
	
	/**
	* 0:禁用，1正常
	*/
	protected Integer status; 
	
	
	/**
	 * 组织ID，用于在组织下添加用户。
	 */
	protected String groupId="";
	
	/**
	 * 微信同步关注状态  0：未同步  1：已同步，尚未关注  2：已同步且已关注
	 */
	protected Integer hasSyncToWx;
	

	/**
	 * 密码策略时间
	 */
	protected LocalDateTime pwdCreateTime;
	
	/**
	* 租户id
	*/
	protected String tenantId;

	protected boolean admin;


	protected UserDetailDTO userDetail;


	protected List<UserRoleDTO> userRoles= Collections.emptyList();

	
	/**
	 * 其他属性
	 * 如是是数组，逗号隔开
	 * 当前用户的主组织：	 * CURRENT_USER_MAIN_ORGID
	 * 当前用户的组织idS  1,2,3 以及下级组织id:CURRENT_USER_MAIN_ORGID	 *
	 * 当前用户的组织idS  1,2,3 以及下级组织id:CURRENT_USER_SUB_ORGIDS
	 */
	// 当前用户的主组织：	 * CURRENT_USER_MAIN_ORGID
	private OrgDTO mainOrg;

	//当前用户的组织idS  1,2,3 以及下级组织id:CURRENT_USER_MAIN_ORGID	 *
	private OrgDTO[] orgs=new OrgDTO[]{};

	//当前用户的组织idS  1,2,3 以及下级组织id:CURRENT_USER_SUB_ORGIDS
	private OrgDTO[] subOrgs=new OrgDTO[]{};


	private TenantManageDTO tenantManageDTO;



	public String getUserId() {
		return this.id;
	}


	public void setUserId(String userId) {
		this.id=userId;

	}

	public boolean isAdmin() {
		String tmp = BootConstant.SYSTEM_ACCOUNT;
		return tmp.equals(this.account);
	}


	
}