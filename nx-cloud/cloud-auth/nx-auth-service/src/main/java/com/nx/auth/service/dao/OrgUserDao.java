/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.OrgUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * <pre> 
 * 描述：用户组织关系 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-30 10:27:31
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface OrgUserDao extends BaseMapper<OrgUser>{

	
	/**
	 * 获取组织人员关系
	 * map中可以传orgId,userId,relId(岗位id),isMaster,isCharge
	 * @param map
	 * @return
	 */
	List<OrgUser> getByParms(Map<String,Object> map);
	

	
}
