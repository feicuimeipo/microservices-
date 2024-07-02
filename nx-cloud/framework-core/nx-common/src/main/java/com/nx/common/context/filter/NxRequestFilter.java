package com.nx.common.context.filter;

import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.ThreadLocalContext;
import com.nx.common.context.constant.ServiceProtocol;
import com.nx.common.context.spi.ServiceVerification;
import com.nx.common.crypt.Base64;
import com.nx.common.exception.BaseException;
import com.nx.common.tracing.NxTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static com.nx.common.context.constant.NxRequestHeaders.*;


@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "nxBaseRequestFilter", urlPatterns = "/*")
@Slf4j
public class NxRequestFilter implements Filter {

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

       //traceId
        String requestId = request.getHeader(NxTraceUtil.TRACE_ID_NAME);
        if (isBlank(requestId)) {
            requestId = NxTraceUtil.generateTraceId();
        }
        //HEADER_REFERER_PROTOCOL, GlobalConstants.REFERER_FEIGN
        MDC.put(NxTraceUtil.TRACE_ID_NAME, requestId);
        log.info("LogFilter触发request_id:{}",MDC.get(NxTraceUtil.TRACE_ID_NAME));

        try{
            String protocol =  response.getHeader(HEADER_SERVICE_REFERER);
            if (protocol!=null && protocol.equals(ServiceProtocol.feign.code())) {
                //过滤芯请求
                String appId = response.getHeader(HEADER_SERVICE_APP_ID);
                String accessToken = Base64.decodeBase64(response.getHeader(HEADER_SERVICE_ACCESS_TOKEN));
                //TODO: 校验, 不能过抛出异常
                //String path = request.getRequestURI();
                if (!ServiceVerification.apiAccessCheck(appId, accessToken,  ServiceProtocol.feign)){
                    String msg =  String.format("feign接口认证未通过(appId:%s,accessToken:%s,encrypt:%s)！",appId,accessToken);
                    throw new BaseException(403,msg);
                };
            }
            chain.doFilter(req, res);
        }finally {
            MDC.remove(NxTraceUtil.TRACE_ID_NAME);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        log.info("LogFilter初始化");
    }

    @Override
    public void destroy() {
        MDC.remove(NxTraceUtil.TRACE_ID_NAME);
        ThreadLocalContext.unset();
    }

    private static boolean isBlank(String value){
        return value ==null || value.trim().length()==0;
    }
}