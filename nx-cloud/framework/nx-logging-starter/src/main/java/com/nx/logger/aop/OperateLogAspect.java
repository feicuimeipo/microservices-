
package com.nx.logger.aop;

import com.alibaba.fastjson2.JSONObject;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.spi.LoginUser;
import com.nx.common.model.Result;
import com.nx.common.tracing.NxTraceUtil;
import com.nx.logger.NxLoggerStorageProvider;
import com.nx.logger.enums.OperateTypeEnum;
import com.nx.logger.model.api.dto.OperateLogDTO;
import com.nx.logger.utils.LogWebUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.nx.common.model.constant.ResultCode.INTERNAL_SERVER_ERROR;
import static com.nx.common.model.constant.ResultCode.SUCCESS;

/**
 * 拦截使用 @OperateLogDTO 注解，如果满足条件，则生成操作日志。
 * 满足如下任一条件，则会进行记录：
 * 1. 使用 @ApiOperation + 非 @GetMapping
 * 2. 使用 @OperateLogDTO 注解
 * <p>
 * 但是，如果声明 @OperateLogDTO 注解时，将 enable 属性设置为 false 时，强制不记录。
 * @author 芋道源码
 */
@Aspect
@Slf4j
@ConditionalOnProperty(prefix = "nx.logger.operate-log",value = "enabled",havingValue = "true",matchIfMissing = true)
public class OperateLogAspect {

    public OperateLogAspect(){
        BannerUtils.push(new BannerUtils.BannerInfo(this.getClass(),"",new String[]{"nx-logging-starter："+ this.getClass().getSimpleName() +" enabled"}));
    }

    /**
     * 用于记录操作内容的上下文
     *
     * @see OperateLogDTO#getContent()
     */
    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();
    /**
     * 用于记录拓展字段的上下文
     *
     * @see OperateLogDTO#getExts()
     */
    private static final ThreadLocal<Map<String, Object>> EXTS = new ThreadLocal<>();


    @Around("@annotation(apiOperation)")
    public Object around(ProceedingJoinPoint joinPoint, ApiOperation apiOperation) throws Throwable {
        // 可能也添加了 @ApiOperation 注解
        com.nx.logger.model.annotations.OperateLog operateLog = getMethodAnnotation(joinPoint,
                com.nx.logger.model.annotations.OperateLog.class);
        return around0(joinPoint, operateLog, apiOperation,null);
    }

    @Around("@annotation(apiOperation)")
    public Object around(ProceedingJoinPoint joinPoint, Operation apiOperation) throws Throwable {
        // 可能也添加了 @ApiOperation 注解
        com.nx.logger.model.annotations.OperateLog operateLog = getMethodAnnotation(joinPoint,
                com.nx.logger.model.annotations.OperateLog.class);
        return around0(joinPoint, operateLog, null,apiOperation);
    }


    @Around("!@annotation(io.swagger.annotations.ApiOperation) && @annotation(operateLog)")
    // 兼容处理，只添加 @OperateLogDTO 注解的情况
    public Object around(ProceedingJoinPoint joinPoint,
                         com.nx.logger.model.annotations.OperateLog operateLog) throws Throwable {
        return around0(joinPoint, operateLog, null,null);
    }

    private Object around0(ProceedingJoinPoint joinPoint,
                           com.nx.logger.model.annotations.OperateLog operateLog,
                           ApiOperation apiOperation,Operation operation) throws Throwable {
        // 目前，只有管理员，才记录操作日志！所以非管理员，直接调用，不进行记录
        LoginUser.UserTypeEnum userType = CurrentRuntimeContext.getCurrentUser().getUserType();
        if (!Objects.equals(userType, LoginUser.UserTypeEnum.Admin.getCode())) {
            return joinPoint.proceed();
        }

        // 记录开始时间
        Date startTime = new Date();
        try {
            // 执行原有方法
            Object result = joinPoint.proceed();
            // 记录正常执行时的操作日志
            this.log(joinPoint, operateLog, apiOperation,operation, startTime, result, null);
            return result;
        } catch (Throwable exception) {
            this.log(joinPoint, operateLog, apiOperation,operation, startTime, null, exception);
            throw exception;
        } finally {
            clearThreadLocal();
        }
    }

    public static void setContent(String content) {
        CONTENT.set(content);
    }

    public static void addExt(String key, Object value) {
        if (EXTS.get() == null) {
            EXTS.set(new HashMap<>());
        }
        EXTS.get().put(key, value);
    }

    private static void clearThreadLocal() {
        CONTENT.remove();
        EXTS.remove();
    }

