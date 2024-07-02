package com.hotent.service.exception;


import org.nianxi.api.enums.ResultCode2;
import org.nianxi.api.exception.BaseException;

/**
 * WSDL文档解析异常
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年7月3日
 */
public class WSDLParseException extends BaseException {
	private static final long serialVersionUID = 1L;

	public WSDLParseException(){
		super(ResultCode2.WEBSERVICE_PARSE_ERROR);
	}
	
	public WSDLParseException(String message){
		super(ResultCode2.WEBSERVICE_PARSE_ERROR, message);
	}
}