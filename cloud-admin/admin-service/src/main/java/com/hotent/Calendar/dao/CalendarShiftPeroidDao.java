/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotent.Calendar.model.CalendarShiftPeroid;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CalendarShiftPeroidDao  extends BaseMapper<CalendarShiftPeroid> {

	
	/**
	 * 根据外键获取子表明细列表
	 * @param settingid
	 * @return
	 */
	public List<CalendarShiftPeroid> getCalendarShiftPeroidList(@Param("shiftId") String shiftId);
	
	/**
	 * 根据外键删除子表记录
	 * @param settingid
	 * @return
	 */
	public void delByMainId(@Param("shiftId") String settingid);
}


