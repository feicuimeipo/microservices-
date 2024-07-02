/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.controller;


import javax.annotation.Resource;

import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.controller.BaseController;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.hotent.sys.persistence.manager.SysMethodManager;
import com.hotent.sys.persistence.model.SysMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 系统菜单 控制器类
 * 
 * @company 广州宏天软件股份有限公司
 * @author liyg
 * @email liyg@jee-soft.cn
 * @date 2018-07-02 17:18:55
 */
@RestController
@RequestMapping("/sys/sysMethod/v1")
@Api(tags="系统菜单方法")
@ApiGroup(group= {ApiGroupConsts.GROUP_PORTAL})
public class SysMethodController extends BaseController<SysMethodManager, SysMethod>{
	@Resource
	SysMethodManager sysMethodManager;

	@RequestMapping(value="listJson", method=RequestMethod.POST, produces = { "application/json; charset=utf-8" })
	@ApiOperation(value = "菜单资源数据", httpMethod = "POST", notes = "菜单资源数据")
	public PageList<SysMethod> listJson(@ApiParam(name="queryFilter",value="通用查询对象")@RequestBody QueryFilter<SysMethod> queryFilter) throws Exception {
		return sysMethodManager.query(queryFilter);
	}
	
	@RequestMapping(value="getRoleMethods", method=RequestMethod.POST, produces = { "application/json; charset=utf-8" })
	@ApiOperation(value = "获取角色已有的后台方法集", httpMethod = "POST", notes = "获取角色已有的后台方法集")
	public PageList<SysMethod> getRoleMethods(@ApiParam(name="roleAlias",value="角色别名") @RequestParam("roleAlias") String roleAlias,@ApiParam(name="queryFilter",value="通用查询对象")@RequestBody QueryFilter queryFilter){
		return sysMethodManager.getRoleMethods(roleAlias,queryFilter);
	}

	

}
