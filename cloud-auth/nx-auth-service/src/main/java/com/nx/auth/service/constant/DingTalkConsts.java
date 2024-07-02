/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.constant;


import com.nx.auth.service.model.entity.SysExternalUnite;
import com.nx.auth.service.service.SysExternalUniteManager;
import com.nx.auth.service.util.DingTalkTokenUtil;
import com.nx.boot.support.AppUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 阿里钉钉常量
 * @author pangquan
 *
 */
public class DingTalkConsts {
	private final static String DT_URL = "https://oapi.dingtalk.com";
	/**
	 * 获取集成信息
	 * @return
	 */
	public static SysExternalUnite getUnite(){
		SysExternalUniteManager manager= AppUtil.getBean(SysExternalUniteManager.class);
		return manager.getDingtalk();
	}
	/**
	 * 获取钉钉用户详细信息
	 * @return
	 */
	public static String getUserUrl() throws IOException {
		String url = DT_URL + "/user/get?access_token=" + DingTalkTokenUtil.getToken() + "&userid=";
		return url;
	}
	public static String getTokenUrl() {
		return DT_URL + "/gettoken?appkey=" + getUnite().getAgentKey() + "&appsecret=" + getUnite().getAgentSecret();
	}
	public static String getCreateUserUrl() throws IOException {
		String url = DT_URL + "/user/create?access_token=" + DingTalkTokenUtil.getToken();
		return url;
	}
	public static String generateMenuUrl(String baseUrl,String corpId) throws UnsupportedEncodingException {
		String redirect_uri = baseUrl + "/home";
		String url = baseUrl+"/dingTalk?corpid="+corpId+"&redirect_uri="+URLEncoder.encode(redirect_uri,"UTF-8");
		return url;
	}
	public static String getUserInfo(String code) throws IOException {
		return DT_URL+"/user/getuserinfo?access_token="+DingTalkTokenUtil.getToken()+"&code="+code;
	}
	
	/**
	 * 发送消息的api地址
	 * @return
	 */
	public static String getMsgSendUrl(){
		return DT_URL +"/topapi/message/corpconversation/asyncsend_v2";
	}
	public static String getAuthorize(String paramStr) {
		return getUnite().getBaseUrl()+"/dingTalk?corpid="+getUnite().getCorpId()+"&params="+paramStr;
	}

}
