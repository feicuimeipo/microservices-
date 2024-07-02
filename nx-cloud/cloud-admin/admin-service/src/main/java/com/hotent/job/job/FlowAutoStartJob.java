/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.job.job;

import javax.annotation.Resource;

import org.nianxi.api.enums.SystemConstants;
import org.nianxi.x7.api.BpmRuntimeApi;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.nianxi.api.exception.BaseException;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.StringUtil;
import com.hotent.job.model.BaseJob;
import com.hotent.uc.apiimpl.util.ContextUtil;

/**
 * 定时启动流程
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2019年5月28日
 */
public class FlowAutoStartJob extends BaseJob {
	@Resource
	BpmRuntimeApi bpmRuntimeFeignService;

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		String defaultAccount = SystemConstants.SYSTEM_ACCOUNT;
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String startAccount = jobDataMap.getString("startAccount");
		if (StringUtil.isNotEmpty(startAccount)) {
			defaultAccount = startAccount;
		}
		// 定时任务中没有当前登录用户，所以需要设置到当前用户上下文中
		ContextUtil.setCurrentUserByAccount(defaultAccount);

		if (bpmRuntimeFeignService == null) {
			bpmRuntimeFeignService = AppUtil.getBean(BpmRuntimeApi.class);
		}
		// 调用接口启动流程
		ObjectNode startFlowResult = bpmRuntimeFeignService.defAutoStart();
		if (BeanUtils.isNotEmpty(startFlowResult) && !startFlowResult.get("state").asBoolean()) {
			String message = "流程启动失败";
			if (BeanUtils.isNotEmpty(startFlowResult) && BeanUtils.isNotEmpty(startFlowResult.get("message"))) {
				message += ":" + startFlowResult.get("message").asText();
			}
			throw new BaseException(message);
		}
	}

}