    private void log(ProceedingJoinPoint joinPoint,
                     com.nx.logger.model.annotations.OperateLog operateLog,
                     ApiOperation apiOperation,
                     Operation operation,
                     Date startTime, Object result, Throwable exception) {
        try {
            // 判断不记录的情况
            if (!isLogEnable(joinPoint, operateLog)) {
                return;
            }
            // 真正记录操作日志
            this.writeLog(joinPoint, operateLog, apiOperation,operation, startTime, result, exception);
        } catch (Throwable ex) {
            log.error("[log][记录操作日志时，发生异常，其中参数是 joinPoint({}) operateLog({}) apiOperation({}) result({}) exception({}) ]",
                    joinPoint, operateLog, apiOperation, result, exception, ex);
        }
    }

    private void writeLog(ProceedingJoinPoint joinPoint,
                      com.nx.logger.model.annotations.OperateLog operateLog,
                      ApiOperation apiOperation,
                      Operation operation,
                      Date startTime, Object result, Throwable exception) {
        OperateLogDTO operateLogDTOObj = new OperateLogDTO();
        // 补全通用字段
        operateLogDTOObj.setTraceId(NxTraceUtil.getContextTraceId());
        operateLogDTOObj.setStartTime(startTime);
        // 补充用户信息
        fillUserFields(operateLogDTOObj);
        // 补全模块信息
        fillModuleFields(operateLogDTOObj, joinPoint, operateLog, apiOperation,operation);
        // 补全请求信息
        fillRequestFields(operateLogDTOObj);
        // 补全方法信息
        fillMethodFields(operateLogDTOObj, joinPoint, operateLog, startTime, result, exception);


        // 异步记录日志
        NxLoggerStorageProvider.writeLog(operateLogDTOObj);

    }

    private static void fillUserFields(OperateLogDTO operateLogDTOObj) {
        operateLogDTOObj.setUserId(CurrentRuntimeContext.getCurrentUser().getId());
        operateLogDTOObj.setUserType(CurrentRuntimeContext.getCurrentUser().getUserType().getCode());
    }

    private static void fillModuleFields(OperateLogDTO operateLogDTO,
                                         ProceedingJoinPoint joinPoint,
                                         com.nx.logger.model.annotations.OperateLog operateLog,
                                         ApiOperation apiOperation,
                                         Operation apiOperationV3) {
        // module 属性
        if (operateLog != null) {
            operateLogDTO.setModule(operateLog.module());
        }
        if (StringUtils.isEmpty(operateLogDTO.getModule())) {
            Api api = getClassAnnotation(joinPoint, Api.class);
            if (api != null) {
                // 优先读取 @API 的 name 属性
                if (StringUtils.isNotEmpty(api.value())) {
                    operateLogDTO.setModule(api.value());
                }
                // 没有的话，读取 @API 的 tags 属性
                if (StringUtils.isEmpty(operateLogDTO.getModule()) && ArrayUtils.isNotEmpty(api.tags())) {
                    operateLogDTO.setModule(api.tags()[0]);
                }
            }
        }
        // name 属性
        if (operateLog != null) {
            operateLogDTO.setName(operateLog.name());
        }
        if (StringUtils.isEmpty(operateLogDTO.getName()) && apiOperation != null) {
            operateLogDTO.setName(apiOperation.value());
        }

        if (StringUtils.isEmpty(operateLogDTO.getName()) && apiOperationV3 != null) {
            operateLogDTO.setName(apiOperationV3.summary());
        }
        // type 属性
        if (operateLog != null && ArrayUtils.isNotEmpty(operateLog.type())) {
            operateLogDTO.setType(operateLog.type()[0].getType());
        }
        if (operateLogDTO.getType() == null) {
            RequestMethod requestMethod = obtainFirstMatchRequestMethod(obtainRequestMethod(joinPoint));
            OperateTypeEnum operateLogType = convertOperateLogType(requestMethod);
            operateLogDTO.setType(operateLogType != null ? operateLogType.getType() : null);
        }
        // content 和 exts 属性
        operateLogDTO.setContent(CONTENT.get());
        operateLogDTO.setExts(EXTS.get());
    }

    private static void fillRequestFields(OperateLogDTO operateLogDTOObj) {
        // 获得 Request 对象
        HttpServletRequest request = LogWebUtil.getRequest();
        if (request == null) {
            return;
        }
        // 补全请求信息
        operateLogDTOObj.setRequestMethod(request.getMethod());
        operateLogDTOObj.setRequestUrl(request.getRequestURI());
        operateLogDTOObj.setUserIp(LogWebUtil.getClientIP(request));
        operateLogDTOObj.setUserAgent(LogWebUtil.getUserAgent(request));
    }

