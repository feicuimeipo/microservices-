/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.Demension;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 
 * <pre> 
 * 描述：维度管理 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2017-07-19 15:30:09
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
@Repository
public interface DemensionDao extends BaseMapper<Demension>{

	/**
	 *删除所有已逻辑删除的实体（物理删除）
	 * @param
	 */
	Integer removePhysical();


	/**
	 * 获取默认维度
	 * @return
	 */
	Demension getDefaultDemension();


	
}

