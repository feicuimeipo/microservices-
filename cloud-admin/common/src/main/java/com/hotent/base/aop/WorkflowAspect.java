/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.aop;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.base.annotation.Workflow;
import com.pharmcube.mybatis.support.model.DbBaseModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nianxi.api.feign.BpmServiceApi;
import org.nianxi.api.feign.dto.bpm.StartFlowParamObjectDTO;
import org.nianxi.api.feign.dto.bpm.StartResultDTO;
import org.nianxi.api.model.exception.BaseException;
import org.nianxi.api.model.exception.WorkFlowException;
import org.nianxi.boot.support.JsonUtil;
import org.nianxi.utils.BeanUtils;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 工作流切面处理
 * 启动工作流
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年8月17日
 */
@Aspect
@Component
public class WorkflowAspect {
	@Resource
	BpmServiceApi bpmRuntimeFeignService;
	
	@SuppressWarnings({ "rawtypes" })
	@Around("execution(* *..*Controller.*(..)) && @annotation(com.hotent.base.annotation.Workflow)")
	public Object workflow(ProceedingJoinPoint joinPoint) throws Throwable{
		Object	returnVal=null;
		Class<?> targetClass = joinPoint.getTarget().getClass();
		String methodName = joinPoint.getSignature().getName();
		Object[] params=joinPoint.getArgs();
		Method[] methods = targetClass.getMethods();
		// 当前切中的方法
		Method method = null;
		// 当前实体类参数
		DbBaseModel param = null;
		
		for (int i = 0; i < params.length; i++){
			// 获取入参中第一个继承自 BaseModel的参数
			if (params[i] instanceof DbBaseModel){
				param = (DbBaseModel)params[i];
				break;
			}
		}
		
		if(BeanUtils.isEmpty(param)) {
			returnVal = joinPoint.proceed();
			return returnVal;
		}
		
		for (int i = 0; i < methods.length; i++){
			if (methods[i].getName() == methodName){
				method = methods[i];
				break;
			}
		}
		Workflow workflow = method.getAnnotation(Workflow.class);
		// 流程定义
		String flowKey = workflow.flowKey();
		// 业务系统编码
		String sysCode = workflow.sysCode();
		// 实例ID回填到实体类的哪个字段
		String instanceIdField = workflow.instanceIdField();
		// 变量keys集合
		String[] varKeys = workflow.varKeys();
		if(StringUtils.isEmpty(flowKey)) {
			return joinPoint.proceed();
		}
		// 构建启动流程的参数
		ObjectNode startFlowParam = JsonUtil.getMapper().createObjectNode();
		startFlowParam.put("flowKey", flowKey);
		String businessKey = param.getPkVal();
		if(StringUtils.isEmpty(businessKey)) {
			// 没有主键时抛出错误
			throw new WorkFlowException("启动流程时，实体对象中的id不能为空");
		}
		startFlowParam.put("businessKey", businessKey);
		startFlowParam.put("formType", "frame");
		if(StringUtils.isNotEmpty(sysCode)) {
			startFlowParam.put("sysCode", sysCode);
		}
		// 构建vars流程变量
		if(BeanUtils.isNotEmpty(varKeys) && varKeys.length > 0) {
			ObjectNode varsObject = JsonUtil.getMapper().createObjectNode();
			JsonNode paramJsonNode = JsonUtil.toJsonNode(param);
			for(String key : varKeys) {
				JsonNode jsonNode = paramJsonNode.get(key);
				varsObject.set(key, jsonNode);
			}
			startFlowParam.set("vars", varsObject);
		}
		
		// 调用接口启动流程
		StartFlowParamObjectDTO dto = new StartFlowParamObjectDTO();
		org.springframework.beans.BeanUtils.copyProperties(startFlowParam,dto);
		StartResultDTO startFlowResult = bpmRuntimeFeignService.start(dto);

		//if(BeanUtils.isNotEmpty(startFlowResult) && startFlowResult.get("state").asBoolean()) {
		if(BeanUtils.isNotEmpty(startFlowResult) && startFlowResult.getState()) {
			//String instanceId = startFlowResult.get("instId").asText();
			String instanceId = startFlowResult.getInstId();
			// 将流程实例ID回填到实体对象中
			BeanUtils.setProperty(param, instanceIdField, instanceId);
			returnVal = joinPoint.proceed();
		}else {
			String message = "流程启动失败";
			//if(BeanUtils.isNotEmpty(startFlowResult) && BeanUtils.isNotEmpty(startFlowResult.get("message"))) {
			if(BeanUtils.isNotEmpty(startFlowResult) && BeanUtils.isNotEmpty(startFlowResult.getMessage())) {
				//message += ":" + startFlowResult.get("message").asText();
				message += ":" + startFlowResult.getMessage();
			}
			throw new BaseException(message);
		}
		return returnVal;
	}
}
