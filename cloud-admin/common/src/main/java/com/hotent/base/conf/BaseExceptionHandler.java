/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.conf;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.binding.BindingException;
import org.mybatis.spring.MyBatisSystemException;
import org.nianxi.api.model.CommonResult;
import org.nianxi.api.model.ResultCode;
import org.nianxi.api.model.exception.BaseException;
import org.nianxi.utils.ThreadMsgUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.ConnectException;
import java.sql.SQLException;



/**
 * 全局的异常处理类
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月4日
 */
@Slf4j
@RestControllerAdvice(annotations = { RestController.class, Controller.class })
public class BaseExceptionHandler {
	/**
	 * 请求参数类型错误异常的捕获
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { BindException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public CommonResult<String> badRequest(BindException e) {
		log.error(e.getMessage(),e);
		return new CommonResult<>(ResultCode.BAD_REQUEST);
	}

	/**
	 * 404错误异常的捕获
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { NoHandlerFoundException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public CommonResult<String> badRequestNotFound(BindException e) {
		log.error(e.getMessage(),e);
		return new CommonResult<>(ResultCode.NOT_FOUND);
	}

	/**
	 * mybatis未绑定异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(BindingException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> mybatis(Exception e) {
		log.error(e.getMessage(),e);
		return new CommonResult<>(ResultCode.BOUND_STATEMENT_NOT_FOUNT);
	}

	/**
	 * 自定义异常的捕获 自定义抛出异常。统一的在这里捕获返回JSON格式的友好提示。
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { BaseException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public <T extends Serializable> CommonResult<T> sendError(BaseException e, HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		log.error(String.format("occurs error when execute url ={} ,message {}",requestURI,e.getMessage()), e);
		return new CommonResult(e.getCode(),
				StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : e.getDetailMessage());
	}

	/**
	 * 数据库操作出现异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { SQLException.class, DataAccessException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> systemError(Exception e) {
		log.error(e.getMessage(),e);
		if(e instanceof MyBatisSystemException){
		    return new CommonResult<>(((MyBatisSystemException) e).getRootCause().getMessage());
        }else{
        	CommonResult<String> commonResult = new CommonResult<>(ResultCode.DATABASE_ERROR);
        	commonResult.setMessage(String.format("%s：%s", commonResult.getMessage(), ExceptionUtils.getRootCauseMessage(e)));
            return commonResult;
		}
    }

	/**
	 * 网络连接失败！
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { ConnectException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> connect(Exception e) {
		log.error(e.getMessage(),e);
		return new CommonResult<>(ResultCode.CONNECTION_ERROR);
	}

	@ExceptionHandler(value = { RuntimeException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> runTimeError(Exception e) {
		log.error(e.getMessage(),e);
		String errorMsg = ExceptionUtils.getRootCauseMessage(e);
		String flowErrorMsg = ThreadMsgUtil.getMapMsg(ThreadMsgUtil.MSG_FLOW_ERROR, true);
		if (StringUtils.isNotEmpty(errorMsg) && errorMsg.indexOf("流程异常") > -1 && StringUtils.isNotEmpty(flowErrorMsg)) {
			errorMsg = flowErrorMsg;
		}else if (StringUtils.isNotEmpty(errorMsg)) {
			errorMsg = errorMsg.replace("RuntimeException:", "");
		}
		return new CommonResult<>(false, errorMsg);
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> notAllowed(Exception e) {
		log.error(e.getMessage(),e);
		return new CommonResult<>(ResultCode.SYSTEM_ERROR);
	}
	
	/**
	 * 校验错误信息收集
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonResult<String> MethodArgumentNotValidExceptiona(MethodArgumentNotValidException e) {
		log.error(e.getMessage(),e);
		StringBuffer stringBuffer = new StringBuffer();
		BindingResult bindingResult = e.getBindingResult();
		bindingResult.getAllErrors().forEach(error-> stringBuffer.append(error.getDefaultMessage()+" "));
		return new CommonResult<String>(ResultCode.ILLEGAL_ARGUMENT.getCode(),stringBuffer.toString());
	}
}
