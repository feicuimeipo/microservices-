/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.controller;

import com.hotent.Calendar.manager.CalendarAssignManager;
import com.hotent.Calendar.manager.CalendarManager;
import com.hotent.Calendar.model.Calendar;
import com.hotent.Calendar.model.CalendarAssign;
import com.hotent.Calendar.params.CalendarAssignGetVo;
import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.calendar.ICalendarService;
import com.hotent.base.controller.BaseController;
import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.MapUtil;
import org.nianxi.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 日历分配
 * 
 * @company 广州宏天软件股份有限公司
 * @author zhangxianwen
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月26日
 */
@RestController
@RequestMapping("/calendar/assign/v1/")
@Api(tags="日历分配")
@ApiGroup(group= {ApiGroupConsts.GROUP_PORTAL})
public class CalendarAssignController extends BaseController<CalendarAssignManager, CalendarAssign> {
	@Resource
	CalendarAssignManager calendarAssignManager;
	@Resource
	CalendarManager calendarManager;
	@Resource
	ICalendarService iCalendarService;

	
	@RequestMapping(value="list", method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiOperation(value = "日历分配列表(分页条件查询)", httpMethod = "POST", notes = "日历分配列表(分页条件查询)")
	public PageList<CalendarAssign> listJson(@ApiParam(name="queryFilter",value="通用查询对象")@RequestBody QueryFilter<CalendarAssign> queryFilter) throws Exception {
		return calendarAssignManager.query(queryFilter);
	}
	
	@RequestMapping(value="get",method=RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	@ApiOperation(value = "获取日历分配详细信息", httpMethod = "GET", notes = "获取日历分配详细信息")
	public CalendarAssignGetVo get(@ApiParam(name="id",value="日历分配id", required = true) @RequestParam String id) throws Exception{
		List<Calendar> calendarList = calendarManager.getAll();
		CalendarAssign calendarAssign = null;
		if (StringUtil.isNotEmpty(id)) {
			calendarAssign = calendarAssignManager.get(id);
		} 
		return new CalendarAssignGetVo(calendarAssign, calendarList);
	}
	
	
	@RequestMapping(value="save",method=RequestMethod.POST, produces = { "application/json; charset=utf-8" })
	@ApiOperation(value = "保存日历分配信息", httpMethod = "POST", notes = "保存日历分配信息")
	public CommonResult<String> save(@ApiParam(name="map",value="日历分配信息", required = true) @RequestBody Map<String,Object> map) throws Exception{
		String assignStr = MapUtil.getString(map,"assign");
		List<String> duplicateNames = calendarAssignManager.saveAssign(assignStr);
		return new CommonResult<String>(true, "保存成功",JsonUtil.toJson(duplicateNames));
	}
	
	@RequestMapping(value="remove",method=RequestMethod.DELETE, produces = { "application/json; charset=utf-8" })
	@ApiOperation(value = "批量删除日历分配记录(逻辑删除)", httpMethod = "DELETE", notes = "批量删除日历分配记录(逻辑删除)")
	public CommonResult<String> remove(@ApiParam(name="ids",value="日历分配记录ID，多个用“,”号分隔", required = true) @RequestParam String ids) throws Exception{
		String[] aryIds = null;
		if(!StringUtil.isEmpty(ids)){
			aryIds = ids.split(",");
		}
		calendarAssignManager.removeByIds(aryIds);
		return new CommonResult<String>(true, "删除成功");
	}

}
