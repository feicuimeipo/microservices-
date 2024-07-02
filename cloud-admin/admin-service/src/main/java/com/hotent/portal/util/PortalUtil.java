/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.util;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.nianxi.utils.FileUtil;
import com.hotent.uc.api.model.IUser;

/**
 * 当前用户皮肤管理工具类
 * @author mikel
 */
public class PortalUtil {
	
	private static final String CURRENT_SKIN = "current_skin";
	
	/**
	 * 取得当前用户皮肤
	 * @return
	 * @throws Exception 
	 */
	public static String getCurrentUserSkin (IUser user) {
	return PortalUtil.CURRENT_SKIN;
	}

	/**
	 * 取得当前用户皮肤
	 * @return
	 * @throws Exception 
	 */
	public static String setCurrentUserSkin(HttpServletRequest request) {
		return PortalUtil.CURRENT_SKIN;
	}
	
	/**
	 * 获取模板路径。
	 * @return
	 */
	public static String getTemplatePath(){
		return FileUtil.getClassesPath() +"template" + File.separator;
	}
	
	/**
	 * 获取首页模板路径。
	 * @return
	 */
	public static String getIndexTemplatePath(){
		return getTemplatePath() + File.separator+"index"+File.separator;
	}
}
