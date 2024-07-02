/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;


import com.hotent.base.controller.BaseController;
import com.hotent.uc.manager.PwdStrategyManager;
import com.hotent.uc.model.PwdStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nianxi.api.model.CommonResult;
import org.nianxi.boot.annotation.ApiGroup;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import org.nianxi.utils.StringUtil;
import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 
 * <pre> 
 * 描述：密码策略 控制器类
 * 构建组：x7
 * 作者:tangxin
 * 邮箱:tangxin@jee-soft.cn
 * 日期:2020-04-20 15:45:36
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@RestController
@RequestMapping("/api/pwdStrategy/v1/")
@Api(tags="密码策略")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class PwdStrategyController extends BaseController<PwdStrategyManager, PwdStrategy>{
	
	@Resource
	PwdStrategyManager pwdStrategyManager;
	
	/**
	 * 密码策略列表(分页条件查询)数据
	 * @param request
	 * @return
	 * @throws Exception 
	 * PageJson
	 * @exception 
	 */
	@PostMapping("list")
	@ApiOperation(value="密码策略数据列表", httpMethod = "POST", notes = "获取密码策略列表")
	public PageList<PwdStrategy> list(@ApiParam(name="queryFilter",value="查询对象")@RequestBody QueryFilter<PwdStrategy> queryFilter) throws Exception{
		return pwdStrategyManager.query(queryFilter);
	}
	
	/**
	 * 密码策略明细页面
	 * @param id
	 * @return
	 * @throws Exception 
	 * ModelAndView
	 */
	@GetMapping(value="get/{id}")
	@ApiOperation(value="密码策略数据详情",httpMethod = "GET",notes = "密码策略数据详情")
	public PwdStrategy get(@ApiParam(name="id",value="业务对象主键", required = true)@PathVariable String id) throws Exception{
		return pwdStrategyManager.get(id);
	}
	
    /**
	 * 新增密码策略
	 * @param mySchedule
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@PostMapping(value="save")
	@ApiOperation(value = "新增,更新密码策略数据", httpMethod = "POST", notes = "新增,更新密码策略数据")
	public CommonResult<String> save(@ApiParam(name="mySchedule",value="密码策略业务对象", required = true)@RequestBody PwdStrategy sysPwdStrategy) throws Exception{
		String msg = "添加密码策略成功";
		if(StringUtil.isEmpty(sysPwdStrategy.getId())){
			pwdStrategyManager.create(sysPwdStrategy);
		}else{
			pwdStrategyManager.update(sysPwdStrategy);
			 msg = "更新密码策略成功";
		}
		return new CommonResult<String>(msg);
	}
	
	/**
	 * 删除密码策略记录
	 * @param id
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@DeleteMapping(value="remove/{id}")
	@ApiOperation(value = "删除密码策略记录", httpMethod = "DELETE", notes = "删除密码策略记录")
	public  CommonResult<String>  remove(@ApiParam(name="id",value="业务主键", required = true)@PathVariable String id) throws Exception{
		pwdStrategyManager.remove(id);
		return new CommonResult<String>(true, "删除成功");
	}
	
	/**
	 * 批量删除密码策略记录
	 * @param ids
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@DeleteMapping(value="removes")
	@ApiOperation(value = "批量删除密码策略记录", httpMethod = "DELETE", notes = "批量删除密码策略记录")
	public CommonResult<String> removes(@ApiParam(name="ids",value="业务主键数组,多个业务主键之间用逗号分隔", required = true)@RequestParam String...ids) throws Exception{
		pwdStrategyManager.removeByIds(ids);
		return new CommonResult<String>(true, "删除成功");
	}
	
	@GetMapping(value="getDefault")
	@ApiOperation(value="获取默认密码策略",httpMethod = "GET",notes = "获取默认密码策略")
	public PwdStrategy getDefault() throws Exception{
		return pwdStrategyManager.getDefault();
	}
}
