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
 * 流程自定义异常
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月9日
 */
public class WorkFlowException extends BaseException {
	private static final long serialVersionUID = 1L;

	public WorkFlowException(){
		super(ResultCode.WORKFLOW_ERROR);
	}
	
	public WorkFlowException(String detailMessage) {
		super(ResultCode.WORKFLOW_ERROR, detailMessage);
	}
}
