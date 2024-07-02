/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.mail.persistence.manager.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.file.persistence.FilePersistenceFactory;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import com.hotent.file.model.DefaultFile;
import com.hotent.file.util.AppFileUtil;
import com.hotent.mail.model.MailAttachment;
import com.hotent.mail.persistence.dao.MailAttachmentDao;
import com.hotent.mail.persistence.manager.MailAttachmentManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 外部邮件附件表 处理实现类
 * 
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月6日
 */
@Service("mailAttachmentManager")
public class MailAttachmentManagerImpl extends BaseManagerImpl<MailAttachmentDao, MailAttachment> implements MailAttachmentManager{
	@Resource
	FilePersistenceFactory fileManager;
	
	@Override
	public List<MailAttachment> getByMailId(String mailId) {
		return baseMapper.getByMailId(mailId);
	}
	
	@Override
	public void updateFilePath(String fileName, String mailId, String filePath) {
		baseMapper.updateFilePath(fileName, mailId, filePath);
	}
	
	@Override
	public List<MailAttachment> getByOutMailFileIds(String fileIds) throws Exception {
		List<MailAttachment> result = new ArrayList<MailAttachment>();
		if(StringUtil.isEmpty(fileIds)) return result;
		JsonNode jsonNode = JsonUtil.toJsonNode(fileIds);
		for(Object obj:jsonNode){
			ObjectNode json = (ObjectNode)obj;
			String id = json.get("id").textValue();
			DefaultFile file= fileManager.getFileManager().get(id);
			String filePath = AppFileUtil.getBasePath()+File.separator+file.getFilePath();
			MailAttachment attachment = new MailAttachment();
			attachment.setId(id);
			attachment.setFileName(json.get("name").textValue());
			attachment.setFilePath(filePath);
			result.add(attachment);
		}
		return result;
	}
	
	@Override
	public void delByMailId(String mailId) {
		baseMapper.delByMailId(mailId);
	}
}
