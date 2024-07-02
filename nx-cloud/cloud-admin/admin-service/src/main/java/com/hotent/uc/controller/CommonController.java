/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;

import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.nianxi.boot.annotation.ApiGroup;
import org.nianxi.api.model.CommonResult;
import org.nianxi.utils.PinyinUtil;
import org.nianxi.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 角色组织模块接口
 * @author zhangxw
 *
 */
@RestController
@RequestMapping("/api/common/v1/")
@Api(tags="通用")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class CommonController{
	/**
	 * 汉字转拼音
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="getPinyin",method=RequestMethod.GET, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "汉字转拼音", httpMethod = "GET", notes = "汉字转拼音")
	public CommonResult<String> getPinyin(@ApiParam(name="chinese",value="需要获取拼音字母的中文", required = true) @RequestParam String chinese,@ApiParam(name="type",value="类型:1 为全拼，否则为首字母", required = true) @RequestParam Integer type) throws Exception{
		String pinyin = "";
		if(StringUtil.isNotEmpty(chinese)){
			if(type == 1){
				pinyin = PinyinUtil.getPinyin(chinese);
			}else{
				pinyin = PinyinUtil.getPinYinHeadChar(chinese);
			}
		}
		return new CommonResult<String>(true,"获取拼音成功！",pinyin);
	}
}
