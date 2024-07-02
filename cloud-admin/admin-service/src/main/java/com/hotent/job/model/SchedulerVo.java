/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.job.model;




//@ApiModel(description="定时任务vo对象")
public class SchedulerVo {
	////@ApiModelProperty("类名")
	protected String className;
	////@ApiModelProperty("任务名")
	protected String jobName;
	////@ApiModelProperty("参数")
	protected String parameterJson;
	////@ApiModelProperty("描述")
	protected String description;

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getParameterJson() {
		return parameterJson;
	}
	public void setParameterJson(String parameterJson) {
		this.parameterJson = parameterJson;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
