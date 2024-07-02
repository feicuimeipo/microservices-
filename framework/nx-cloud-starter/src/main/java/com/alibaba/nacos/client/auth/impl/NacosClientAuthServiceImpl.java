package com.alibaba.nacos.client.auth.impl;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.auth.impl.process.HttpLoginProcessor;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.auth.api.LoginIdentityContext;
import com.alibaba.nacos.plugin.auth.api.RequestResource;
import com.alibaba.nacos.plugin.auth.spi.client.AbstractClientAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * a ClientAuthService implement.
 *
 * @author wuyfee
 */

public class NacosClientAuthServiceImpl extends AbstractClientAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(NacosClientAuthServiceImpl.class);
    
    /**
     * TTL of token in seconds.
     */
    private long tokenTtl;
    
    /**
     * Last timestamp refresh security info from server.
     */
    private long lastRefreshTime;
    
    /**
     * time window to refresh security info in seconds.
     */
    private long tokenRefreshWindow;
    
    /**
     * A context to take with when sending request to Nacos server.
     */
    private volatile LoginIdentityContext loginIdentityContext = new LoginIdentityContext();
    
    
    /**
     * Login to servers.
     *
     * @return true if login successfully
     */
    
    @Override
    public Boolean login(Properties properties) {
        try {
            if ((System.currentTimeMillis() - lastRefreshTime) < TimeUnit.SECONDS
                    .toMillis(tokenTtl - tokenRefreshWindow)) {
                return true;
            }
            
            if (StringUtils.isBlank(properties.getProperty(PropertyKeyConst.USERNAME))) {
                lastRefreshTime = System.currentTimeMillis();
                return true;
            }
            
            for (String server : this.serverList) {
                HttpLoginProcessor httpLoginProcessor = new HttpLoginProcessor(nacosRestTemplate);
                properties.setProperty(NacosAuthLoginConstant.SERVER, server);
                LoginIdentityContext identityContext = httpLoginProcessor.getResponse(properties);

                if (identityContext != null) {
                    if (StringUtils.isNotBlank(identityContext.getParameter(NacosAuthLoginConstant.ACCESSTOKEN))) {
                        tokenTtl = Long.parseLong(identityContext.getParameter(NacosAuthLoginConstant.TOKENTTL));
                        tokenRefreshWindow = tokenTtl / 10;
                        lastRefreshTime = System.currentTimeMillis();
                        
                        loginIdentityContext = new LoginIdentityContext();
                        loginIdentityContext.setParameter(NacosAuthLoginConstant.ACCESSTOKEN,
                                identityContext.getParameter(NacosAuthLoginConstant.ACCESSTOKEN));
                    }
                    return true;
                }
            }
        } catch (Throwable throwable) {
            log.warn("[SecurityProxy] login failed, error: ", throwable);
            return false;
        }
        return false;
    }
    
    @Override
    public LoginIdentityContext getLoginIdentityContext(RequestResource resource) {
        return this.loginIdentityContext;
    }
    
    @Override
    public void shutdown() throws NacosException {
    
    }
}
