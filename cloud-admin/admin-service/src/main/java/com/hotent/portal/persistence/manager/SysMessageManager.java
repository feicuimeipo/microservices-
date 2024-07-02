/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;



import java.util.List;

import com.hotent.portal.params.MessaboxVo;
import io.swagger.annotations.ApiParam;
import com.pharmcube.mybatis.support.manager.BaseManager;
import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.hotent.portal.model.SysExecutor;
import com.hotent.portal.model.SysMessage;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 系统信息处理接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月19日
 */
public interface SysMessageManager extends BaseManager<SysMessage> {

    MessaboxVo getMessBoxInfo(@ApiParam(name = "account", value = "用户账号", required = true) @RequestParam String account) throws Exception;

    /**
	 * 通过用户id获取信息
	 * @param queryFilter 查询参数
	 * @return
	 */
	PageList<SysMessage> getMsgByUserId(QueryFilter queryFilter);

	/**
	 * 处理消息发送
	 * @param sysMessage 系统信息
	 */
	void addMessageSend(SysMessage sysMessage);
	
	/**
	 * 获取最新一条的未读的消息
	 * @param userId 用户id
	 * @return
	 */
	SysMessage getNotReadMsg(String userId);
	
	/**
	 * 获取未读信息数量
	 * @param userId 用户id
	 * @return
	 */
	int getNotReadMsgNum(String userId);
	
	/**
	 * 获取信息的个数
	 * @param currentUserId 当前用户id
	 * @return 
	 */
	int getMsgSize(String receiverId);

	/**
	 * 发送系统消息
	 * @param subject
	 * @param content
	 * @param messageType
	 * @param senderId
	 * @param senderName
	 * @param receivers
	 * @return
	 */
	CommonResult<String> sendMsg(String subject, String content, String messageType, String senderId, String senderName, List<SysExecutor> receivers);
	
}
