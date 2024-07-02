package com.nx.skywalking;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@WebFilter(urlPatterns = {"/*"}, filterName = "traceIdFilter")
@Component
public class RequestTraceIdFilter extends OncePerRequestFilter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RequestTraceIdFilter.class);

    @Value("${nx.skywalking.exclude-uri:''}")
    private String excludeUri;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = request;
        String traceId = httpRequest.getHeader("tractId");
        if (StringUtils.isEmpty(traceId)){
            traceId = SkywalkingContext.traceId();
        }

        MDC.put("tractId", traceId);
        if (UriMatcherUtils.match(this.excludeUri, request.getRequestURI())) {
            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove("tractId");
            }
            return;
        }
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        } finally {
            long end = System.currentTimeMillis();
            saveLogData(traceId, request, response, end - start);
            MDC.remove("tractId");
        }
    }

    private void saveLogData(String traceId, HttpServletRequest request, HttpServletResponse httpResponse, long l) {
        httpResponse.setHeader("tractId", traceId);
        log.info(String.format("%s%s", new Object[] { "tractId", traceId, String.valueOf(l) }));
    }
}