/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.params;


import com.hotent.Calendar.model.CalendarSettingEvent;



import java.util.List;

/**
 * 系统日历信息vo
 * @company 广州宏天软件股份有限公司
 * @author zhangxw
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月5日
 */
//@ApiModel(value="系统日历信息")
public class CalendarSettingVo {

	////@ApiModelProperty(name="calendarSettingEvent",notes="日历设置事件列表")
	private List<CalendarSettingEvent> calendarSettingEvent;
	
	////@ApiModelProperty(name="year",notes="年份")
	private Integer year;
	
	////@ApiModelProperty(name="result",notes="获取状态")
	private Boolean result;
	
	public CalendarSettingVo(){}
	
	public CalendarSettingVo(List<CalendarSettingEvent> calendarSettingEvent, Integer year, Boolean result){
		this.calendarSettingEvent = calendarSettingEvent;
		this.year = year;
		this.result = result;
	}

	public List<CalendarSettingEvent> getCalendarSettingEvent() {
		return calendarSettingEvent;
	}

	public void setCalendarSettingEvent(
			List<CalendarSettingEvent> calendarSettingEvent) {
		this.calendarSettingEvent = calendarSettingEvent;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

}
