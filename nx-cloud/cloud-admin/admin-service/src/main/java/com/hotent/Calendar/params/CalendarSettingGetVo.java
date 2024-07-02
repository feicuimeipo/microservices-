/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.params;


import com.hotent.Calendar.model.Calendar;
import com.hotent.Calendar.model.CalendarShift;



import java.util.List;

/**
 * 系统日历信息vo
 * @company 广州宏天软件股份有限公司
 * @author zhangxw
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月5日
 */
//@ApiModel(value="系统日历信息")
public class CalendarSettingGetVo {

	////@ApiModelProperty(name="calendar",notes="系统日历对象")
	private Calendar calendar;
	
	////@ApiModelProperty(name="shifts",notes="班次列表")
	private List<CalendarShift> shifts;
	
	public CalendarSettingGetVo(){}
	
	public CalendarSettingGetVo(Calendar calendar, List<CalendarShift> shifts){
		this.calendar = calendar;
		this.shifts = shifts;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public List<CalendarShift> getShifts() {
		return shifts;
	}

	public void setShifts(List<CalendarShift> shifts) {
		this.shifts = shifts;
	}

	
	
}
