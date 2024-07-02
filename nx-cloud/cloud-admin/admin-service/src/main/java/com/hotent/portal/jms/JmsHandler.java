/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.jms;

import org.nianxi.jms.model.JmsMessage;

/**
 * 发送消息处理接口
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年10月9日
 */
public interface JmsHandler{
	/**
	 * 消息类型
	 * @return
	 */
	String getType();
	/**
	 * 标题
	 * @return String
	 */
	String getTitle();
	/**
	 * 是否默认处理器
	 * @return   boolean
	 */
	boolean getIsDefault();
	/**
	 * 是否支持html
	 * @return   boolean
	 */
	boolean getSupportHtml();
	/**
	 * 处理jms消息，如发送、持久化等操作。
	 * @param 	jmsMessage
	 * @return  boolean
	 */
	boolean send(JmsMessage jmsMessage);
}
