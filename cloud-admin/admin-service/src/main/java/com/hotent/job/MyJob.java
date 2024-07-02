/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.job;


import com.hotent.job.model.BaseJob;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyJob extends BaseJob {

	protected Logger logger = LoggerFactory.getLogger(MyJob.class);
	@Override
	public void executeJob(JobExecutionContext context)  {
		//获取上下文参数。
		//context.getJobDetail().getJobDataMap()
		//com.hotent.platform.job.MyJob
		logger.info("定时计划测试正常com.hotent.job.MyJob");
		//
	}
}
