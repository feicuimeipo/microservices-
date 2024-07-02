/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.exception;

import org.nianxi.api.enums.ResultCode2;
import org.springframework.http.HttpStatus;

/**
 * 异常基类
 * @author heyifan
 * @date 2017年6月30日
 */
public class BaseException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;	/*HttpStatus错误码*/
	private Integer code = 500;										/*自编错误代码*/
	private String shortMessage = "";								/*简短的异常信息，可直接显示给终端用户*/
	
	public BaseException(){
		super();
	}
	
	public BaseException(String message){
		super(message);
	}
	
	public BaseException(HttpStatus status, Integer code){
		super();
		this.status = status;
		this.code = code;
	}
	
	public BaseException(HttpStatus status, Integer code, String message){
		super(message);
		this.status = status;
		this.code = code;
	}
	
	public BaseException(HttpStatus status, Integer code, String message, String shortMessage){
		super(message);
		this.status = status;
		this.code = code;
		this.shortMessage = shortMessage;
	}

	public BaseException(ResultCode2 serviceInvokeError, String rootCauseMessage) {
		super(rootCauseMessage);
		this.code = Integer.valueOf(serviceInvokeError.getCode());
		this.shortMessage = serviceInvokeError.getMessage();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}
}
