/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.system.persistence.manager.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.base.service.PropertyService;
import com.hotent.system.consts.WeChatWorkConsts;
import com.hotent.system.model.WxUser;
import com.hotent.system.persistence.manager.IUserService;
import com.hotent.system.persistence.manager.IWXOrgService;
import com.hotent.system.util.OrgConvertUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nianxi.api.model.CommonResult;
import org.nianxi.boot.support.HttpUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import org.nianxi.x7.api.UCApi;
import org.nianxi.x7.api.dto.uc.OrgDTO;
import org.nianxi.x7.api.dto.uc.UserDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 企业微信-用户同步service类
 * @author Administrator
 *
 */
@Component
public class WxUserService implements IUserService {
	private final Log logger = LogFactory.getLog(WxUserService.class);
	@Resource
    UCApi userManager;
	@Resource
	IWXOrgService iwxOrgService;
	@Resource
	PropertyService propertyService;
	
	@Override
	public void create(ObjectNode user) throws IOException {
		String mobile=user.get("mobile").asText();
		String fullname=user.get("fullname").asText();
		if(StringUtil.isEmpty(mobile))
			throw new RuntimeException(fullname+"添加微信通讯录失败 ：没有填写手机信息 ");

		//获取用户的主部门
		//ArrayNode department = userManager.getOrgListByUserId(user.get("id").asText());
		List<OrgDTO> department = userManager.getOrgListByUserId(user.get("id").asText());
		if(department == null || BeanUtils.isEmpty(department)){
			user.put("department", "1");
		}else{
			List<String> arr = new ArrayList<>();
			for(OrgDTO node : department){
				//ObjectNode dept= (ObjectNode) node;
				//arr.add(dept.get("id").asText());
				arr.add(node.getId());
			}
			user.put("department",StringUtil.join(arr, ","));
		}

		if( this.queryUser(user)==true) {
			this.update(user);
			return;
		}

		WxUser wxUser = OrgConvertUtil.userToWxUser(user);
		if(BeanUtils.isEmpty(wxUser.getDepartment())) return;

		String resultJson = HttpUtil.sendHttpsRequest(WeChatWorkConsts.getCreateUserUrl(), wxUser.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);

		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)){
			String account=user.get("account").asText();
			userManager.postUserByAccount(account,"1");
			return;
		}
		// 表示已经存在
		if("60102".equals(errcode)){
			this.update(user);
			return;
		}
		throw new RuntimeException("添加["+fullname+"]到微信通讯录失败 ： "+result.get("errmsg").asText());

	}

	@Override
	public void update(ObjectNode user) throws IOException {
		WxUser wxUser = OrgConvertUtil.userToWxUser(user);

		if(this.queryUser(user)==false)
			throw new RuntimeException(wxUser.getName()+"更新微信通讯录失败 ： 该用户不存在企业微信通讯录");
		//验证存在，即可更新
		String url = WeChatWorkConsts.getUpdateUserUrl();
		String resultJson = HttpUtil.sendHttpsRequest(url, wxUser.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);

		if(!"0".equals(result.get("errcode").asText())){
			throw new RuntimeException(wxUser.getName()+"更新微信通讯录失败 ： "+result.get("errmsg").asText());
		}

	}

	@Override
	public void delete(String userId) throws IOException {
		String url=WeChatWorkConsts.getDeleteUserUrl()+userId;
		String resultJson = HttpUtil.sendHttpsRequest(url, "", "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		if("0".equals(result.get("errcode").asText())) return;

		throw new RuntimeException(userId+"删除微信通讯录失败 ： "+result.get("errmsg").asText());
	}

	@Override
	public void deleteAll(String userIds) throws IOException {

	}

	@Override
	public void addAll(JsonNode users) throws IOException {
		for (JsonNode user : users){
			ObjectNode node= (ObjectNode) user;
			//SysUser u =(SysUser) user;
			// u.getHasSyncToWx() ==1
			String mobile=user.get("mobile").asText();
			if("null".equals(mobile)){
				continue;
			}
			if(StringUtil.isEmpty(mobile))
				continue;

			this.create(node);
		}
	}

	public void invite(String wxUserId) throws IOException {
		ObjectNode inviteData= JsonUtil.getMapper().createObjectNode();
		inviteData.put("userid", wxUserId);
		inviteData.put("invite_tips", propertyService.getProperty("wx.invite_tips","宏天流程业务平台邀请您关注！"));
		String resultJson = HttpUtil.sendHttpsRequest(WeChatWorkConsts.getInviteUserUrl(),inviteData.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		if("0".equals(result.get("errcode").asText())) return;
		logger.error("微信邀请失败！"+result.get("errmsg").asText());
	}


	public boolean queryUser(ObjectNode user) throws IOException {
		String account=user.get("account").asText();
		//获取企业微信用户，判断是否存在
		String getUserUrl= WeChatWorkConsts.getUserUrl() + account;
		String resultJsonUser = HttpUtil.sendHttpsRequest(getUserUrl, "", "POST");
		JsonNode userJson = JsonUtil.toJsonNode(resultJsonUser);

		if("0".equals(userJson.get("errcode").asText())){
			return true;
		}
		return false;
	}


	@Override
	public void syncUser(String[] lAryId) throws IOException {
		ArrayNode userList = JsonUtil.getMapper().createArrayNode();
		iwxOrgService.syncAllOrg();
		if(BeanUtils.isNotEmpty(lAryId)){
			for (int i = 0; i < lAryId.length; i++) {
				//ObjectNode user = (ObjectNode) userManager.getUserById(lAryId[i]).getValue();
				//if(user != null) userList.add(user);
				CommonResult<UserDTO> ret = userManager.getUserById(lAryId[i]);
				if (ret!=null && ret.getState() && ret.getValue()!=null){
					JsonNode jsonNode = JsonUtil.toJsonNode(ret.getValue());
					userList.add(jsonNode);
				}
			}
		}else{
			userList = (ArrayNode) userManager.getAllUser();
		}
		addAll(userList);
	}
	/**
	 * 将第三方通讯录拉取至本系统
	 * @param lAryId
	 * @throws IOException
	 */
	public void pullUser(String[] lAryId) throws IOException {
		String url = WeChatWorkConsts.getDepartmentListUrl("1");
		String resultJson = HttpUtil.sendHttpsRequest(url,"", "GET");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)) {
			JsonNode departmentList = result.get("department");
			for(JsonNode o : departmentList ) {
				ObjectNode node = (ObjectNode) o;
				String orgId = node.get("id").asText();
				OrgDTO exist = userManager.getOrgByIdOrCode(orgId);
				if(exist == null) {
					//exist = JsonUtil.getMapper().createObjectNode();
					/*
					exist.put("id",node.get("id").asText());
					exist.put("code",node.get("id").asText());
					exist.put("name",node.get("name").asText());
					exist.put("parentId",node.get("parentid").asText());
					exist.put("orderNo",node.get("order").asText());
					*/
					exist=new OrgDTO();
					exist.setId(node.get("id").asText());
					exist.setCode(node.get("id").asText());
					exist.setName(node.get("name").asText());
					exist.setParentId(node.get("parentid").asText());
					exist.setOrderNo(Long.valueOf(node.get("order").asText()));
					CommonResult<String> orgRsl = userManager.addOrgFromExterUni(exist);
					if(!orgRsl.getState()) {
						throw new RuntimeException("写入组织架构失败："+orgRsl.getMessage());
					}
				}
				this.pullUserByDepartmentId(orgId);
			}
		}else {
			throw new RuntimeException("获取企业微信通讯录的组织架构失败："+result.get("errmsg").asText());
		}
	}

	private void pullUserByDepartmentId(String orgId) throws IOException {
		String url = WeChatWorkConsts.getUsersByDepartmentId(orgId,"0");
		String resultJson = HttpUtil.sendHttpsRequest(url,"", "GET");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)) {
			JsonNode userlist = result.get("userlist");
			for(JsonNode o : userlist ) {
				ObjectNode usernode = (ObjectNode) o;
				String userid = BeanUtils.isNotEmpty(usernode.get("userid"))?usernode.get("userid").asText():"";
				if(StringUtil.isNotEmpty(userid)) {
					//判断用户是否存在，如果不存在，则创建
					UserDTO userexist = userManager.getUser(userid,"");
					if(BeanUtils.isEmpty(userexist)) {
						//创建用户
						/*
						ObjectNode newUser = JsonUtil.getMapper().createObjectNode();
						newUser.put("account",usernode.get("userid").asText());
						newUser.put("mobile",usernode.get("mobile").asText());
						newUser.put("fullname",usernode.get("name").asText());
						newUser.put("password","123456");
						*/
						UserDTO dto = new UserDTO();
						dto.setAccount(usernode.get("userid").asText());
						dto.setMobile(usernode.get("mobile").asText());
						dto.setFullname(usernode.get("name").asText());
						dto.setPassword("123456");
						userManager.addUserFromExterUni(dto);
						//保存用户与部门的关系
						userManager.addUsersForOrg(orgId,usernode.get("userid").asText());
					}
				}
			}
		}else {
			throw new RuntimeException("获取部门id["+orgId+"]的成员失败："+result.get("errmsg").asText());
		}
	}


}
