package com.nx.auth.security.denied;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当用户在没有授权的时候，返回的指定信息
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    @Qualifier("myHttpSessionRequestCache")
    private RequestCache requestCache;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        //重定向到登录页
//        if (!response.isCommitted()) {
//            //Save Target-Request
//            requestCache.saveRequest(request, response);
//            //Forward to the login page
//            request.getRequestDispatcher("/loginPage").forward(request, response);
//        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e==null?"用户访问没有授权资源":e.getMessage());
    }

}
