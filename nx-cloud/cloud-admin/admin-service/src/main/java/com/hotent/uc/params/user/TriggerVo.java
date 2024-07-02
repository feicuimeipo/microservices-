/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.user;




/**
 * 用户参数对象
 * @author liangqf
 *
 */
//@ApiModel
public class TriggerVo {
	////@ApiModelProperty(name="type",notes="计划定时任务类型")
	private String type;
	////@ApiModelProperty(name="values",notes="计划定时任务值")
	private String values;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}

}
