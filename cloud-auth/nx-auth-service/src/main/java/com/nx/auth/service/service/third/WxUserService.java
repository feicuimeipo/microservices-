/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.third;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nx.api.context.SpringAppUtils;
import com.nx.api.model.R;
import com.nx.auth.service.model.bo.WxUser;
import com.nx.auth.service.model.entity.Org;
import com.nx.auth.service.model.entity.User;
import com.nx.auth.service.service.BaseService;
import com.nx.auth.service.service.IUserService;
import com.nx.auth.service.service.IWXOrgService;
import com.nx.auth.service.service.UserRegisterService;
import com.nx.utils.BeanUtils;
import com.nx.utils.JsonUtil;
import com.nx.auth.service.constant.WeChatWorkConsts;
import com.nx.auth.service.dao.OrgDao;
import com.nx.auth.service.dao.OrgUserDao;
import com.nx.auth.service.dao.UserDao;
import com.nx.auth.service.util.OrgConvertUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;


/**
 * 企业微信-用户同步service类
 * @author Administrator
 *
 */
@Component
public class WxUserService implements IUserService {
	private final Log logger = LogFactory.getLog(WxUserService.class);
//	@Resource
//    UCApi userManager;
	@Resource
IWXOrgService iwxOrgService;
//	@Resource
//	PropertyService propertyService;

	@Autowired
	OrgUserDao orgUserDao;



	@Autowired
    BaseService baseService;


	@Autowired
	UserDao userDao;

	@Autowired
    UserRegisterService userRegisterService;



