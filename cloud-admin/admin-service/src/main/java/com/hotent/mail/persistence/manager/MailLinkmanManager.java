/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.mail.persistence.manager;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.mail.model.MailLinkman;

import java.util.List;


/**
 * 外部邮件最近联系 处理接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月7日
 */
public interface MailLinkmanManager extends BaseManager<MailLinkman>{
	
	/**
	 * 根据邮箱地址找到联系人
	 * @param address 邮箱地址
	 * @param userId  用户id
	 * @return		     返回邮箱联系人
	 * @throws Exception
	 */
	public MailLinkman findLinkMan(String address, String userId)throws Exception;
	
	/**
	 * 找到当前用户下的最近联系人
	 * @param userId    用户id
	 * @param condition 当前环境
	 * @return			返回联系人集合
	 */
	public List<MailLinkman> getAllByUserId(String userId, String condition);
	
	/**
	 * 根据联系人名称或邮箱地址模糊查询
	 * @param query
	 * @return
	 */
	public List<MailLinkman> queryByAll(String query);
	
}
