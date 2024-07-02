/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nianxi.utils.StringUtil;

public class SubSystemUtil {
	
	/**
	 * 获取上下文系统ID
	 * @param req
	 * @return
	 */
	public static String getSystemId(HttpServletRequest req){
		String systemId=CookieUitl.getValueByName("systemId", req);
		if(StringUtil.isEmpty(systemId))return "1";
		return systemId;
	}

	/**
	 * 设置系统id。
	 * @param req
	 * @param response
	 * @param systemId
	 */
	public static void setSystemId(HttpServletRequest req,HttpServletResponse response,String systemId){
		CookieUitl.addCookie("systemId", systemId, true, req, response);
	}
}
