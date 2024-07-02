/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.common.service.api.exception;

import com.nx.common.model.constant.ResultCode;
import com.nx.common.exception.BaseException;

/**
 * Feign未找到对应微服务异常
 * @email xlnian@163.com
 * @date 2020年4月9日
 */
public class EmptyFeignException extends BaseException {
	private static final long serialVersionUID = 1L;

	public EmptyFeignException() {
		super(ResultCode.FEIGN_EMPTY_ERROR);
	}

	public EmptyFeignException(String message) {
		super(ResultCode.FEIGN_EMPTY_ERROR);
		this.msg = message;
	}
}
