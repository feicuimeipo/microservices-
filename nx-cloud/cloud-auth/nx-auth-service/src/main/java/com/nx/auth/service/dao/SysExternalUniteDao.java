/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.SysExternalUnite;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 *
 * <pre>
 * 描述：系统第三方集成 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:PangQuan
 * 邮箱:PangQuan@jee-soft.cn
 * 日期:2019-11-26 16:07:01
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface SysExternalUniteDao extends BaseMapper<SysExternalUnite> {

	SysExternalUnite isTypeExists(Map<String, Object> params);

	SysExternalUnite getOneByType(String type);
}
