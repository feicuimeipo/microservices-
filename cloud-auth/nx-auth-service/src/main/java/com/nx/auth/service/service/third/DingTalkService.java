/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.third;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nx.api.context.SpringAppUtils;
import com.nx.auth.service.model.bo.WxUser;
import com.nx.auth.service.model.entity.User;
import com.nx.auth.service.service.IUserService;
import com.nx.utils.BeanUtils;
import com.nx.utils.JsonUtil;
import com.nx.auth.service.constant.DingTalkConsts;
import com.nx.auth.service.dao.UserDao;
import com.nx.auth.service.util.OrgConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 * 阿里钉钉，用户同步类
 * 日期：2019-12-03
 * @author Pangquan
 * </pre>
 */
@Component
public class DingTalkService implements IUserService {


	@Override
	public void create(ObjectNode user) throws IOException {
		String mobile=user.get("mobile").asText();
		String fullname=user.get("fullname").asText();
		if(StringUtils.isEmpty(mobile))
			throw new RuntimeException(fullname+"添加钉钉通讯录失败 ：没有填写手机信息 ");

		if( this.queryUser(user)==true) {
			return;
		}

		WxUser wxUser = OrgConvertUtil.userToWxUser(user);
		if(BeanUtils.isEmpty(wxUser.getDepartment())) return;


		String resultJson = SpringAppUtils.Request.sendHttpsRequest(DingTalkConsts.getCreateUserUrl(), wxUser.toString(), "POST","application/json");
		//String resultJson = DingTalkHttpUtil.sendHttpsRequest(DingTalkConsts.getCreateUserUrl(), wxUser.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)){
			return;
		}
		// 表示已经存在
		if("60102".equals(errcode)){
			return;
		}
		throw new RuntimeException("添加["+fullname+"]到钉钉通讯录失败 ： "+resultJson);
	}


	public boolean queryUser(ObjectNode user) throws IOException {
		String account=user.get("account").asText();
		//获取企业微信用户，判断是否存在
		String getUserUrl= DingTalkConsts.getUserUrl() + account;
		String resultJsonUser =  SpringAppUtils.Request.sendHttpsRequest(getUserUrl, "", "GET");
		JsonNode userJson = JsonUtil.toJsonNode(resultJsonUser);

		if("0".equals(userJson.get("errcode").asText())){
			return true;
		}
		return false;
	}
	@Override
	public void update(ObjectNode user) throws IOException {


	}

	@Override
	public void delete(String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(String userIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(JsonNode users) throws IOException {
		for (JsonNode user : users){
			ObjectNode node= (ObjectNode) user;
			//SysUser u =(SysUser) user;
			// u.getHasSyncToWx() ==1
			String mobile=user.get("mobile").asText();
			if(StringUtils.isEmpty(mobile))
				continue;

			this.create(node);
		}

	}

	@Autowired
	UserDao userDao;
	@Override
	public void syncUser(String[] lAryId) throws IOException {
		ArrayNode userList = JsonUtil.getMapper().createArrayNode();
//		iwxOrgService.syncAllOrg();//不同步组织结构，把用户都挂在顶级的组织上
		if(BeanUtils.isNotEmpty(lAryId)){
			for (int i = 0; i < lAryId.length; i++) {
				//ObjectNode user = (ObjectNode) userManager.getUserById(lAryId[i]).getValue();
				//CommonResult<UserDTO> ret = userD.getUserById(lAryId[i]);
				User user = userDao.selectById(lAryId[i]);
				if (user!=null){
					JsonNode jsonNode = JsonUtil.toJsonNode(user);
					userList.add(jsonNode);
				}
				//CommonResult<UserDTO> user = userManager.getUserById(lAryId[i]).getValue();;
				//if(user != null) userList.add(user);
			}
		}else{
			List<User> list = userDao.selectList(Wrappers.emptyWrapper());
			userList = JsonUtil.listToArrayNode(list);
			//userList = (ArrayNode) userManager.getAllUser();
		}
		addAll(userList);
	}

}
