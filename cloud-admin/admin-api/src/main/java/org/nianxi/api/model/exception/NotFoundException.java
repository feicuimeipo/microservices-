/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.model.exception;


import org.nianxi.api.model.ResultCode;

/**
 * 未找到资源
 * @author heyifan
 * @date 2017年6月30日
 */
public class NotFoundException extends BaseException{
	private static final long serialVersionUID = 1L;
	
	public NotFoundException(){
		super(ResultCode.NOT_FOUND);
	}
	
	public NotFoundException(String detailMessage){
		super(ResultCode.NOT_FOUND, detailMessage);
	}
}
