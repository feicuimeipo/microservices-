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
 * 应用系统异常(500)
 * @author heyifan
 * @date 2017年6月30日
 */
public class SystemException extends BaseException{
	private static final long serialVersionUID = 1L;
	
	public SystemException(){
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
	
	public SystemException(String message){
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}
	
	public SystemException(String message, String shortMessage){
		super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message, shortMessage);
	}
	
	public SystemException(HttpStatus status, Integer code, String message, String shortMessage){
		super(status, code, message, shortMessage);
	}
}
