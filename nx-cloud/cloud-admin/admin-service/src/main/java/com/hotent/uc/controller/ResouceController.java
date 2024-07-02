/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;

import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.controller.BaseController;
import com.hotent.uc.manager.ResouceManager;
import com.hotent.uc.model.Resouce;
import org.nianxi.api.model.CommonResult;
import com.hotent.uc.params.resouce.ResouceVo;

/**
 * 维度模块接口
 * @author zhangxw
 *
 */
@RestController
@RequestMapping("/api/resouce/v1/")
@Api(tags="角色资源关系")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class ResouceController extends BaseController<ResouceManager, Resouce> {
	
	@Resource
	ResouceManager resouceService;

	/**
	 * 添加维度
	 * @param
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value="resouce/saveResouce",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "添加角色资源关系", httpMethod = "POST", notes = "添加角色资源关系")
	public CommonResult<String> saveResouce(@ApiParam(name="resouceVo",value="维度参数对象", required = true) @RequestBody ResouceVo resouceVo) throws Exception{
		return resouceService.saveResouce(resouceVo);
	}
	
	/**
	 * 添加维度
	 * @param
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value="resouce/getByRoleCode",method=RequestMethod.GET, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "根据角色获取资源信息", httpMethod = "GET", notes = "根据角色获取资源信息")
	public Resouce getByRoleCode(@ApiParam(name="roleCode",value="角色编码", required = true)  String roleCode) throws Exception{
		return resouceService.getByRoleCode(roleCode);
	}
}
