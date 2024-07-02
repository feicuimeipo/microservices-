/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysModule;

public interface SysModuleManager extends BaseManager<SysModule> {
    /**
     * 保存模块
     * @param sysModule
     */
    void saveModule(SysModule sysModule);

    /**
     * 根据模块编码获取
     * @param code
     * @return
     */
    SysModule getModuleByCode(String code);

    /**
     * 导出模板
     * @param idList
     * @return
     */
    Map<String,String> exportModules(List<String> idList) throws JAXBException;

    /**
     * 导入模块
     * @param unZipFilePath
     */
    void importModules(String unZipFilePath) throws JAXBException, UnsupportedEncodingException;
}
