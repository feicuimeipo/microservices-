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
 * 数据源异常
 * 
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年4月4日
 */
public class DataBaseException extends BaseException {
	private static final long serialVersionUID = 3148019938789322656L;

	public DataBaseException(){
		super(ResultCode.DATABASE_ERROR);
	}
	
	public DataBaseException(String detailMessage){
		super(ResultCode.DATABASE_ERROR, detailMessage);
	}
}
