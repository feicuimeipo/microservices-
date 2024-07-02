/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.system.persistence.manager;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.uc.apiimpl.model.Org;

import java.util.List;

public interface IWXOrgService {
	/**
	 * 新增组织
	 * @param org
	 */
	//public void create(ObjectNode org);

    void create(Org org);

    /**
	 * 更新组织
	 * @param org
	 */
	public void update(ObjectNode org);
	
	/**
	 * 删除组织
	 * @param orgId 用户账户
	 */
	public void delete(String orgId);
	/**
	 * 批量删除
	 * @param orgIds
	 */
	public void deleteAll(String orgIds);

	//void addAll(List<ObjectNode> orgList);
	
	public String getDepartmentUser(String orgCode);
	/**
	 * 批量添加组织
	 * @param orgList
	 */
    void addAll(List<Org> orgList);

    /**
     * 微信组织机构同步
     */
    void syncAllOrg();
}
