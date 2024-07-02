/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.api;

import com.hotent.sys.persistence.manager.SysLogsManager;
import com.pharmcube.actionlogger.ActionLog;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Schema;
import org.nianxi.api.model.CommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;


/**
 * /syslog/v1
 */
@RestController
@RequestMapping(value="/syslog/v1")
@Api(tags="系统日志")
public class SysLogApController {
	@Resource
	SysLogsManager sysLogsManager;

	//add by nianxiaoling
	@PostMapping(value="")
	public CommonResult<String> save(@Schema(name="recordActionLog",description="系统操作日志业务对象", required = true)@RequestBody ActionLog actionLog) throws Exception{
		SysLogs sysLogs = actionLog.getData();
		String msg = "添加系统操作日志成功";
		if(StringUtil.isEmpty(sysLogs.getId())){
			sysLogsManager.create(sysLogs);
		}else{
			sysLogsManager.update(sysLogs);
			msg = "更新系统操作日志成功";
		}
		return new CommonResult<String>(msg);
	}



}
