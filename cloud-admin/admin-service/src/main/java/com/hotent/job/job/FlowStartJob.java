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
import org.nianxi.x7.api.dto.bpm.runtime.StartFlowParamObjectDTO;
import org.nianxi.x7.api.dto.bpm.runtime.StartResultDTO;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import org.nianxi.api.exception.BaseException;
import org.nianxi.x7.api.BpmRuntimeApi;
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
public class FlowStartJob extends BaseJob{
	@Resource
	BpmRuntimeApi bpmRuntimeFeignService;

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		String defaultAccount = SystemConstants.SYSTEM_ACCOUNT;
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String flowkey = jobDataMap.getString("flowkey");
		String startAccount = jobDataMap.getString("startAccount");
		if(StringUtil.isNotEmpty(startAccount)) {
			defaultAccount = startAccount;
		}
		// 定时任务中没有当前登录用户，所以需要设置到当前用户上下文中
		ContextUtil.setCurrentUserByAccount(defaultAccount);
		if(BeanUtils.isEmpty(flowkey)) {
			throw new BaseException("定时启动流程的自动任务中必须配置flowkey来指定要启动的流程.");
		}
		startFlow(flowkey, defaultAccount);
	}

	private void startFlow(String flowKey, String account) throws Exception {
		// 构建启动流程的参数
/*		ObjectNode startFlowParam = JsonUtil.getMapper().createObjectNode();
		startFlowParam.put("flowKey", flowKey);
		startFlowParam.put("account", account);
		if(bpmRuntimeFeignService==null) {
			bpmRuntimeFeignService = AppUtil.getBean(BpmRuntimeApi.class);
		}*/
		// 调用接口启动流程

		StartFlowParamObjectDTO dto = new StartFlowParamObjectDTO();
		dto.setFlowKey(flowKey);
		dto.setAccount(account);

		StartResultDTO startFlowResult = bpmRuntimeFeignService.start(dto);

		//if(BeanUtils.isNotEmpty(startFlowResult) && !startFlowResult.get("state").asBoolean()) {
		if(BeanUtils.isNotEmpty(startFlowResult) && !startFlowResult.getState()) {
			String message = "流程启动失败";
			//if(BeanUtils.isNotEmpty(startFlowResult) && BeanUtils.isNotEmpty(startFlowResult.get("message"))) {
			if(BeanUtils.isNotEmpty(startFlowResult) && BeanUtils.isNotEmpty(startFlowResult.getMessage())) {
				message += ":" +  startFlowResult.getMessage();//startFlowResult.get("message").asText();
				//message += ":" + startFlowResult.get("message").asText();
			}
			throw new BaseException(message);
		}
	}
}
