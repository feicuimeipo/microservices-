/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.Org;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 
 * <pre> 
 * 描述：组织架构 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-28 15:13:03
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface OrgDao extends BaseMapper<Org>{


	/**
	 * 根据父组织id获取其下子组织（包含父组织）
	 * @param pid
	 * @return
	 */
	List<Org> getByParentId(@Param("pid") String pid);
	
	/**
	 * 通过用户ID获取组织ID、是否主组织Map
	 * @param userId
	 * @return
	 */
	List<Org> getOrgMapByUserId(@Param("userId") String userId);
	
	/**
	 * 通过父组织ID集合查询所有子组织ID
	 * @param parentIds
	 * @return
	 */
	List<Org> getSubOrgByIds(@Param("parentIds")Set<String> parentIds);

	Org getByCode(@Param("code") String code);


}

