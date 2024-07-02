/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;




import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.portal.model.MessageReceiver;


/**
 * 系统信息接收处理接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author hugh
 * @email zxh@jee-soft.cn
 * @date 2018年6月21日
 */
public interface MessageReceiverManager extends BaseManager<MessageReceiver>{

	/*Map<String, String> getMsgType();*/
	
	/**
	 * 更新读取状态
	 * @param lAryId 信息id
	 */
	void updateReadStatus(String[] lAryId);

	
}
