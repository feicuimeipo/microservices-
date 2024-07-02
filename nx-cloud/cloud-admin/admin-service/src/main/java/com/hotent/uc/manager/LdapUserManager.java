/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager;

import java.util.List;
import java.util.Map;

import org.springframework.ldap.control.PagedResult;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.Filter;

import com.hotent.uc.model.LdapUser;




@SuppressWarnings("deprecation")
public interface LdapUserManager {
	/**
	 * 取得所有的用户列表
	 * @return
	 */
	public List<LdapUser> getAll();
	
	/**
	 * @return
	 */
	List<LdapUser> get();

	public List<LdapUser> get(Filter filter);
	
	/**通过DN取得用户列表
	 * @param dn
	 * @return
	 */
	public List<LdapUser> get(DistinguishedName dn);
	
	/**通过DN取得用户列表
	 * @param dn
	 * @return
	 */
	public List<LdapUser> get(Filter filter,DistinguishedName dn);
	
	/**分页查询
	 * @param cookie
	 * @param pageSize
	 * @return
	 */
	PagedResult get(PagedResultsCookie cookie, int pageSize);

	/**用户认证
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean authenticate(String userId,String password);
	
	/**添加用户
	 * @param user
	 */
	public void addUser(LdapUser user);
	/**
	 * @param params
	 * @return
	 */
	List<LdapUser> getAll(Map<String, Object> params);

	PagedResult get(Filter filter, DistinguishedName dn,
			PagedResultsCookie cookie, int pageSize);
	/**
	 * 增量更新
	 * @param date
	 * @return
	 */
        public List<LdapUser> getPart(String date);
}
