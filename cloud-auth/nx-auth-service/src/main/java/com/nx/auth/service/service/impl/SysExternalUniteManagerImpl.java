/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.nx.api.context.SpringAppUtils;
import com.nx.auth.service.constant.ExterUniEnum;
import com.nx.auth.service.model.entity.SysExternalUnite;
import com.nx.auth.service.service.SysExternalUniteManager;
import com.nx.auth.service.service.third.DingTalkService;
import com.nx.auth.service.service.third.WxUserService;
import com.nx.mybatis.support.manager.impl.BaseManagerImpl;
import com.nx.utils.BeanUtils;
import com.nx.utils.JsonUtil;
import com.nx.auth.service.constant.WeChatWorkConsts;
import com.nx.auth.service.dao.SysExternalUniteDao;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <pre>
 * 描述：系统第三方集成 处理实现类
 * 构建组：x5-bpmx-platform
 * 作者:PangQuan
 * 邮箱:PangQuan@jee-soft.cn
 * 日期:2019-11-26 16:07:01
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service("sysExternalUniteManager")
public class SysExternalUniteManagerImpl extends BaseManagerImpl<SysExternalUniteDao, SysExternalUnite> implements SysExternalUniteManager {

	@Resource
    WxUserService systemUserService;
	@Resource
	DingTalkService dtUserService;
	@Resource
	SysExternalUniteDao sysExternalUniteDao;

	@Override
	public boolean isTypeExists(String type,String id) {
		Map<String,Object> map=new HashMap<>();
		map.put("type",type);
		map.put("id",id);
		return BeanUtils.isNotEmpty(sysExternalUniteDao.isTypeExists(map));
	}
	/**
	 * 获取企业微信的集成信息
	 */
	@Override
	public SysExternalUnite getWechatWork(){
		return sysExternalUniteDao.getOneByType(ExterUniEnum.WeChatWork.getKey());
	}
	/**
	 * 获取阿里钉钉的集成信息
	 */
	@Override
	public SysExternalUnite getDingtalk(){
		return sysExternalUniteDao.getOneByType(ExterUniEnum.Dingtalk.getKey());
	}
	/**
	 * 获取微信公众号的集成信息
	 */
	@Override
	public SysExternalUnite getWeChatOfficialAccounts(){
		return sysExternalUniteDao.getOneByType(ExterUniEnum.WeChatOfficialAccounts.getKey());
	}

	/**
	 * 将本系统的用户同步到第三方平台
	 */
	@Override
	public void syncUser(String uniteId) throws IOException {
		SysExternalUnite entity = this.get(uniteId);
		if(BeanUtils.isEmpty(entity)){
			throw new RuntimeException("查无此集成信息");
		}
		//企业微信
		if(ExterUniEnum.WeChatWork.getKey().equals(entity.getType())){
			systemUserService.syncUser(null);
		}else if(ExterUniEnum.Dingtalk.getKey().equals(entity.getType())){
			dtUserService.syncUser(null);
		}
	}
	@Override
	public void saveAgent(SysExternalUnite unite) throws IOException {
		//企业微信
		this.update(unite);
		//保存并且发布到第三方
		if("1".equals(unite.getIsPublish())){
			if(ExterUniEnum.WeChatWork.getKey().equals(unite.getType())){
				String url = WeChatWorkConsts.getCreateAgentMenuUrl(unite.getAgentId());
				//临时写法
				String button = "{\"button\":[{\"type\":\"view\",\"name\":\"$name\",\"url\":\"$url\"}]}";
				button = button.replace("$name", unite.getMenuName()).replace("$url", unite.getMenuUrl());
				String rtn= SpringAppUtils.Request.sendHttpsRequest(url, button, "POST");
				JsonNode jsonObj = JsonUtil.toJsonNode(rtn);
				System.out.println(jsonObj);
				//{"errcode":0,"errmsg":"ok"}
				if(!"0".equals(jsonObj.get("errcode").asText())){
					throw new RuntimeException("发布菜单失败："+jsonObj.get("errmsg").asText());
				}
			}
		}
	}
	/**
	 * 将第三方通讯录同步至本系统
	 * @throws IOException
	 */
	@Override
	public void pullUser(String uniteId) throws IOException {
		SysExternalUnite entity = this.get(uniteId);
		if(BeanUtils.isEmpty(entity)){
			throw new RuntimeException("查无此集成信息");
		}
		//企业微信
		if(ExterUniEnum.WeChatWork.getKey().equals(entity.getType())){
			systemUserService.pullUser(null);
		}
	}
}
