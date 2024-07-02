package com.alibaba.nacos.client.auth.impl.process;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.client.auth.impl.NacosAuthLoginConstant;
import com.alibaba.nacos.client.utils.ContextPathUtil;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.auth.api.LoginIdentityContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import com.nx.boot.launch.env.NxEnvironment;
import com.nx.common.context.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.client.naming.utils.UtilAndComs.webContext;
import static com.alibaba.nacos.common.constant.RequestUrlConstants.HTTP_PREFIX;

/**
 * Login processor for Http.
 *
 * @author Nacos
 */
public class HttpLoginProcessor implements LoginProcessor {
    
    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger(HttpLoginProcessor.class);
    
    private static final String LOGIN_URL = "/v1/auth/users/login";
    
    private final NacosRestTemplate nacosRestTemplate;
    
    public HttpLoginProcessor(NacosRestTemplate nacosRestTemplate) {
        this.nacosRestTemplate = nacosRestTemplate;
    }
    
    @Override
    public LoginIdentityContext getResponse(Properties properties)  {
        
        String contextPath = ContextPathUtil
                .normalizeContextPath(properties.getProperty(PropertyKeyConst.CONTEXT_PATH, webContext));

        String configServer = "";
        try{
            Class.forName("com.nx.boot.launch.NxSpringBootApplicationBuilder");
            NxEnvironment bootstrap = NxSpringBootApplicationBuilder.getNxEnvironment();
            if (bootstrap.isConfigEnabled()) {
                configServer = bootstrap.getCloudServerAddr();
            }
        }catch (ClassNotFoundException e){
            configServer =  SpringUtils.Env.getProperty("spring.cloud.nacos.config.server-addr",null);
        }

        String url = HTTP_PREFIX + configServer + contextPath + LOGIN_URL;

        if (configServer.contains(Constants.HTTP_PREFIX)) {
            url = configServer + contextPath + LOGIN_URL;
        }
        
        Map<String, String> params = new HashMap<String, String>(2);
        Map<String, String> bodyMap = new HashMap<String, String>(2);
        params.put(PropertyKeyConst.USERNAME, properties.getProperty(PropertyKeyConst.USERNAME, StringUtils.EMPTY));
        bodyMap.put(PropertyKeyConst.PASSWORD, properties.getProperty(PropertyKeyConst.PASSWORD, StringUtils.EMPTY));
        try {
            HttpRestResult<String> restResult =
                    nacosRestTemplate.postForm(url, Header.EMPTY, Query.newInstance().initParams(params), bodyMap, String.class);

            if (!restResult.ok()) {
                SECURITY_LOGGER.error("login failed: {}", JacksonUtils.toJson(restResult));
                return null;
            }
            JsonNode obj = JacksonUtils.toObj(restResult.getData());
    
            LoginIdentityContext loginIdentityContext = new LoginIdentityContext();

            if (obj.has(Constants.ACCESS_TOKEN)) {
                loginIdentityContext.setParameter(NacosAuthLoginConstant.ACCESSTOKEN, obj.get(Constants.ACCESS_TOKEN).asText());
                loginIdentityContext.setParameter(NacosAuthLoginConstant.TOKENTTL, obj.get(Constants.TOKEN_TTL).asText());
            } else {
                SECURITY_LOGGER.info("[NacosClientAuthServiceImpl] ACCESS_TOKEN is empty from response");
            }
            return loginIdentityContext;
        } catch (Exception e) {
            SECURITY_LOGGER.error("[NacosClientAuthServiceImpl] login http request failed url: " + "{}, params: {}, bodyMap: {}, errorMsg: {}, {}",
                    url, params, bodyMap, e.getMessage(), e);
            return null;
        }
    }
    
}
