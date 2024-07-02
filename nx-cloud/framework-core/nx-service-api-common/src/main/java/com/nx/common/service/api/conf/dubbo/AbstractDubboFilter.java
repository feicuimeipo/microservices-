package com.nx.common.service.api.conf.dubbo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import static com.nx.common.tracing.NxTraceUtil.*;


/**
 *
 * 使用 Logback 的 MDC 机制，在日志模板中加入 traceId 标识，取值方式为 %X{traceId}
 * 系统入口（api网关）创建 traceId 的值
 * 使用 MDC 保存 traceId
 * 修改 logback 配置文件模板格式添加标识 %X{traceId}
 * ----
 * MDC（Mapped Diagnostic Context，映射调试上下文）是 log4j 和 logback 提供的一种方便在多线程条件下记录日志的功能。
 *
 * <dubbo:provider filter="providerFilter" />
 * <dubbo:consumer filter="consumerFilter" />
 *
 * Sa-Token 整合 Dubbo Consumer端过滤器
 */
@Slf4j
public abstract class AbstractDubboFilter {

    protected void handleTraceId() {
        RpcContext context = RpcContext.getContext();
        if (context.isConsumerSide()) {
            putTraceInto(context);
        } else if (context.isProviderSide()) {
            getTraceFrom(context);
        }
    }

    protected void printResponse(Invocation invocation, Result result, long spendTime) {
        DubboResponseDTO responseDTO = new DubboResponseDTO();
        responseDTO.setInterfaceClassName(invocation.getInvoker().getInterface().getName());
        responseDTO.setMethodName(invocation.getMethodName());
        responseDTO.setResult(JSON.toJSONString(result.getValue()));
        responseDTO.setSpendTime(spendTime);
    }


    protected void printRequest(Invocation invocation) {
        DubboRequestDTO requestDTO = new DubboRequestDTO();
        requestDTO.setInterfaceClass(invocation.getInvoker().getInterface().getName());
        requestDTO.setMethodName(invocation.getMethodName());
        requestDTO.setArgs(getArgs(invocation));
        log.info("RPC请求开始 , {}", requestDTO);
    }

    protected Object[] getArgs(Invocation invocation) {
        Object[] args = invocation.getArguments();
        args = Arrays.stream(args).filter(arg -> {
            // 过滤大参
            if (arg instanceof byte[] || arg instanceof Byte[] || arg instanceof InputStream || arg instanceof File) {
                return false;
            }
            return true;
        }).toArray();
        return args;
    }


    @Data
    public class DubboResponseDTO implements Serializable {
        private String interfaceClassName;
        private String methodName;
        private String result;
        private long spendTime;
    }

    @Data
    public static class DubboRequestDTO implements Serializable {
        private String interfaceClass;
        private String methodName;
        private Object[] args;
    }


    //dubbo-skyworking
    protected static void getTraceFrom(RpcContext context) {
        String traceId = context.getAttachment(TRACE_ID_NAME);
        if (!isBlank(traceId)) {
            setTraceId(traceId);
        }
        String uri = context.getAttachment(EXTENDED_INFO_NAME);
        if (!isBlank(uri)) {
            MDC.put(EXTENDED_INFO_NAME, uri);
        }
    }

    protected static void putTraceInto(RpcContext context) {
        String traceId = MDC.get(TRACE_ID_NAME);
        if (!isBlank(traceId)) {
            context.setAttachment(TRACE_ID_NAME, traceId);
        }

        String uri = MDC.get(EXTENDED_INFO_NAME);
        if (!isBlank(uri)) {
            context.setAttachment(EXTENDED_INFO_NAME, uri);
        }
    }

    protected static boolean isBlank(String traceId){
        return traceId ==null || traceId.trim().length()==0;
    }

}
