package com.nx.skywalking;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * @ClassName SkywalkingContext
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/7/7 14:23
 * @Version 1.0
 **/
public class SkywalkingContext {

    public static String traceId() {
        return TraceContext.traceId();
    }
}
