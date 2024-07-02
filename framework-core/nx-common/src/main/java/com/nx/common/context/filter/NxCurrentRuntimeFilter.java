package com.nx.common.context.filter;


import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.ThreadLocalContext;
import com.nx.common.exception.BaseException;
import com.nx.common.model.Result;
import com.nx.common.model.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import static com.nx.common.context.constant.NxRequestHeaders.*;


@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE-1000)
@WebFilter(filterName = "currentRuntimeFilter", urlPatterns = "/*")
public class NxCurrentRuntimeFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) res;
        final HttpServletRequest request = (HttpServletRequest) req;

        ThreadLocalContext.unset();
        Enumeration<String> headerNames = request.getHeaderNames();
        String headerName;
        while(headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            if (headerName.equals(HEADER_AUTH_USER)){
                continue;
            }
            CurrentRuntimeContext.addContextHeader(headerName,request.getHeader(headerName));
        }


        chain.doFilter(req, res);
    }



    @Override
    public void init(final FilterConfig filterConfig) {
        log.info("LogFilter初始化");
    }

    @Override
    public void destroy() {
        ThreadLocalContext.unset();
    }



}