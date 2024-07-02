/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.time.TimeUtil;
import com.hotent.uc.model.User;
import com.hotent.uc.service.UserDetailsFacade;


@Service
public class UserDetailsFacadeImpl implements UserDetailsFacade{

	private static final long serialVersionUID = 1L;

	@Override
	public UserDetails loadUserDetails(Collection<GrantedAuthority> authorities, Object obj) throws Exception {
		// valid();
		JsonNode jsonNode = JsonUtil.toJsonNode(obj);
		String id = jsonNode.get("id").asText();
		String account = jsonNode.get("account").asText();
		String pwd = jsonNode.get("password").asText();
		int status = jsonNode.get("status").asInt();
		String fullname = jsonNode.get("fullname").asText();
		String email = jsonNode.get("email").asText();
		String mobile = jsonNode.get("mobile").asText();
        String weixin = jsonNode.get("weixin").asText();
        String tenantId = jsonNode.get("tenantId").asText();
        LocalDateTime pwdCreateTime = null;
        if(BeanUtils.isNotEmpty(jsonNode.get("pwdCreateTime"))) {
        	pwdCreateTime = TimeUtil.convertString(jsonNode.get("pwdCreateTime").asText());
        }
		//  设置用户的组织id  以及 下级组织id
		JsonNode jsonNode2 = jsonNode.get("attributes");
		Map<String, String> attributes = JsonUtil.toMap(JsonUtil.toJson(jsonNode2));
		
		User user = new User(account, fullname, pwd, authorities);
		user.setId(id);
		user.setStatus(status);
		user.setAttributes(attributes);
		user.isEnabled();
		user.setMobile(mobile);
		user.setEmail(email);
		user.setWeixin(weixin);
		user.setTenantId(tenantId);
		user.setPwdCreateTime(pwdCreateTime);
		
		return user;
	}
}
