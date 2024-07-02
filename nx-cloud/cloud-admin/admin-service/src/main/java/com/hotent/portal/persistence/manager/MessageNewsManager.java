/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;

import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.portal.model.MessageNews;

/**
 * 
 * <pre> 
 * 描述：新闻公告 处理接口
 * 构建组：x7
 * 作者:dengyg
 * 邮箱:dengyg@jee-soft.cn
 * 日期:2018-08-20 16:04:35
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface MessageNewsManager extends BaseManager<MessageNews>{

    CommonResult<String> publicMsgNews(String array) throws Exception;
}
