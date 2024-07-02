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
 * 必填异常
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年6月29日
 */
public class RequiredException extends BaseException {
	
	private static final long serialVersionUID = 1L;
	
	public RequiredException(){
		super(ResultCode.REQUIRED_ERROR);
	}
	
	public RequiredException(String detailMessage){
		super(ResultCode.REQUIRED_ERROR, detailMessage);
	}
}
