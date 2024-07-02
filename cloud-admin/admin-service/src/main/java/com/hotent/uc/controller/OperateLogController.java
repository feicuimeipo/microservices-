/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;

import javax.annotation.Resource;


import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.controller.BaseController;
import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.hotent.uc.manager.OperateLogManager;
import com.hotent.uc.model.OperateLog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;


/**
 * 操作日志接口
 * @author zhangxw
 *
 */
@RestController
@RequestMapping("/api/operateLog/v1/")
@Api(tags="操作日志")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class OperateLogController extends BaseController<OperateLogManager, OperateLog> {
	
	@Resource
	OperateLogManager operateLogService;
	
	/**
	 * 查询日志列表
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="operateLogs/getLogPage",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	public PageList<OperateLog> getLogPage(@ApiParam(name = "queryFilter", value = "查询参数", required = true) @RequestBody QueryFilter queryFilter) throws Exception{
    	PageList<OperateLog> query = operateLogService.query(queryFilter);
    	return query;
	}
	
	/**
	 * 根据id删除日志
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="operateLogs/removeByIds",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	public CommonResult<String>  removeByIds(@ApiParam(name = "ids", value = "id字符串，多个用“,”分割", required = true) @RequestBody String ids) throws Exception{
    	return operateLogService.removeByIdStr(ids);
	}
	
}
