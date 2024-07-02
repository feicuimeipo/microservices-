/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.calc.impl;


import com.hotent.Calendar.calc.ICalendarCalc;
import com.hotent.Calendar.manager.CalendarShiftPeroidManager;
import com.hotent.Calendar.model.TimePeroid;
import com.hotent.Calendar.util.CalendarUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;

public class ShiftCalc implements ICalendarCalc {
	@Resource
	CalendarShiftPeroidManager calendarShiftPeroidManager;

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "shift";
	}

	@Override
	public List<TimePeroid> getRealTimePeroidList(String userId, LocalDateTime startTime,
			LocalDateTime endTime) {
		List<TimePeroid> list=calendarShiftPeroidManager.getRealShiftPeroidList(userId,startTime,endTime);
		return list;
	}

	@Override
	public SortedMap<LocalDateTime, TimePeroid> overrideCalendarShiftPeroidMap(
			SortedMap<LocalDateTime, TimePeroid> calendarShiftPeroidMap,
			List<TimePeroid> shiftTimePeroidlist) {
		// TODO Auto-generated method stub
		return CalendarUtil.getTimePeroidMap(shiftTimePeroidlist);
	}

}
