/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.mail.persistence.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.mail.model.MailAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外部邮件附件表 DAO接口
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月6日
 */
public interface MailAttachmentDao extends BaseMapper<MailAttachment> {
	/**
	 * 根据邮箱邮件ID获取附件
	 * @param mailId 邮箱ID
	 * @return		  返回邮箱附件
	 */
	List<MailAttachment> getByMailId(String mailId);
	
	/**
	 * 更新文件路径
	 * @param fileName 文件名称
	 * @param mailId   邮箱ID
	 * @param filePath 文件路径
	 */
	void updateFilePath(@Param("fileName")String fileName, @Param("mailId")String mailId, @Param("filePath")String filePath);
	
	/**
	 * 根据邮件ID删除附件
	 * @param mailId 邮箱ID
	 */
	void delByMailId(String mailId);
}
