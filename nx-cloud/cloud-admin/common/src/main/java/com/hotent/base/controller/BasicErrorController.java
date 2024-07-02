/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.controller;

import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.ResultCode;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * 基础的请求错误的处理器
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月4日
 */
@RestController
public class BasicErrorController implements ErrorController{
	private static final String ERROR_PATH="/error";

	@RequestMapping("/error")
	public CommonResult<String> handleError(HttpServletRequest request){
		//获取statusCode:401,404,500
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if(statusCode==404){
			return new CommonResult<>(ResultCode.NOT_FOUND);
		}
		else if(statusCode==401) {
			return new CommonResult<>(ResultCode.NOLOGIN);
		}
		else {
			return new CommonResult<>(ResultCode.SYSTEM_ERROR);
		}
	}

}
