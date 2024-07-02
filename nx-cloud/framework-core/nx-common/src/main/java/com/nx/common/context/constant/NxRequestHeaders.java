package com.nx.common.context.constant;

import com.nx.common.tracing.NxTraceUtil;

public interface NxRequestHeaders {
    //头认息
    String HEADER_TRACE_ID =         NxTraceUtil.TRACE_ID_NAME;
    String HEADER_SYSTEM_ID =        "x-system-id";
    //服务间的接口访问
    String HEADER_SERVICE_REFERER =         "service-protocol"; //feign | dubbo
    String HEADER_SERVICE_APP_ID  =         "service-app-id";   //应用id
    String HEADER_SERVICE_ACCESS_TOKEN =    "service-accessToken";

    //应用访问
    String HEADER_JWT_AUTHORITIES    =  "Authorization";    //用户token
    String HEADER_TENANT_ID          =  "TENANT_ID";        //租户
    String HEADER_IGNORE_TENANT_ID   =  "HEADER_IGNORE_TENANT_ID";       //是否忽略当前租户
    String HEADER_I18N               =  "I18N";            //国际化
    String HEADER_AUTH_USER          =  "AUTH_USER";             //多线程上下文头信息

}
