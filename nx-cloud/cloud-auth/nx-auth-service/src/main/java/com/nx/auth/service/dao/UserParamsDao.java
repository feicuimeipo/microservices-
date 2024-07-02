/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.UserParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * <pre> 
 * 描述：用户参数 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2016-11-01 17:11:33
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface UserParamsDao extends BaseMapper<UserParams>{


	List<UserParams> getByUserId(@Param("id") String id);

	UserParams getByUserIdAndCode(@Param("userId") String userId,@Param("code") String code);

}
