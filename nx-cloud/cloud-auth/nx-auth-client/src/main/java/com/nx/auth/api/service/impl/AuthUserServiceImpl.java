/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.service.impl;

import com.nx.auth.api.dto.UserDetailDTO;
import com.nx.auth.api.service.IAuthUserService;
import com.nx.auth.context.ContextUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 类 {@code UserServiceImpl} 用户服务的实现
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月9日
 */
@Primary
@Service
public class AuthUserServiceImpl implements IAuthUserService {


	@Override
	public UserDetailDTO getUserDetail()  {
		return ContextUtil.getCurrentUser().getUserDetail();
	}



}
