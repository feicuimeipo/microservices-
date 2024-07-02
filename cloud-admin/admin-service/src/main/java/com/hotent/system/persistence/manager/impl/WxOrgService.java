/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.system.persistence.manager.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.uc.apiimpl.model.Org;
import org.nianxi.x7.api.UCApi;
import org.nianxi.utils.BeanUtils;
import org.nianxi.boot.support.HttpUtil;
import org.nianxi.utils.JsonUtil;
import com.hotent.system.consts.WeChatWorkConsts;
import com.hotent.system.model.WxOrg;
import com.hotent.system.persistence.manager.IWXOrgService;
import com.hotent.system.util.OrgConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.nianxi.x7.api.dto.uc.OrgDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class WxOrgService implements IWXOrgService {
	
	
	protected Logger logger = LoggerFactory.getLogger(WxOrgService.class);
	@Resource
    UCApi orgManager;

	@Override
	public void create(Org org) {
		WxOrg wxorg = OrgConvertUtil.sysOrgToWxOrg(org);
		
        ObjectNode result=null;
        try{
        	String resultJson = HttpUtil.sendHttpsRequest(WeChatWorkConsts.getCreateOrgUrl(), wxorg.toString(), "POST");
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
        	String resultJson = HttpUtil.sendHttpsRequest(WeChatWorkConsts.getUpdateOrgUrl(), wxorg.toString(), "POST");
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
        	String resultJson = HttpUtil.sendHttpsRequest(url, "", "POST");
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
	            String resultJson = HttpUtil.sendHttpsRequest(delUrl,JsonUtil.toJson(users), "POST");
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
        	String departmentJson = HttpUtil.sendHttpsRequest(departmentUrl, "", WeChatWorkConsts.METHOD_GET);
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
		List<OrgDTO> orgDTOList = orgManager.getOrgsByparentId("1");
		List<Org> orgList = new ArrayList<>();
		if(orgDTOList!=null && orgDTOList.size()>0){
			//addAll(orgs);
            for (OrgDTO rootOrg : orgDTOList) {
				Org obj = new Org();
				org.springframework.beans.BeanUtils.copyProperties(rootOrg,obj);
				orgList.add(obj);
                //syncOrgsByParentId(rootOrg.get("id").asText());
				List<Org> childList =  new ArrayList<>();
				syncOrgsByParentId(rootOrg.getId(),childList);
				orgList.addAll(childList);
            }
			addAll(orgList);
        }
    }

    public void syncOrgsByParentId(String parentId,List<Org> childList){
		List<OrgDTO> orgs = orgManager.getChildOrg(parentId);
    	if(BeanUtils.isNotEmpty(orgs) && orgs.size()>0){
    		//addAll(orgs);
    		for (OrgDTO dto : orgs) {
				Org obj = new Org();
				org.springframework.beans.BeanUtils.copyProperties(dto,obj);
				childList.add(obj);
    			//syncOrgsByParentId(org.get("id").asText());
				syncOrgsByParentId(dto.getId(),childList);
    		}
    	}
    }

}

