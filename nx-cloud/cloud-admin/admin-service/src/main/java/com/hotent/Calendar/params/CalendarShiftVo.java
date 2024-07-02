/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.params;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hotent.Calendar.model.CalendarShift;
import com.hotent.Calendar.model.CalendarShiftPeroid;



import java.util.List;

/**
 * 班次管理信息vo
 * @company 广州宏天软件股份有限公司
 * @author zhangxw
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月5日
 */
//@ApiModel(value="日历分配信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarShiftVo {

	////@ApiModelProperty(name="calendarShift",notes="班次对象")
	private CalendarShift calendarShift;
	
	////@ApiModelProperty(name="shiftPeroidlist",notes="班次时间列表")
	private List<CalendarShiftPeroid> shiftPeroidlist;
	
	public CalendarShiftVo(){}
	
	public CalendarShiftVo(CalendarShift calendarShift, List<CalendarShiftPeroid> shiftPeroidlist){
		this.calendarShift = calendarShift;
		this.shiftPeroidlist = shiftPeroidlist;
	}

	public CalendarShift getCalendarShift() {
		return calendarShift;
	}

	public void setCalendarShift(CalendarShift calendarShift) {
		this.calendarShift = calendarShift;
	}

	public List<CalendarShiftPeroid> getShiftPeroidlist() {
		return shiftPeroidlist;
	}

	public void setShiftPeroidlist(List<CalendarShiftPeroid> shiftPeroidlist) {
		this.shiftPeroidlist = shiftPeroidlist;
	}
	
}
