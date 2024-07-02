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
 * 应用系统异常
 * @author heyifan
 * @date 2017年6月30日
 */
public class SystemException extends BaseException{
	private static final long serialVersionUID = 1L;
	
	public SystemException(){
		super(ResultCode.SYSTEM_ERROR);
	}

	public SystemException(String detailMessage){
		super(ResultCode.SYSTEM_ERROR, detailMessage);
	}
}