    private static void fillMethodFields(OperateLogDTO operateLogDTOObj,
                                         ProceedingJoinPoint joinPoint,
                                         com.nx.logger.model.annotations.OperateLog operateLog,
                                         Date startTime, Object result, Throwable exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        operateLogDTOObj.setJavaMethod(methodSignature.toString());
        if (operateLog == null || operateLog.logArgs()) {
            operateLogDTOObj.setJavaMethodArgs(obtainMethodArgs(joinPoint));
        }
        if (operateLog == null || operateLog.logResultData()) {
            operateLogDTOObj.setResultData(obtainResultData(result));
        }
        operateLogDTOObj.setDuration((int) (System.currentTimeMillis() - startTime.getTime()));
        // （正常）处理 resultCode 和 resultMsg 字段
        if (result instanceof Result) {
            Result<?> commonResult = (Result<?>) result;
            operateLogDTOObj.setResultCode(commonResult.getCode());
            operateLogDTOObj.setResultMsg(commonResult.getMsg());
        } else {
            operateLogDTOObj.setResultCode(SUCCESS.getCode());
        }
        // （异常）处理 resultCode 和 resultMsg 字段
        if (exception != null) {
            operateLogDTOObj.setResultCode(INTERNAL_SERVER_ERROR.getCode());
            operateLogDTOObj.setResultMsg(ExceptionUtils.getRootCauseMessage(exception));
        }
    }

    private static boolean isLogEnable(ProceedingJoinPoint joinPoint,
                                       com.nx.logger.model.annotations.OperateLog operateLog) {
        // 有 @OperateLogDTO 注解的情况下
        if (operateLog != null) {
            return operateLog.enable();
        }
        // 没有 @ApiOperation 注解的情况下，只记录 POST、PUT、DELETE 的情况
        return obtainFirstLogRequestMethod(obtainRequestMethod(joinPoint)) != null;
    }

    private static RequestMethod obtainFirstLogRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtils.isEmpty(requestMethods)) {
            return null;
        }
        return Arrays.stream(requestMethods).filter(requestMethod ->
                requestMethod == RequestMethod.POST
                        || requestMethod == RequestMethod.PUT
                        || requestMethod == RequestMethod.DELETE)
                .findFirst().orElse(null);
    }

    private static RequestMethod obtainFirstMatchRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtils.isEmpty(requestMethods)) {
            return null;
        }
        // 优先，匹配最优的 POST、PUT、DELETE
        RequestMethod result = obtainFirstLogRequestMethod(requestMethods);
        if (result != null) {
            return result;
        }
        // 然后，匹配次优的 GET
        result = Arrays.stream(requestMethods).filter(requestMethod -> requestMethod == RequestMethod.GET)
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // 兜底，获得第一个
        return requestMethods[0];
    }

    private static OperateTypeEnum convertOperateLogType(RequestMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        switch (requestMethod) {
            case GET:
                return OperateTypeEnum.GET;
            case POST:
                return OperateTypeEnum.CREATE;
            case PUT:
                return OperateTypeEnum.UPDATE;
            case DELETE:
                return OperateTypeEnum.DELETE;
            default:
                return OperateTypeEnum.OTHER;
        }
    }

    private static RequestMethod[] obtainRequestMethod(ProceedingJoinPoint joinPoint) {
        RequestMapping requestMapping = AnnotationUtils.getAnnotation( // 使用 Spring 的工具类，可以处理 @RequestMapping 别名注解
                ((MethodSignature) joinPoint.getSignature()).getMethod(), RequestMapping.class);
        return requestMapping != null ? requestMapping.method() : new RequestMethod[]{};
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(annotationClass);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getClassAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getAnnotation(annotationClass);
    }

    private static String obtainMethodArgs(ProceedingJoinPoint joinPoint) {
        // TODO 提升：参数脱敏和忽略
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        // 拼接参数
        Map<String, Object> args = new HashMap<>(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            // 被忽略时，标记为 ignore 字符串，避免和 null 混在一起
            args.put(argName, !isIgnoreArgs(argValue) ? argValue : "[ignore]");
        }
        String json = JSONObject.toJSONString(args);
        return json;
        //return LogJsonUtil.toJsonString(args);
    }

    private static String obtainResultData(Object result) {
        // TODO 提升：结果脱敏和忽略
        if (result instanceof Result) {
            result = ((Result<?>) result).getData();
        }
        String json = JSONObject.toJSONString(result);
        return json;
        //return LogJsonUtil.toJsonString(result);
    }

    private static boolean isIgnoreArgs(Object object) {
        Class<?> clazz = object.getClass();
        // 处理数组的情况
        if (clazz.isArray()) {
            return IntStream.range(0, Array.getLength(object))
                    .anyMatch(index -> isIgnoreArgs(Array.get(object, index)));
        }
        // 递归，处理数组、Collection、Map 的情况
        if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>) object).stream()
                    .anyMatch((Predicate<Object>) OperateLogAspect::isIgnoreArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return isIgnoreArgs(((Map<?, ?>) object).values());
        }
        // obj
        return object instanceof MultipartFile
                || object instanceof HttpServletRequest
                || object instanceof HttpServletResponse
                || object instanceof BindingResult;
    }

}
