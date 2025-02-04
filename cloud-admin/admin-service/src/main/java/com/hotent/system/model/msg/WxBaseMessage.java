/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.system.model.msg;

import org.nianxi.x7.api.PortalApi;

import javax.annotation.Resource;

public abstract class WxBaseMessage {
    @Resource
    PortalApi portalFeignService;
	
	/**
	 * 成员id表示企业账户中的帐号。
	 * 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。
	 * 特殊情况：指定为@all，则向关注该企业应用的全部成员发送。
	 * @all : 表示发送给企业微信中的全体用户。
	 */
	private String touser="";
	/**
	 * 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 */
	private String toparty="";
	/**
	 * 消息类型
	 */
	private String msgtype="";
	
	/**
	 * 企业应用的id，整型。可在应用的设置页面查看
	 */
	protected String agentid;
	
	/**
	 * 表示是否是保密消息，0表示否，1表示是，默认0
	 */
	private String safe="0";
	
	

	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getToparty() {
		return toparty;
	}
	public void setToparty(String toparty) {
		this.toparty = toparty;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getAgentid() {
		return agentid;
	}
	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	public String getSafe() {
		return safe;
	}
	public void setSafe(String safe) {
		this.safe = safe;
	}
	
	
	
	
}
