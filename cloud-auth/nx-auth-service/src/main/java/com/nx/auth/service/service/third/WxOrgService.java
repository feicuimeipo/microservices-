/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.third;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nx.api.context.SpringAppUtils;
import com.nx.auth.service.model.bo.WxOrg;
import com.nx.auth.service.model.entity.Org;
import com.nx.auth.service.service.IWXOrgService;
import com.nx.auth.service.service.UserRegisterService;
import com.nx.utils.JsonUtil;
import com.nx.auth.service.constant.WeChatWorkConsts;
import com.nx.auth.service.dao.OrgDao;
import com.nx.auth.service.util.OrgConvertUtil;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WxOrgService implements IWXOrgService {
	
	
	protected Logger logger = LoggerFactory.getLogger(WxOrgService.class);
//	@Resource
//    UCApi orgManager;

	@Autowired
	OrgDao orgDao;

	@Override
	public void create(Org org) {
		WxOrg wxorg = OrgConvertUtil.sysOrgToWxOrg(org);
		
        ObjectNode result=null;
        try{
        	String resultJson = SpringAppUtils.Request.sendHttpsRequest(WeChatWorkConsts.getCreateOrgUrl(), wxorg.toString(), "POST");
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
		String errcode = result.get("errcode").asText();
		if("0".equals(errcode))return;
		
		logger.debug(wxorg.toString());
		// 表示该部门已经存在 跳过
		if("60008".equals(errcode)){
			return;
		}
        // 表示该部门的父部门不存在 跳过
        if("60004".equals(errcode)){
            return;
        }
		throw new RuntimeException(org.getName()+" 添加微信通讯录组织失败 ： "+result.get("errmsg").asText());
		//throw new RuntimeException(org.get("name").asText()+" 添加微信通讯录组织失败 ： "+result.get("errmsg").asText());
	}

	@Override
	public void update(ObjectNode org) {
		WxOrg wxorg = OrgConvertUtil.sysOrgToWxOrg(org);
        ObjectNode result=null;
        try{
        	String resultJson = SpringAppUtils.Request.sendHttpsRequest(WeChatWorkConsts.getUpdateOrgUrl(), wxorg.toString(), "POST");
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        String errcode = result.get("errcode").asText();
		if("0".equals(errcode))return;
		
		throw new RuntimeException(org.get("name").asText()+"添加微信通讯录组织失败 ： "+result.get("errmsg").asText());
	}

	@Override
	public void delete(String orgId) {
        ObjectNode result=null;
        try{
        	String url=WeChatWorkConsts.getDeleteOrgUrl()+orgId;
        	String resultJson = SpringAppUtils.Request.sendHttpsRequest(url, "", "POST");
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

		if("0".equals(result.get("errcode").asText())) return;
		//尚未同步的组织
		if("60003".equals(result.get("errcode").asText())){
			logger.error(orgId+"删除微信通讯录失败 ： "+result.get("errmsg").asText());
			return;
		}
		
		throw new RuntimeException(orgId+"删除微信通讯录失败 ： "+result.get("errmsg").asText());
	}

	@Override
	public void deleteAll(String orgIds) {
		ObjectNode result=null;
		try{
			String delUrl=WeChatWorkConsts.getDeleteAllUserUrl();
	        Map<String,Object> users = new HashMap<>();
			users.put("useridlist", orgIds.split(","));
	            String resultJson = SpringAppUtils.Request.sendHttpsRequest(delUrl,JsonUtil.toJson(users), "POST");
	            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
		if("0".equals(result.get("errcode").asText())) return;
		throw new RuntimeException("批量删除微信通讯录用户失败 ： "+result.get("errmsg").asText());
		
	}
	
	/**
	 * 根据微信组织代码获取该组织下所有成员
	 * @param orgCode 微信组织代码
	 * @return
	 */
	public String getDepartmentUser(String orgCode){
        ObjectNode departmentResult=null;
        try{
        	String departmentUrl = WeChatWorkConsts.getDepartmentUrl(orgCode);
        	String departmentJson = SpringAppUtils.Request.sendHttpsRequest(departmentUrl, "", WeChatWorkConsts.METHOD_GET);
            departmentResult = (ObjectNode) JsonUtil.toJsonNode(departmentJson);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
		if("0".equals(departmentResult.get("errcode").asText())){
			ArrayNode users = departmentResult.putArray("userlist");
			String[] userAccounts= new String[users.size()]; 
			for(int i = 0 ; i<users.size() ; i++){
                ObjectNode user = null;
                try{
                    user = (ObjectNode) JsonUtil.toJsonNode(i);
                }catch (Exception e){
                    logger.error(e.getMessage());
                }

				userAccounts[i] = user.get("userid").asText();
			}
			return StringUtils.join(userAccounts,",");
		}
		throw new RuntimeException("批量删除微信通讯录用户失败 ： "+departmentResult.get("errmsg").asText());
	}
	
//	@Override
//	public void addAll(List<ObjectNode> orgList){
//		for (ObjectNode org : orgList){
//			this.create(org);
//		}
//	}

	@Override
	public void addAll(List<Org> orgList){
		for (Org org : orgList){
			this.create(org);
			try {
				Thread.currentThread().sleep(10);
			}catch (Exception e){}
		}
	}

    @Override
    public void syncAllOrg() {
    	//List<ObjectNode> orgs = orgManager.getOrgsByparentId("1");
		List<Org> orgDTOList = orgDao.getByParentId("1");

		List<Org> orgList = new ArrayList<>();
		if(orgDTOList!=null && orgDTOList.size()>0){
			//addAll(orgs);
            for (Org oriOrg : orgDTOList) {
				Org targetOrg = new Org();
				org.springframework.beans.BeanUtils.copyProperties(oriOrg,targetOrg);
				orgList.add(targetOrg);
                //syncOrgsByParentId(rootOrg.get("id").asText());
				List<Org> childList =  new ArrayList<>();
				syncOrgsByParentId(oriOrg.getId(),childList);
				orgList.addAll(childList);
            }
			addAll(orgList);
        }
    }

	@Autowired
    UserRegisterService userRegisterService;

    public void syncOrgsByParentId(String parentId,List<Org> childList){
		List<Org> orgList = userRegisterService.getOrgListByParentId(parentId);//orgService.getChildOrg(parentId);
    	if(!Collections.isEmpty(orgList)){
    		//addAll(orgs);
    		for (Org dto : orgList) {
				Org obj = new Org();
				org.springframework.beans.BeanUtils.copyProperties(dto,obj);
				childList.add(obj);
    			//syncOrgsByParentId(org.get("id").asText());
				syncOrgsByParentId(dto.getId(),childList);
    		}
    	}
    }

}

