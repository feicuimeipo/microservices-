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
 * Feign未找到对应微服务异常
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月9日
 */
public class EmptyFeignException extends BaseException{
	private static final long serialVersionUID = 1L;

	public EmptyFeignException() {
		super(ResultCode.FEIGN_EMPTY_ERROR);
	}
	
	public EmptyFeignException(String detailMessage) {
		super(ResultCode.FEIGN_EMPTY_ERROR, detailMessage);
	}
}
