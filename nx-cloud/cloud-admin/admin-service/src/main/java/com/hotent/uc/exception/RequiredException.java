/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.exception;

import org.springframework.http.HttpStatus;

/**
 * 必填
 * @author liangqf 2017-09-05
 *
 */
public class RequiredException extends BaseException {
	
	private static final long serialVersionUID = 1L;
	
	public RequiredException(String message){
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}
	
	public RequiredException(String message, String shortMessage){
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message,shortMessage);
	}

}
