/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.manager;

import com.hotent.Calendar.model.CalendarShiftPeroid;
import com.hotent.Calendar.model.TimePeroid;
import com.pharmcube.mybatis.support.manager.BaseManager;

import java.time.LocalDateTime;
import java.util.List;


public interface CalendarShiftPeroidManager  extends BaseManager<CalendarShiftPeroid>{

	List<TimePeroid> getRealShiftPeroidList(String userId, LocalDateTime start_time,
			LocalDateTime end_time);

	List<CalendarShiftPeroid> getByShiftId(String shiftId);

	void shiftPeroidAdd(String shiftId, String[] startTime, String[] endTime,
			String[] memo);
	
}

