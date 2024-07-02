package com.alibaba.nacos.client.auth.impl.process;

import com.alibaba.nacos.plugin.auth.api.LoginIdentityContext;

import java.util.Properties;


public interface LoginProcessor {
    /**
     * send request to server and get result.
     *
     * @param properties request properties.
     * @return login identity context.
     */
    LoginIdentityContext getResponse(Properties properties) throws ClassNotFoundException;
    
}
