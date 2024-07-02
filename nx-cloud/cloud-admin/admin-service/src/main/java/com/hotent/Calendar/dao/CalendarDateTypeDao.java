/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.Calendar.model.CalendarDateType;

import java.util.List;


public interface CalendarDateTypeDao extends BaseMapper<CalendarDateType> {

	List<CalendarDateType> getPhByWeekList();

	List<CalendarDateType> getLhByYearList();

	List<CalendarDateType> getChByYearList();


}
