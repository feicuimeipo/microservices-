/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.manager;

import com.hotent.Calendar.model.CalendarDateType;
import com.pharmcube.mybatis.support.manager.BaseManager;

import java.util.Map;

public interface CalendarDateTypeManager extends BaseManager<CalendarDateType>{

	Map<Integer, String> getLhByYearMon(String statTime, String endTime);
	
	//得到周日期范围的公休日
	Map<Integer, String> getPhByWeekMap();
	
	
}
