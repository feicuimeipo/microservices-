/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.sys.persistence.model.SysModule;

public interface SysModuleDao extends BaseMapper<SysModule> {
    SysModule getModuleByCode(String code);
}
