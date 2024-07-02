package com.nx.gateway.conf.gateway;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @ClassName GlobalInterceptorAdapter
// * @Description TODO
// * @Author NIANXIAOLING
// * @Date 2022/6/18 21:53
// * @Version 1.0
// **/
//@Slf4j
//@Component
//public class GlobalInterceptorAdapter implements HandlerInterceptor {
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
//            Exception {
//        String scheme = request.getScheme();
//        String serverName = request.getServerName();
//        String realServerName = request.getHeader("realServerName");
//        int port = request.getServerPort();
//        String path = request.getContextPath();
//        String basePath = "";
//        basePath = scheme + "://" + serverName + ":" + port + path;
//        if (log.isDebugEnabled()) {
//            log.debug(basePath);
//        }
//        request.setAttribute("basePath", basePath);
//        return true;
//    }
//
//}