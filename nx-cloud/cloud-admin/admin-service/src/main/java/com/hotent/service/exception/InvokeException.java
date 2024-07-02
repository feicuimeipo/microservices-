package com.hotent.service.exception;

import org.nianxi.api.enums.ResultCode2;
import org.nianxi.api.exception.BaseException;

/**
 * 服务调用异常
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月3日
 */
public class InvokeException extends BaseException {
	private static final long serialVersionUID = 1L;
	
	public InvokeException(){
		super(ResultCode2.SERVICE_INVOKE_ERROR);
	}
	
	public InvokeException(String message){
		super(ResultCode2.SERVICE_INVOKE_ERROR, message);
	}
}
