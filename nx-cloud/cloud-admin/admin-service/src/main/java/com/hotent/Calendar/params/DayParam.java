/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.params;




/**
 * 日期参数类
 * @company 广州宏天软件股份有限公司
 * @author zhangxw
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月5日
 */
//@ApiModel(value="日期参数类")
public class DayParam {

	////@ApiModelProperty(name="day",notes="day")
	private Integer day;
	
	////@ApiModelProperty(name="type",notes="类型")
	private String type;
	
	////@ApiModelProperty(name="worktimeid",notes="worktimeid")
	private String worktimeid;

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWorktimeid() {
		return worktimeid;
	}

	public void setWorktimeid(String worktimeid) {
		this.worktimeid = worktimeid;
	}
	
}
