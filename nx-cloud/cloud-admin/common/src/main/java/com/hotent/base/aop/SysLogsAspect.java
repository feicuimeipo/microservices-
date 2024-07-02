/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.aop;

import com.hotent.base.conf.AdminConfig;
import com.hotent.ucapi.context.BootConstant;
import com.hotent.ucapi.context.ContextUtil;
import com.hotent.ucapi.support.AuthenticationUtil;
import com.pharmcube.actionlogger.ActionLog;
import com.pharmcube.actionlogger.ActionLogger;
import com.pharmcube.actionlogger.ActionLoggerFactory;
import com.pharmcube.actionlogger.StorageType;
import com.pharmcube.actionlogger.config.ActionLoggerConfig;
import com.pharmcube.boot.id.UniqueIdUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nianxi.boot.support.HttpUtil;
import org.nianxi.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统日志切面
 *
 * @company 广州宏天软件股份有限公司
 * @author liyg
 * @email liygui@jee-soft.cn
 * @date 2018年8月31日
 */
@Aspect
@Component
@Slf4j
public class SysLogsAspect {
	private Logger logger = LoggerFactory.getLogger(SysLogsAspect.class);
	private final ActionLogger actionLogger;
	private final AdminConfig adminConfig;
	
	private static String moduleType = "x7plus-standalone" ;

	@Autowired
	public SysLogsAspect(AdminConfig adminConfig){
		this.adminConfig = adminConfig;
		actionLogger = ActionLoggerFactory.getActionLogger("sysLogsAspect", StorageType.httpApi);

		ActionLoggerConfig.HttpApi httpApi = new ActionLoggerConfig.HttpApi();
		org.springframework.beans.BeanUtils.copyProperties(adminConfig.getSysLogHttpApi(),httpApi);
		actionLogger.setCurrentHttpApi(httpApi);
	}

	
	@Around("execution(* *..*Controller.*(..)) && @annotation(io.swagger.annotations.ApiOperation)")
	public Object sysLogs(ProceedingJoinPoint joinPoint) throws Throwable{



		Class<?> targetClass = joinPoint.getTarget().getClass();
		String methodName = joinPoint.getSignature().getName();
		
		Method[] methods = targetClass.getMethods();
		// 当前切中的方法
		Method method = null;
		for (int i = 0; i < methods.length; i++){
			if (methods[i].getName() == methodName){
				method = methods[i];
				break;
			}
		}
		ApiResponse apiOperation = method.getAnnotation(ApiResponse.class);
		
		String opeName = apiOperation.description();
		if(StringUtils.isEmpty(opeName)){
			opeName = apiOperation.description();
		}
		ThreadMsgUtil.addMapMsg("sysLogOpeName", opeName);
		// 执行方法前
		Object proceed = null;
		try {
			HttpServletRequest request = HttpUtil.getRequest();
			if(BeanUtils.isNotEmpty(request)) {
				request.setAttribute("enterController", true);
			}
			proceed = joinPoint.proceed();
			// 当前切中的方法
			
			if(BeanUtils.isEmpty(request)) {
				return proceed;
			}

			if (StringUtils.isEmpty(adminConfig.getSysLogHttpApi().getPostUrl())){
				log.warn("请指定系统日志的httpApi");
				return proceed;
			}

			// 执行方法后
		    //访问目标方法的参数：
	        Object[] args = joinPoint.getArgs();
			String reqUrl = request.getRequestURI();
			try {
				String packageName =  targetClass.getPackage().getName();
				StringBuffer sb = new StringBuffer();
				if(BeanUtils.isNotEmpty(args)){
					for (Object object : args) {
						if(object instanceof ServletRequest || object instanceof ServletResponse) {
							continue;
						}
						try {
							String json = JsonUtil.toJson(object);
							sb.append(json);
						} catch (Exception e) {
							sb.append(object.toString());
						}
					}
				}

				// 对于操作内容的记录设置最多记录3999
				String content = sb.toString();
				if(content!=null && content.length() > 3999) {
					content = content.substring(0, 3999);
				}

				String executor = "系统[无用户登录系统]";

				if(StringUtils.isNotEmpty(AuthenticationUtil.getCurrentUserFullname())){
					executor = String.format("%s[%s]",AuthenticationUtil.getCurrentUserFullname(),AuthenticationUtil.getCurrentUsername());  
				}
				SysLog sysLog  = new SysLog();
				sysLog.setId(UniqueIdUtil.getSuid());
				sysLog.setOpeName(opeName);
				sysLog.setModuleType(moduleType);
				sysLog.setReqUrl(reqUrl);
				sysLog.setContent(content);
				sysLog.setType("sysLog");

				sysLog.setTenantId(ContextUtil.getTenantId()==null? BootConstant.PLATFORM_TENANT_ID:ContextUtil.getTenantId());
				// 是否开始配置日志
				boolean recorded = false;
				for (String path : adminConfig.getMustRecordSysLog().keySet()) {
					if (path.matches(reqUrl)){
						if (adminConfig.getMustRecordSysLog().get(path).equals("登录日志")){
							executor = (String) request.getAttribute("loginUser");
						}
						sysLog.setLogType(adminConfig.getMustRecordSysLog().get(path));
						recorded = true;
					}
				}

				if (!recorded) {
					for (String name : adminConfig.getSysLogsAspectPackageName()) {
						if (packageName.startsWith(name) || name.equals("all")) {
							sysLog.setLogType("操作日志");
							recorded = true;
							break;
						}
					}
				}

				if (!recorded) {
					logger.warn("["+packageName+"]并未配置日志切面");
					return proceed;
				}

				sysLog.setExecutor(executor);
				sysLog.setId(WebUtil.getIpAddr(request));
				ActionLog actionLog = new ActionLog();
				actionLog.setId(sysLog.getId());
				actionLog.setLoggerName(actionLogger.getLoggerName());
				actionLog.setAction("SysLogsAspect");
				actionLog.setData(sysLog);
				actionLog.setUid(executor);

				//SysLogsUtil代替品
				actionLogger.writeLog(actionLog);

//				SysLogsUtil.addSysLogs(JsonUtil.getString(objectNode, "id"),
//						opName,
//						executor,
//						ip,
//						logType,
//						JsonUtil.getString(objectNode, "moduleType"),
//						reqUrl,
//						JsonUtil.getString(objectNode, "content"));


			} catch (Exception e) {
				logger.error("保存操作日志失败。" + ExceptionUtil.getExceptionMessage(e));
			}
			
		} catch (Throwable e) {
			throw e;
		}
		return proceed;
	}

	@Data
	public static class SysLog{
		private String id;
		private String opeName;
		private String moduleType;
		private String reqUrl;
		private String content;
		private String type;
		private String tenantId;
		private String logType;
		private String executor;
		private String ip;
	}

}