	@Autowired
	OrgDao orgDao;
	@Override
	public void create(ObjectNode user) throws IOException {
		String mobile=user.get("mobile").asText();
		String fullname=user.get("fullname").asText();

		if(StringUtils.isEmpty(mobile))
			throw new RuntimeException(fullname+"添加微信通讯录失败 ：没有填写手机信息 ");

		//获取用户的主部门
		//ArrayNode department = userManager.getOrgListByUserId(user.get("id").asText());
		String uid = user.get("id").asText();
		List<Org> department = orgDao.getOrgMapByUserId(uid);
		if(department == null || BeanUtils.isEmpty(department)){
			user.put("department", "1");
		}else{
			//List<String> arr = new ArrayList<>();
			StringJoiner joiner = new StringJoiner(",","","");
			for(Org node : department){
				//ObjectNode dept= (ObjectNode) node;
				//arr.add(dept.get("id").asText());
				//arr.add(node.getId());
				joiner.add(node.getId());
			}
			user.put("department", joiner.toString());
		}

//		if( this.queryUser(user)==true) {
//			this.update(user);
//			return;
//		}

		WxUser wxUser = OrgConvertUtil.userToWxUser(user);
		if(BeanUtils.isEmpty(wxUser.getDepartment())) return;

		String resultJson = SpringAppUtils.Request.sendHttpsRequest(WeChatWorkConsts.getCreateUserUrl(), wxUser.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)){
			String account=user.get("account").asText();
			//userManager.postUserByAccount(account,"1");
			postUserByAccount(account,"1");
			return;
		}
		// 表示已经存在
		if("60102".equals(errcode)){
			this.update(user);
			return;
		}
		throw new RuntimeException("添加["+fullname+"]到微信通讯录失败 ： "+result.get("errmsg").asText());

	}

	//userManager.postUserByAccount(account,"1");
	private void postUserByAccount(String account,String openId){
		User u = userDao.getByAccount(account);
		u.setWeixin(openId);
		userDao.updateById(u);
	}

	@Override
	public void update(ObjectNode user) throws IOException {
		WxUser wxUser = OrgConvertUtil.userToWxUser(user);

		if(this.queryUser(user)==false)
			throw new RuntimeException(wxUser.getName()+"更新微信通讯录失败 ： 该用户不存在企业微信通讯录");
		//验证存在，即可更新
		String url = WeChatWorkConsts.getUpdateUserUrl();
		String resultJson = SpringAppUtils.Request.sendHttpsRequest(url, wxUser.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);

		if(!"0".equals(result.get("errcode").asText())){
			throw new RuntimeException(wxUser.getName()+"更新微信通讯录失败 ： "+result.get("errmsg").asText());
		}

	}

	@Override
	public void delete(String userId) throws IOException {
		String url=WeChatWorkConsts.getDeleteUserUrl()+userId;
		String resultJson = SpringAppUtils.Request.sendHttpsRequest(url, "", "POST");
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
			if(StringUtils.isEmpty(mobile))
				continue;

			this.create(node);
		}
	}

	public void invite(String wxUserId) throws IOException {
		ObjectNode inviteData= JsonUtil.getMapper().createObjectNode();
		inviteData.put("userid", wxUserId);
		inviteData.put("invite_tips", baseService.getProperty("wx.invite_tips","宏天流程业务平台邀请您关注！"));
		String resultJson = SpringAppUtils.Request.sendHttpsRequest(WeChatWorkConsts.getInviteUserUrl(),inviteData.toString(), "POST");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		if("0".equals(result.get("errcode").asText())) return;
		logger.error("微信邀请失败！"+result.get("errmsg").asText());
	}




	public boolean queryUser(ObjectNode user) throws IOException {
		String account=user.get("account").asText();
		//获取企业微信用户，判断是否存在
		String getUserUrl= WeChatWorkConsts.getUserUrl() + account;
		String resultJsonUser = SpringAppUtils.Request.sendHttpsRequest(getUserUrl, "", "POST");
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
				User user = userDao.selectById(lAryId[i]);
				if (user!=null){
					JsonNode jsonNode = JsonUtil.toJsonNode(user);
					userList.add(jsonNode);
				}

//				CommonResult<UserDTO> ret = userManager.getUserById(lAryId[i]);
//				if (ret!=null && ret.getState() && ret.getValue()!=null){
//					JsonNode jsonNode = JsonUtil.toJsonNode(ret.getValue());
//					userList.add(jsonNode);
//				}
			}
		}else{
			List<User> users =  getAllUser();
			userList =JsonUtil.listToArrayNode(users);
		}
		addAll(userList);
	}

	//全部数据
	public List<User> getAllUser(){
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		return  this.userDao.selectList(queryWrapper);//.selectOne(queryWrapper);
	}

	/**
	 * 将第三方通讯录拉取至本系统
	 * @param lAryId
	 * @throws IOException
	 */
	public void pullUser(String[] lAryId) throws IOException {
		String url = WeChatWorkConsts.getDepartmentListUrl("1");
		String resultJson = SpringAppUtils.Request.sendHttpsRequest(url,"", "GET");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)) {
			JsonNode departmentList = result.get("department");
			for(JsonNode o : departmentList ) {
				ObjectNode node = (ObjectNode) o;
				String orgId = node.get("id").asText();
				Org org = orgDao.selectById(orgId);
				//userManager.getOrgByIdOrCode(orgId);
				if(org == null) {
					//exist = JsonUtil.getMapper().createObjectNode();
					/*
					exist.put("id",node.get("id").asText());
					exist.put("code",node.get("id").asText());
					exist.put("name",node.get("name").asText());
					exist.put("parentId",node.get("parentid").asText());
					exist.put("orderNo",node.get("order").asText());
					*/
					org=new Org();
					org.setId(node.get("id").asText());
					org.setCode(node.get("id").asText());
					org.setName(node.get("name").asText());
					org.setParentId(node.get("parentid").asText());
					org.setOrderNo(Long.valueOf(node.get("order").asText()));

					R<String> orgRsl = userRegisterService.addOrgFromExterUni(org);//;userManager.addOrgFromExterUni(exist);
					if(!orgRsl.isOK()) {
						throw new RuntimeException("写入组织架构失败："+orgRsl.getMsg());
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
		String resultJson = SpringAppUtils.Request.sendHttpsRequest(url,"", "GET");
		JsonNode result = JsonUtil.toJsonNode(resultJson);
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode)) {
			JsonNode userlist = result.get("userlist");
			for(JsonNode o : userlist ) {
				ObjectNode usernode = (ObjectNode) o;
				String userid = BeanUtils.isNotEmpty(usernode.get("userid"))?usernode.get("userid").asText():"";
				if(StringUtils.hasText(userid)) {
					//判断用户是否存在，如果不存在，则创建
					//UserDTO userexist = userManager.getUser(userid,"");
					User user  = userDao.selectById(userid);
					if(user==null) {
						//创建用户
						/*
						ObjectNode newUser = JsonUtil.getMapper().createObjectNode();
						newUser.put("account",usernode.get("userid").asText());
						newUser.put("mobile",usernode.get("mobile").asText());
						newUser.put("fullname",usernode.get("name").asText());
						newUser.put("password","123456");
						*/
						user = new User();
						user.setAccount(usernode.get("userid").asText());
						user.setMobile(usernode.get("mobile").asText());
						user.setFullname(usernode.get("name").asText());
						user.setPassword("123456");
						userRegisterService.addUser(user);
						//userManager.addUserFromExterUni(dto);
						//保存用户与部门的关系
						userRegisterService.addUsersForOrg(orgId,user.getAccount());
					}
				}
			}
		}else {
			throw new RuntimeException("获取部门id["+orgId+"]的成员失败："+result.get("errmsg").asText());
		}
	}


}
