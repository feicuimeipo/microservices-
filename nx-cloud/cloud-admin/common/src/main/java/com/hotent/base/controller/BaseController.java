/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.controller;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.pharmcube.api.model.page.PageList;
import com.pharmcube.mybatis.support.manager.BaseManager;
import com.pharmcube.mybatis.support.query.QueryFilter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 基础的控制器
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月6日
 */
public class BaseController <M extends BaseManager<T>, T extends Model<T>>{
	@Autowired
	protected M baseService;
	
	@PostMapping("/")
	@ApiOperation("添加实体的接口")
	public CommonResult<String> create(@ApiParam(name="model", value="实体信息") @RequestBody T t) {
		boolean result = baseService.save(t);
		if(!result) {
			return new CommonResult<>(ResultCode.FAIL_OPTION, null);
		}
		return new CommonResult<>();
	}

	@PostMapping(value="/query", produces={"application/json; charset=utf-8" })
	@ApiOperation("分页查询结果")
	public PageList<T> query(@ApiParam(name="queryFilter", value="分页查询信息") @RequestBody QueryFilter<T> queryFilter) {
		return baseService.query(queryFilter);
	}

	@GetMapping("/{id}")
	@ApiOperation("根据id查询实体")
	public T getById(@ApiParam(name="id", value="实体id") @PathVariable String id) {
		return baseService.getById(id);
	}

	@PutMapping("/")
	@ApiOperation("更新实体")
	public CommonResult<String> updateById(@ApiParam(name="model", value="实体信息") @RequestBody T t) {
		boolean result = baseService.updateById(t);
		if(!result) {
			return new CommonResult<>(ResultCode.FAIL_OPTION, "更新实体失败");
		}
		return new CommonResult<>();
	}

	@DeleteMapping("/{id}")
	@ApiOperation("根据id删除")
	public CommonResult<String> deleteById(@ApiParam(name="id", value="实体id") @PathVariable String id) {
		boolean result = baseService.removeById(id);
		if(!result) {
			return new CommonResult<>(ResultCode.FAIL_OPTION, "删除实体失败");
		}
		return new CommonResult<>();
	}
	
	@DeleteMapping("/")
	@ApiOperation("根据id集合批量删除")
	public CommonResult<String> deleteByIds(@ApiParam(name="ids", value="实体集合") @RequestParam String...ids) {
		boolean result = baseService.removeByIds(Arrays.asList(ids));
		if(!result) {
			return new CommonResult<>(ResultCode.FAIL_OPTION, "删除实体失败");
		}
		return new CommonResult<>();
	}
}
