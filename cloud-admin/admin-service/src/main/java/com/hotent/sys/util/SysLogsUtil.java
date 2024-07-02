/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.util;

import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.StringUtil;
import org.nianxi.utils.time.DateUtil;
import com.hotent.sys.persistence.manager.SysLogsManager;
import com.hotent.sys.persistence.model.SysLogs;

/**
 * 系统日志添加工具类
 * @author liyanggui
 *
 */
public class SysLogsUtil {
	public static void addSysLogs(String id,String opeName,String executor,String ip,String logType,String moduleType,String reqUrl,String opeContent){
		SysLogsManager sysLogsManager = AppUtil.getBean(SysLogsManager.class);
		if(StringUtil.isEmpty(executor) || executor.startsWith("null") ){
			executor = "系统[无用户登录系统]";
		}
		// 对于操作内容的记录设置最多记录3999
		if(StringUtil.isNotEmpty(opeContent) && opeContent.length() > 3999) {
			opeContent = opeContent.substring(0, 3999);
		}
		SysLogs sysLogs = new SysLogs(opeName, DateUtil.getCurrentDate(), executor, ip, logType, moduleType,reqUrl, opeContent);
		sysLogs.setId(id);
		if(StringUtil.isNotEmpty(id)) {
			SysLogs isExist = sysLogsManager.get(id);
			if(BeanUtils.isEmpty(isExist)) {
				sysLogsManager.create(sysLogs);
			}else {
				sysLogsManager.update(sysLogs);
			}
		}else {			
			sysLogsManager.create(sysLogs);
		}
		
	}
}
