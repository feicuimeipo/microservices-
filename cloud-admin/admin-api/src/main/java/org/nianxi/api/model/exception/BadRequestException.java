/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.model.exception;


import lombok.extern.slf4j.Slf4j;
import org.nianxi.api.model.ResultCode;

/**
 * 坏请求
 * @author heyifan
 * @date 2017年6月30日
 */
@Slf4j
public class BadRequestException extends BaseException{
	private static final long serialVersionUID = 1L;
	
	public BadRequestException(){
		super(ResultCode.ILLEGAL_ARGUMENT);
	}
	
	public BadRequestException(String detailMessage){
		super(ResultCode.ILLEGAL_ARGUMENT, detailMessage);
	}
}
