/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.params;


import com.hotent.Calendar.model.Calendar;
import com.hotent.Calendar.model.CalendarAssign;



import java.util.List;

/**
 * 日历分配信息vo
 * @company 广州宏天软件股份有限公司
 * @author zhangxw
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月5日
 */
//@ApiModel(value="日历分配信息")
public class CalendarAssignGetVo {

	////@ApiModelProperty(name="calendarAssign",notes="日历分配对象")
	private CalendarAssign calendarAssign;
	
	////@ApiModelProperty(name="calendarList",notes="系统日历列表")
	private List<Calendar> calendarList;
	
	public CalendarAssignGetVo(){}
	
	public CalendarAssignGetVo(CalendarAssign calendarAssign, List<Calendar> calendarList){
		this.calendarAssign = calendarAssign;
		this.calendarList = calendarList;
	}

	public CalendarAssign getCalendarAssign() {
		return calendarAssign;
	}

	public void setCalendarAssign(CalendarAssign calendarAssign) {
		this.calendarAssign = calendarAssign;
	}

	public List<Calendar> getCalendarList() {
		return calendarList;
	}

	public void setCalendarList(List<Calendar> calendarList) {
		this.calendarList = calendarList;
	}
	
	
}
