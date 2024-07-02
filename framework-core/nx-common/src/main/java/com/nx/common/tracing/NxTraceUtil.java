package com.nx.common.tracing;

import org.slf4j.MDC;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;


public class NxTraceUtil {

    public static final String TRACE_ID_NAME = "tid";

    public static final String EXTENDED_INFO_NAME = "extendedInfo";

    /**
     * 获得链路追踪编号，直接返回 SkyWalking 的 TraceId。
     * 如果不存在的话为空字符串！！！
     *
     * @return 链路追踪编号
     */
    public static String getContextTraceId() {
        MDC.get(TRACE_ID_NAME);
        String id = TraceContext.traceId();
        if (id!=null && id.length()>0){
            return MDC.get(TRACE_ID_NAME);
        }
        return id;
    }

    public static void initTrace(String extendedInfo) {
        if (MDC.get(TRACE_ID_NAME) == null) {
            String traceId = generateTraceId();
            setTraceId(traceId);
            if (extendedInfo!=null) {
                MDC.put(EXTENDED_INFO_NAME, extendedInfo);
            }
        }
    }
    public static void setTractIdIfAbsent() {
        initTrace(null);
    }

    public static void setRequestIdIfAbsent() {
        initTrace(null);
    }

    public static void setTraceId(String traceId) {
        traceId =  traceId.substring(0,36);
        MDC.put(TRACE_ID_NAME, traceId);
    }


    //clean
    public static void clearTrace() {
        MDC.clear();
    }



    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }


    // 用于父线程向线程池中提交任务时，将自身MDC中的数据复制给子线程
    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setRequestIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    // 用于父线程向线程池中提交任务时，将自身MDC中的数据复制给子线程
    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setRequestIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

}
