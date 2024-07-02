package com.nx.common.tracing;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.UUID;


public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                String traceId = MDC.get(NxTraceUtil.TRACE_ID_NAME);
                if (traceId==null || traceId.length()==0) {
                    traceId = UUID.randomUUID().toString();
                    MDC.put(NxTraceUtil.TRACE_ID_NAME, traceId);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}