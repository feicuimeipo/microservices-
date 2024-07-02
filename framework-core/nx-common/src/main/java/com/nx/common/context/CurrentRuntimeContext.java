package com.nx.common.context;

import com.nx.common.exception.BaseException;
import com.nx.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nx.common.context.spi.*;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static com.nx.common.context.constant.NxRequestAttribute.*;
import static com.nx.common.context.constant.NxRequestHeaders.*;

/**
 * 用户登录之后,获取
 */
public interface CurrentRuntimeContext{

    static final Long DEFAULT_TENANT_ID=-1l;

    static Logger log = LoggerFactory.getLogger(CurrentRuntimeContext.class);

    static List<String> contextHeaders = new ArrayList(){{
        add(HEADER_TRACE_ID);
        add(HEADER_SERVICE_REFERER);
        add(HEADER_SERVICE_APP_ID);
        add(HEADER_SERVICE_ACCESS_TOKEN);
        add(HEADER_JWT_AUTHORITIES);
        add(HEADER_TENANT_ID);
        add(HEADER_IGNORE_TENANT_ID);
        add(HEADER_I18N);
        add(HEADER_AUTH_USER);
    }};


    static LoginUser getCurrentUser() {
        String token = ThreadLocalContext.get(HEADER_JWT_AUTHORITIES);
        if (StringUtils.hasLength(token)){
            LoginUser loginUser = UserContextHolder.getCurrentUserByToken(token);
            ThreadLocalContext.set(HEADER_AUTH_USER, loginUser.encode());
            return loginUser;
        }
        if (StringUtils.hasLength(ThreadLocalContext.get(HEADER_AUTH_USER))){
            String value =  ThreadLocalContext.get(HEADER_AUTH_USER);
            return LoginUser.decode(value);
        }
        return null;
    }

    static Long getCurrentUserId(){
        Long userId = getCurrentUser()==null?null:getCurrentUser().getId();
        return userId;
    }


    static String getUserToken(){
        String token = ThreadLocalContext.get(HEADER_JWT_AUTHORITIES);
        return token;
    }

    //上下文获取
    static Long getTenantId() {
        Long tenantId = ThreadLocalContext.get(HEADER_TENANT_ID);
        return  tenantId ==null? (getCurrentUser()!=null?getCurrentUser().getTenantId():null):tenantId;
    }


    static void  setCurrentUser(LoginUser loginUser) {
        if (loginUser != null) {
            ThreadLocalContext.set(HEADER_AUTH_USER, loginUser.encode());
        }
    }


    static void  setTenantId(Long tenantId) {
        if (tenantId == null) {
            return;
        } else {
            ThreadLocalContext.set(HEADER_TENANT_ID, tenantId);
        }
    }

    static void  removeCurrentTenantId() {
        ThreadLocalContext.remove(HEADER_TENANT_ID);
    }

    static void  removeCurrentUser() {
        ThreadLocalContext.remove(HEADER_AUTH_USER);
    }

    static void  removeContextHeader(String name) {
        ThreadLocalContext.remove(name);
    }

    static void addContextHeader(String name,String value) {
        if (name.equals(HEADER_JWT_AUTHORITIES)){
            LoginUser user =  UserContextHolder.getCurrentUserByToken(value);
            if (user != null) {
                ThreadLocalContext.set(HEADER_AUTH_USER, user.encode());
            }else{
                user = LoginUser.decode(value);
                ThreadLocalContext.set(HEADER_AUTH_USER, user);
            }
        }else if(name.equals(HEADER_AUTH_USER)){
            LoginUser user = LoginUser.decode(value);
            ThreadLocalContext.set(HEADER_AUTH_USER, user);
        }else if(contextHeaders.contains(name)){
            if (value==null || value.trim().length()==0)
                return;
            ThreadLocalContext.set(name, value);
            if (log.isTraceEnabled()) {
                log.trace("set current context:[{}={}]", name, value);
            }
        }
    }

    static Map<String, String> getContextHeaders() {
        Map<String, String> map = new HashMap<>();
        String headerVal;
        for (String headerName : contextHeaders) {
            if (headerName.equals(HEADER_AUTH_USER)){
                headerVal = getCurrentUser().encode();
            }else {
                headerVal = getContextHeader(headerName, false);
            }
            if(headerVal == null)continue;
            map.put(headerName, headerVal);
        }
        return map;
    }

    static String getContextHeader(String name) {
        return getContextHeader(name,false);
    }

    static String getContextHeader(String headerName, boolean validate) {
        String value = ThreadLocalContext.getStringValue(headerName);
        if (validate && (value ==null || value.trim().length()==0)) {
            throw new BaseException(500, "无法获取上下文[" + headerName + "]信息");
        }
        return value;
    }


//    下列是从请求头中获得，兼职其他场景
//    static Integer getLoginUserType(HttpServletRequest request){
//        Integer userType = getCurrentUser()==null?null:getCurrentUser().getUserType().getCode();
//        if (request == null) {
//            return null;
//        }
//        if (userType==null && request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE)!=null){
//            userType =(Integer) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE);
//        }
//        return userType;
//    }

    static Long getTenantId(HttpServletRequest request){
        Long tenantId = getTenantId();

        if (tenantId==null && request!=null && request.getHeader(HEADER_TENANT_ID)!=null){
            tenantId = Long.valueOf(request.getHeader(HEADER_TENANT_ID));
        }
        return tenantId;
    }

    public static void setCurrentUserId(ServletRequest request, Long userId) {
        if (request!=null) {
            request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
        }
    }

    static Long getCurrentUserId(HttpServletRequest request){
        Long userId = getCurrentUser()==null?null:getCurrentUser().getId();
        if (request == null) {
            return null;
        }
        if (userId==null && request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID)!=null){
            userId =(Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
        }
        return userId;
    }


    public static void setCommonResult(ServletRequest request, Result<?> result) {
        if (request!=null) {
            request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
        }
    }

    public static Result<?> getCommonResult(ServletRequest request) {
        if (request!=null && request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT)!=null) {
            Object obj = request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
            if (obj instanceof Result){
                return (Result<?>) obj;
            }
        }
        return null;
    }

}
