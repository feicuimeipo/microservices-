/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.dao;


import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.portal.model.MessageRead;

/**
 * 系统读取消息 DAO处理接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月21日
 */
public interface MessageReadDao extends BaseMapper<MessageRead> {
	
	/**
	 * 通过用户获取信息读取
	 * @param params map参数
	 * @return
	 */
	MessageRead getReadByUser(Map params);
	
	/**
	 * 通过信息id获取信息读取实体
	 * @param messageId 信息id
	 * @return
	 */
	List<MessageRead> getByMessageId(String messageId);

	
}
