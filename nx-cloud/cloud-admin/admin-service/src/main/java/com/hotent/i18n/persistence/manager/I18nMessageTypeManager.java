/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.i18n.persistence.manager;

import java.util.List;

import org.nianxi.mybatis.support.manager.BaseManager;
import com.hotent.i18n.persistence.model.I18nMessageType;

/**
 * 
 * <pre> 
 * 描述：国际化资源支持的语言类型 处理接口
 * @author zhaoxy
 * @company 广州宏天软件股份有限公司
 * @email zhxy@jee-soft.cn
 * @date 2018-06-06 14:20
 * </pre>
 */
public interface I18nMessageTypeManager extends BaseManager<I18nMessageType> {

    /**
     * 根据type获取语言类型
     * @param type
     * @return
     */
    I18nMessageType getByType(String type);
    
    /**
     * 获取所有语言类型
     */
    List<I18nMessageType> getAllType();
    
    /**
     * 清空语言类型缓存
     */
    void delMessageTypeCache();
}
