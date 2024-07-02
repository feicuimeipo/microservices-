/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.model;


public class CommonResult<E> {
	Boolean state = true;

	String message;
	
	E value;
	
	private String errorCode;
	
	/**
	 * 返回成功及成功的提示信息
	 * @param message
	 */
	public CommonResult(String message) {
		this(true, message, null);
	}
	
	/**
	 * 返回成功/失败，及对应的成功/失败提示信息
	 * @param state
	 * @param message
	 */
	public CommonResult(boolean state, String message) {
		this(state, message, null);
	}
	
	/**
	 * 返回成功/失败，及对应的成功/失败提示信息，还有返回对应的数据
	 * @param state
	 * @param message
	 * @param value
	 */
	public CommonResult(boolean state, String message, E value){
		this.state = state;
		this.message = message;
		this.value = value;
	}
	
	/**
	 * 返回错误，及错误编码，对应的错误信息
	 * @param error
	 */
	public CommonResult(ResultCode error) {
		this.state = false;
		this.errorCode = error.getCode();
		this.message = error.getMessage();
	}
	
	/**
	 * 返回错误，及错误编码，对应的错误信息，还有返回对应的数据
	 * @param error
	 * @param value
	 */
	public CommonResult(ResultCode error, E value) {
		this.state = false;
		this.errorCode = error.getCode();
		this.message = error.getMessage();
		this.value = value;
	}
	
	/**
	 * 返回错误，错误编码，对应的错误信息
	 * @param errorCode
	 * @param message
	 */
	public CommonResult(String errorCode, String message) {
		this.state = false;
		this.errorCode = errorCode;
		this.message = message;
	}
	
	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public CommonResult() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}
}
