/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.manager;

import com.hotent.Calendar.model.CalendarShift;
import com.pharmcube.mybatis.support.manager.BaseManager;


public interface CalendarShiftManager  extends BaseManager<CalendarShift>{
	
	void setDefaultShift(String id);
	
}

