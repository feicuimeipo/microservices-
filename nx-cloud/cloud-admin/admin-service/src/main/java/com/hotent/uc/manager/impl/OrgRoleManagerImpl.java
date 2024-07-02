/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import com.pharmcube.mybatis.support.query.PageBean;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryField;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.pharmcube.mybatis.support.query.QueryOP;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.id.UniqueIdUtil;
import com.hotent.uc.dao.OrgRoleDao;
import com.hotent.uc.manager.OrgManager;
import com.hotent.uc.manager.OrgRoleManager;
import com.hotent.uc.model.Org;
import com.hotent.uc.model.OrgRole;
import com.hotent.uc.model.Role;

/**
 * 
 * <pre> 
 * 描述：组织角色管理 处理实现类
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2017-12-25 10:25:20
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service
public class OrgRoleManagerImpl extends BaseManagerImpl <OrgRoleDao, OrgRole> implements OrgRoleManager{
	
	@Override
    @Transactional
	public void addOrgRole(String orgId, Role role,int isInherit) throws SQLException {

			OrgRole or = baseMapper.getByOrgIdAndRoleId(orgId,role.getId() );
			if(BeanUtils.isNotEmpty(or)){
				or.setIsDelete("0");
				or.setIsInherit(isInherit);
				or.setOrgId(orgId);
				or.setRoleName(role.getName());
				or.setRoleId(role.getId());
				this.update(or);
	
			}else {
				or = new OrgRole();
				or.setId(UniqueIdUtil.getSuid());
				or.setIsInherit(isInherit);
				or.setOrgId(orgId);
				or.setRoleId(role.getId());
				or.setRoleName(role.getName());
				or.setVersion(1);
//				or.setIsDelete("0");
				this.create(or);
			}		
	}

	@Override
    @Transactional
	public void delByOrgIdAndRoleId(String orgId, String roleId) {
		baseMapper.delByOrgIdAndRoleId(orgId, roleId,LocalDateTime.now());
	}
	
	
	
	@Override
	@Transactional(readOnly=true)
	public PageList<OrgRole> query(QueryFilter<OrgRole> queryFilter) {
		
		Map<String, Object> queryFilterParams = getInitParams(queryFilter);
		if (BeanUtils.isNotEmpty(queryFilterParams) && queryFilterParams.containsKey("orgId")) {
			String orgId=(String) queryFilterParams.get("orgId");
			OrgManager orgService=AppUtil.getBean(OrgManager.class);
			Org org=orgService.get(orgId);
			if (BeanUtils.isNotEmpty(org)) {
				queryFilter.addParams("path", org.getPath());
			}
		}
		PageBean pageBean = queryFilter.getPageBean();
		IPage<OrgRole> result=baseMapper.query(convert2IPage(pageBean),convert2Wrapper(queryFilter, currentModelClass()));
		return new PageList<OrgRole>(result);
	}
	
	public Map<String, Object> getInitParams(QueryFilter queryFilter){
		List<QueryField> querys = queryFilter.getQuerys();
		Map<String, Object> initParams = new LinkedHashMap<String, Object>();
		if (BeanUtils.isEmpty(querys))
			return initParams;
		for (QueryField element : querys) {
			QueryField queryField = (QueryField) element;
			QueryOP operation = queryField.getOperation();
			if (QueryOP.IS_NULL.equals(operation) || QueryOP.NOTNULL.equals(operation)
					|| QueryOP.IN.equals(operation)) {
				continue;
			}
			String property = queryField.getProperty();
			if (property.indexOf(".") > -1) {
				property = property.substring(property.indexOf(".") + 1);
			}
			Object value = queryField.getValue();
			initParams.put(property, value);
		}
		initParams.putAll(queryFilter.getParams());
		return initParams;
	}

	@Override
    @Transactional
	public Integer removePhysical() {
		return baseMapper.removePhysical();
	}
	
	
}
