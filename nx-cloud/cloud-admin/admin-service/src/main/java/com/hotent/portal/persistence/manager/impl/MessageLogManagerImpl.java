/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager.impl;

import java.time.LocalDateTime;

import javax.annotation.Resource;


import com.hotent.portal.jms.impl.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.jms.model.JmsActor;
import org.nianxi.jms.model.JmsMessage;
import org.nianxi.jms.model.NoticeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.nianxi.api.exception.WorkFlowException;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import org.nianxi.id.UniqueIdUtil;
import com.hotent.portal.jms.JmsHandler;
import com.hotent.portal.model.MessageLog;
import com.hotent.portal.persistence.dao.MessageLogDao;
import com.hotent.portal.persistence.manager.MessageLogManager;

/**
 * 
 * <pre> 
 * 描述：portal_message_log 处理实现类
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2019-05-30 21:34:36
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service("messageLogManager")
public class MessageLogManagerImpl extends BaseManagerImpl<MessageLogDao, MessageLog> implements MessageLogManager{
	private static Logger log = LoggerFactory.getLogger(MessageLogManagerImpl.class);
	
/*
	@Resource
	MailHandler mailHandler;
*/
/*    @Resource
	InnerHandler innerHandler;*/
    @Resource
	WxEnterpriseHandler wxEnterpriseHandler;
    @Resource
	SmsHandler smsHandler;
    @Resource
	VoiceHandler voiceHandler;
	
	@Override
	public void reinvoke(String id) throws Exception {
		MessageLog messageLog = this.get(id);
		try {
			reinvokeByLog(messageLog);
		} catch (Exception e) {
			int time = BeanUtils.isEmpty(messageLog.getRetryCount())?0:messageLog.getRetryCount().intValue();
			messageLog.setRetryCount(time+1);
			String errorMsg = e.getMessage();
			if(StringUtil.isNotEmpty(errorMsg) && errorMsg.length()>2000){
				messageLog.setException(errorMsg.substring(0, 1999));
			}else{
				messageLog.setException(errorMsg);
			}
			this.update(messageLog);
			throw new WorkFlowException("重调失败：" + ExceptionUtils.getRootCauseMessage(e));
		}
	}

	@Override
	public void signSuccess(String id) {
		MessageLog messageLog = this.get(id);
		if(BeanUtils.isNotEmpty(messageLog)){
			messageLog.setIsSuccess(true);
			this.update(messageLog);
		}
	}
	
	private void reinvokeByLog(MessageLog messageLog) throws Exception{
		
		JmsMessage jmsMessage = JsonUtil.toBean(messageLog.getMsgVo(), JmsMessage.class);
		JmsHandler jmsHandler = null;
		if(NoticeMessageType.WXENTERPRISE.key().equals(jmsMessage.getType())){
			jmsHandler = wxEnterpriseHandler;
		}else if(NoticeMessageType.MAIL.key().equals(jmsMessage.getType())){
			MailHandler mailHandler  = AppUtil.getBean(MailHandler.class);
			jmsHandler = mailHandler;
		}else if(NoticeMessageType.SMS.key().equals(jmsMessage.getType())){
			jmsHandler = smsHandler;
		}else if(NoticeMessageType.VOICE.key().equals(jmsMessage.getType())){
			jmsHandler = voiceHandler;
		}
		if(jmsHandler!=null){
			jmsHandler.send(jmsMessage);
		}
	}

	@Override
	public void handLogByMsgHander(JmsMessage jmsMessage,boolean state,String errorMsg) {
		try {
			MessageLog msgLog = null;
			if(StringUtil.isNotEmpty(jmsMessage.getLogId())){
				msgLog = this.get(jmsMessage.getLogId());
			}
			if(BeanUtils.isEmpty(msgLog)){
				msgLog = new MessageLog();
				msgLog.setId(UniqueIdUtil.getSuid());
				msgLog.setType(jmsMessage.getType());
				jmsMessage.setLogId(msgLog.getId());
				msgLog.setRetryCount(0);
			}else{
				int time = BeanUtils.isEmpty(msgLog.getRetryCount())?0:msgLog.getRetryCount().intValue();
				msgLog.setRetryCount(time+1);
			}
			JmsActor sender = jmsMessage.getSender();
			if(BeanUtils.isNotEmpty(sender)) {
				msgLog.setSenderId(sender.getId());
				msgLog.setSenderName(sender.getName());
			}
			if(BeanUtils.isNotEmpty(jmsMessage.getReceivers())){
				msgLog.setReceivers(JsonUtil.toJson(jmsMessage.getReceivers()));
			}
			msgLog.setSubject(jmsMessage.getSubject());
			msgLog.setContent(jmsMessage.getContent());
			msgLog.setMsgVo(JsonUtil.toJson(jmsMessage));
			msgLog.setCreateTime(LocalDateTime.now());
			msgLog.setIsSuccess(state);
			if(!state){
				if(StringUtil.isNotEmpty(errorMsg) && errorMsg.length()>2000){
					msgLog.setException(errorMsg.substring(0, 1999));
				}else{
					msgLog.setException(errorMsg);
				}
			}
			if(BeanUtils.isNotEmpty(msgLog.getRetryCount()) && msgLog.getRetryCount().intValue()>0){
				this.update(msgLog);
			}else{
				this.create(msgLog);
			}
		} catch (Exception e) {
			log.error("记录消息日志失败："+e.getMessage());
		}
	}
}
