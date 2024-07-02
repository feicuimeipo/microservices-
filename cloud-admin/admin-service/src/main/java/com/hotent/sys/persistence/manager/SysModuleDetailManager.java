/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import java.io.Serializable;
import java.util.List;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysModuleDetail;

public interface SysModuleDetailManager extends BaseManager<SysModuleDetail> {
    /**
     * 根据模块主表id获取明细
     * @param moduleId
     * @param type
     * @return
     */
    List<SysModuleDetail> getModuleDetail(String moduleId, String type);

    /**
     * 根据模块id删除明细
     * @param moduleId
     */
    void removeByModuleId(Serializable moduleId);
}
