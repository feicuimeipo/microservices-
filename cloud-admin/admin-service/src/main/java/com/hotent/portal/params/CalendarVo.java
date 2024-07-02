/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.params;



/**
 * 
 * 日历操作对象
 * @company 广州宏天软件股份有限公司
 * @author zhangxianwen
 * @email zhangxw@jee-soft.cn
 * @date 2018年8月2日
 */
public class CalendarVo {

	////@ApiModelProperty(name = "userId", notes = "userId", required = true)
	protected String userId;

	////@ApiModelProperty(name = "startTime", notes = "startTime")
	protected String startTime;

	////@ApiModelProperty(name = "endTime", notes = "endTime")
	protected String endTime;
	
	////@ApiModelProperty(name = "time", notes = "time")
	protected String time;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}