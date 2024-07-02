/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.job.job;

import com.hotent.job.model.BaseJob;
import com.hotent.uc.apiimpl.util.ContextUtil;
import org.nianxi.api.enums.SystemConstants;
import org.nianxi.api.exception.BaseException;
import org.nianxi.api.model.CommonResult;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.StringUtil;
import org.nianxi.x7.api.BpmModelApi;
import org.quartz.JobExecutionContext;


/**
 * 任务催办定时计划
 * @author liyg
 */
public class TaskReminderJob extends BaseJob{
	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		BpmModelApi bean = AppUtil.getBean(BpmModelApi.class);
		String defaultAccount = SystemConstants.SYSTEM_ACCOUNT;
		// 定时任务中没有当前登录用户，所以需要设置到当前用户上下文中
		ContextUtil.setCurrentUserByAccount(defaultAccount);
		CommonResult<String> executeTaskReminderJob = bean.executeTaskReminderJob();
		if(!executeTaskReminderJob.getState()){
			throw new BaseException(StringUtil.isNotEmpty(executeTaskReminderJob.getMessage())?executeTaskReminderJob.getMessage():"执行催办任务失败");
		}
	}
	
}
