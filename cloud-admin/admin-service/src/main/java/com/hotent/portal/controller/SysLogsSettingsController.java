/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.controller;

import java.util.Map;

import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.controller.BaseController;
import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import org.nianxi.utils.StringUtil;
import com.hotent.sys.persistence.manager.SysLogsSettingsManager;
import com.hotent.sys.persistence.model.SysLogsSettings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * <pre> 
 * 描述：日志配置 控制器类
 * 构建组：x7
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-08-31 16:19:34
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@RestController
@RequestMapping(value="/sys/sysLogsSettings/v1")
@Api(tags="系统日志配置")
@ApiGroup(group= {ApiGroupConsts.GROUP_PORTAL})
public class SysLogsSettingsController extends BaseController<SysLogsSettingsManager, SysLogsSettings>{
	/**
	 * 日志配置列表(分页条件查询)数据
	 * @return
	 * @throws Exception 
	 * PageJson
	 * @exception 
	 */
	@PostMapping("/list")
	@ApiOperation(value="日志配置数据列表", httpMethod = "POST", notes = "获取日志配置列表")
	public PageList<SysLogsSettings> list(@ApiParam(name="queryFilter",value="查询对象")@RequestBody QueryFilter<SysLogsSettings> queryFilter) throws Exception{
		return baseService.query(queryFilter);
	}
	
	/**
	 * 日志配置明细页面
	 * @param id
	 * @return
	 * @throws Exception 
	 * ModelAndView
	 */
	@GetMapping(value="/get/{id}")
	@ApiOperation(value="日志配置数据详情",httpMethod = "GET",notes = "日志配置数据详情")
	public SysLogsSettings get(@ApiParam(name="id",value="业务对象主键", required = true)@PathVariable String id) throws Exception{
		return baseService.get(id);
	}
	
    /**
	 * 新增日志配置
	 * @param sysLogsSettings
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@PostMapping(value="save")
	@ApiOperation(value = "新增,更新日志配置数据", httpMethod = "POST", notes = "新增,更新日志配置数据")
	public CommonResult<String> save(@ApiParam(name="sysLogsSettings",value="日志配置业务对象", required = true)@RequestBody SysLogsSettings sysLogsSettings) throws Exception{
		String msg = "添加日志配置成功";
		if(StringUtil.isEmpty(sysLogsSettings.getId())){
			baseService.create(sysLogsSettings);
		}else{
			baseService.update(sysLogsSettings);
			 msg = "更新日志配置成功";
		}
		return new CommonResult<String>(msg);
	}
	
	/**
	 * 删除日志配置记录
	 * @param id
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@DeleteMapping(value="remove/{id}")
	@ApiOperation(value = "删除日志配置记录", httpMethod = "DELETE", notes = "删除日志配置记录")
	public  CommonResult<String>  remove(@ApiParam(name="id",value="业务主键", required = true)@PathVariable String id) throws Exception{
		baseService.remove(id);
		return new CommonResult<String>(true, "删除成功");
	}
	
	/**
	 * 批量删除日志配置记录
	 * @param ids
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@DeleteMapping(value="/removes")
	@ApiOperation(value = "批量删除日志配置记录", httpMethod = "DELETE", notes = "批量删除日志配置记录")
	public CommonResult<String> removes(@ApiParam(name="ids",value="业务主键数组,多个业务主键之间用逗号分隔", required = true)@RequestParam String...ids) throws Exception{
		baseService.removeByIds(ids);
		return new CommonResult<String>(true, "批量删除成功");
	}
	
	/**
	 * 获取日志配置状态的Map
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/getSysLogsSettingStatusMap")
	public Map<String, String> getSysLogsSettingStatusMap() throws Exception{
		return baseService.getSysLogsSettingStatusMap();
	}
}
