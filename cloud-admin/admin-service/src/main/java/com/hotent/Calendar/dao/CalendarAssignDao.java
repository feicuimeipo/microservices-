/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hotent.Calendar.model.CalendarAssign;
import com.pharmcube.mybatis.support.query.PageList;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;


public interface CalendarAssignDao extends BaseMapper<CalendarAssign> {
	
	public CalendarAssign get(@Param("id") Serializable id);
	
	PageList<CalendarAssign> query(IPage<CalendarAssign> page, @Param(Constants.WRAPPER) Wrapper<CalendarAssign> convert2Wrapper);
	
	/**
	 * 根据分配类型和分配ID取得分配对象。
	 * @param assignType 1,用户,2,组织
	 * @param assignId	分配ID
	 * @return
	 */
	public CalendarAssign getByAssignId(@Param("assignType") int assignType,@Param("assignId") String assignId);
	
	/**
	 * 根据日历id删除记录
	 * @param calId
	 */
	public void delByCalId(@Param("canlendarId") String calId);
	
	/**
	 * 根据用户ID得到唯一条分配信息
	 * @param assignId
	 * @return
	 */
	public CalendarAssign getbyAssign(@Param("assignId") String assignId);
}
