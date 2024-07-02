/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager.impl;



import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import org.nianxi.utils.BeanUtils;
import com.hotent.portal.model.MessageReceiver;
import com.hotent.portal.persistence.dao.MessageReceiverDao;
import com.hotent.portal.persistence.manager.MessageReadManager;
import com.hotent.portal.persistence.manager.MessageReceiverManager;
import com.hotent.uc.apiimpl.util.ContextUtil;
import com.hotent.uc.api.model.IUser;

/**
 * 系统信息处理实现类
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月21日
 */
@Service("messageReceiverManager")
public class MessageReceiverManagerImpl extends BaseManagerImpl<MessageReceiverDao, MessageReceiver> implements MessageReceiverManager{
	@Resource
	MessageReadManager messageReadManager;
	
	@Override
	public void updateReadStatus(String[] lAryId) {
		if (lAryId.length==0) return;
		IUser currentUser = ContextUtil.getCurrentUser();
		for (String id :lAryId ){
			MessageReceiver messageReceiver = this.get(id);
			if (BeanUtils.isEmpty(messageReceiver)) continue;
			messageReadManager.addMessageRead(messageReceiver.getMsgId(),currentUser);
			
		}
	}
}
