/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;

import com.hotent.base.controller.BaseController;
import com.hotent.uc.manager.PropertiesService;
import com.hotent.uc.model.Properties;
import com.hotent.uc.params.properties.PropertiesVo;
import com.hotent.uc.util.UpdateMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nianxi.api.model.CommonResult;
import org.nianxi.boot.annotation.ApiGroup;
import com.pharmcube.mybatis.support.query.PageBean;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import org.nianxi.utils.BeanUtils;
import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统参数组织模块接口
 * @author zhangxw
 *
 */
@RestController
@RequestMapping("/api/properties/v1/")
@Api(tags="系统参数")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class PropertiesController extends BaseController<PropertiesService, Properties> {
	
	@Resource
	PropertiesService propertiesService;
	
	/**
	 * 查询系统参数
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="properties/getPropertiesPage",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "获取系统参数列表（带分页信息）", httpMethod = "POST", notes = "获取系统参数列表")
	public PageList<Properties> getPropertiesPage(@ApiParam(name = "filter", value = "查询参数", required = true) @RequestBody QueryFilter filter) throws Exception{
		return propertiesService.query(filter);
	}
	
	/**
	 * 获取所有系统参数
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="properties/getPropertiesList",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "获取所有系统参数", httpMethod = "POST", notes = "获取所有系统参数")
	public List<Properties> getPropertiesList(@ApiParam(name = "filter", value = "查询参数", required = true) @RequestBody QueryFilter filter) throws Exception{
		if(BeanUtils.isEmpty(filter)){
			filter = QueryFilter.build();
		}
		filter.setPageBean(new PageBean(1,10000));
		return propertiesService.query(filter).getRows();
	}
	
	
	/**
	 * 更新系统参数
	 * @param Properties
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="properties/updateProperties",method=RequestMethod.PUT, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "更新系统参数", httpMethod = "PUT", notes = "更新系统参数")
	@UpdateMethod(type=PropertiesVo.class)
	public CommonResult<String> updateProperties(@ApiParam(name="Properties",value="系统参数参数对象", required = true) @RequestBody  PropertiesVo Properties) throws Exception{
		return propertiesService.updateProperties(Properties);
	}
	
	
	/**
	 * 获取系统参数信息
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="properties/getProperties",method=RequestMethod.GET, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "根据编码获取系统参数信息", httpMethod = "GET", notes = "获取系统参数信息")
	public Properties getProperties(@ApiParam(name="code",value="系统参数编码", required = true) @RequestParam String code) throws Exception{
		return propertiesService.getPropertiesByCode(code);
	}

}
