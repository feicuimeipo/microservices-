/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.nianxi.api.model.CommonResult;
import org.nianxi.utils.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import com.pharmcube.mybatis.support.query.FieldRelation;
import com.pharmcube.mybatis.support.query.PageBean;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryField;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.pharmcube.mybatis.support.query.QueryOP;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import com.hotent.portal.model.MessageNews;
import com.hotent.portal.persistence.dao.MessageNewsDao;
import com.hotent.portal.persistence.manager.AuthorityManager;
import com.hotent.portal.persistence.manager.MessageNewsManager;
import com.hotent.uc.apiimpl.util.ContextUtil;
import com.hotent.uc.apiimpl.util.PermissionCalc;
import com.hotent.uc.api.model.IUser;

/**
 * 
 * <pre> 
 * 描述：新闻公告 处理实现类
 * 构建组：x7
 * 作者:dengyg
 * 邮箱:dengyg@jee-soft.cn
 * 日期:2018-08-20 16:04:35
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service("messageNewsManager")
public class MessageNewsManagerImpl extends BaseManagerImpl<MessageNewsDao, MessageNews> implements MessageNewsManager{

	@Resource
	AuthorityManager authorityManager;
	@Resource
	PermissionCalc permssionCalc;
	
	@Override
	public PageList<MessageNews> query(QueryFilter queryFilter){
		List<QueryField> querys = queryFilter.getQuerys();
		boolean isp = false;
		for (QueryField queryField : querys) {
			if("2".equals(queryField.getValue()) && ("FStatus".equals(queryField.getProperty())||"F_status".equals(queryField.getProperty()))){
				isp = true;
				break;
			}
		}
		IUser user = ContextUtil.getCurrentUser();
		if(isp && !user.isAdmin()){
			QueryFilter filter = QueryFilter.build().withPage(new PageBean(1, Integer.MAX_VALUE));
			PageList<MessageNews> pageList = super.query(filter);
			if(pageList.getTotal()>0){
				List<MessageNews> all = pageList.getRows();
				Map<String, Set<String>> authMap = authorityManager.getUserRightMap();
				List<String> authIds = new ArrayList<String>();
				for (MessageNews messageNews : all) {
					try {
						if(StringUtil.isNotEmpty(messageNews.getFCkqxsz())){
							ArrayNode authArray = (ArrayNode) JsonUtil.toJsonNode(messageNews.getFCkqxsz());
							for (JsonNode jsonNode : authArray) {
								if(permssionCalc.hasRight(jsonNode.toString(), authMap)){
									authIds.add(messageNews.getId());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(authIds.size()>1000){
					int sourceSize = authIds.size();
				    int size = (authIds.size() / 1000) + 1;
				    for (int i = 0; i < size; i++) {
				        List<String> subset = new ArrayList<String>();
				        for (int j = i * 1000; j < (i + 1) * 1000; j++) {
				            if (j < sourceSize) {
				                subset.add(authIds.get(j));
				            }
				        }
				        if(subset.size()>0){
				        	queryFilter.addFilter("id", subset, QueryOP.IN, FieldRelation.OR, "group01");
				        }
				    }
				}else if(authIds.size()>0){
					queryFilter.addFilter("id", authIds, QueryOP.IN, FieldRelation.AND, "group01");
				}
			}
		}
		return super.query(queryFilter);
	}


	@Override
	public CommonResult<String> publicMsgNews(String array) throws Exception{
		if(StringUtil.isNotEmpty(array)){
			String[] ids = array.split(",");
			for (String id : ids) {
				MessageNews msgNew = this.get(id);
				if(BeanUtils.isNotEmpty(msgNew)){
					msgNew.setFStatus("2");
					this.update(msgNew);
				}
			}
		}else{
			return new CommonResult<String>(false, "请传入需发布的新闻公告id");
		}
		return new CommonResult<String>(true, "发布成功");
	}
}
