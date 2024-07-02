/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service;

import com.nx.api.model.R;
import com.nx.auth.api.dto.UserFacadeDTO;
import com.nx.auth.service.model.entity.SysRoleAuth;
import com.nx.auth.service.model.entity.User;

import java.security.cert.CertificateException;
import java.util.List;

/**
 * 
 * <pre> 
 * 描述：角色权限配置 处理实现类
 * 构建组：x6
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-06-29 14:27:46
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface AuthService {

    User getUserByOpenId(String openId);

    List<SysRoleAuth> getSysRoleAuthAll();

	UserFacadeDTO loadUserByUsername(String username) throws CertificateException;

	R<String> changePassword(String account, String oldPassword, String newpassword);

	R<String> updatePhotoUrl(String account, String photoUrl);
}
