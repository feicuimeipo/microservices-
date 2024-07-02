package com.nx.common.context.filter;


import com.nx.common.tracing.NxTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@Order(0)
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
public class NxTraceIdFilter implements Filter {
    /**
     * 日志跟踪标识
     */
    public static final String TRACE_ID = NxTraceUtil.TRACE_ID_NAME;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)   throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        //traceId
        String requestId = request.getHeader(TRACE_ID);
        if (isBlank(requestId)) {
            requestId = NxTraceUtil.generateTraceId();
        }

        MDC.put(TRACE_ID, requestId);
        log.debug("LogFilter触发request_id:{}",MDC.get(TRACE_ID));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }

    private static boolean isBlank(String value){
        return value ==null || value.trim().length()==0;
    }
}
