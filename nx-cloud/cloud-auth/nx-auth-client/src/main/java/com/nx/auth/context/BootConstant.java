/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.context;

import com.nx.api.context.GlobalConstants;

public interface BootConstant {

    public static final String SYSTEM_ACCOUNT = "admin";
    /**
     * 平台的租户id
     *
     */
    String PLATFORM_TENANT_ID = GlobalConstants.DEFAULT_TENANT_ID;//"-1" ;
    String REFERER_FEIGN = GlobalConstants.REFERER.feign.code();

    /**
     * 租户有效状态
     */
    String TENANT_STATUS_ENABLE = "enable";


    /**
      * 用户来源(系统) {@value}
      */
     String FROM_SYSTEM = "system";

     /**
      * 用户来源(接口) {@value}
      */
     String FROM_RESTFUL = "restful";
}
