/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.sys.persistence.model.SysModuleDetail;

public interface SysModuleDetailDao extends BaseMapper<SysModuleDetail> {
    /**
     * 根据模块主表id获取明细
     * @param moduleId
     * @param type
     * @return
     */
    List<SysModuleDetail> getModuleDetail(@Param("moduleId") String moduleId, @Param("type")String type);

    /**
     * 根据模块id删除明细
     * @param moduleId
     */
    void removeByModuleId(Serializable moduleId);
}
