/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.SysRoleAuth;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * <pre> 
 * 描述：角色权限配置 DAO接口
 * 构建组：x7
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-06-29 14:27:46
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface SysRoleAuthDao extends BaseMapper<SysRoleAuth> {

	List<SysRoleAuth> getSysRoleAuthAll();

}
