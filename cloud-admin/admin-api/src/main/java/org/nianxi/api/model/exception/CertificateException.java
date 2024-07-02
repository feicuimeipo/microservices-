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
 * 授信异常，禁止访问
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年8月30日
 */
public class CertificateException extends BaseException{
	private static final long serialVersionUID = 1L;

	public CertificateException(){
		super(ResultCode.CERT_ERROR);
	}
	
	public CertificateException(String detailMessage){
		super(ResultCode.CERT_ERROR, detailMessage);
	}
}
