package com.nx.auth.service.conf;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nx.auth.conf.AuthConfig;
import com.nx.utils.JsonUtil;
import com.nx.api.crypt.Base64;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;

/**
 * 单点登录配置文件
 * @author liyg
 * @Date 2018-08-07
 */
@ConfigurationProperties(prefix = "nx.auth")
@Configuration
@Data
public class SsoConfig {
    private final AuthConfig authConfig;
    public final static String MODE_CAS = AuthConfig.MODE.CAS.code();
    public final static String MODE_OAUTH2 = AuthConfig.MODE.OAuth2.code();
    public final static String MODE_JWT = AuthConfig.MODE.JWT.code();

    @Autowired
    public SsoConfig(AuthConfig authConfig){
        this.authConfig = authConfig;
    }

    // 是否开启单点登录
    private boolean ssoEnabled=true;
    // 单点登录模式


    // cas配置
    private Cas cas;
    // oauth配置
   private Oauth2 oauth2;


    static class Cas {
        // 基础地址
        private String url;
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Data
    static class Oauth2 {
        // 基础地址
        private String url;
        // 登录路径
        private String loginPath;
        // 获取token的路径
        private String tokenPath;
        // 检查token的路径
        private String checkPath;
        // 客户端ID
        private String clientId;
        // 客户端秘钥
        private String secret;
    }

    public String getCasUrl() {
        return cas.getUrl();
    }
    /**
     * 获取单点登录地址
     * @return
     */
    public String getSsoUrl() {
        String ssoUrl = null;
        if(ssoEnabled) {
            if(MODE_CAS.equals(authConfig.getMode())) {
                ssoUrl = cas.getUrl() + "?service=";
            } else if(MODE_OAUTH2.equals(authConfig.getMode())) {
                String stufix = String.format("%s?response_type=code&client_id=%s&client_secret=%s&redirect_uri=", oauth2.getLoginPath(), oauth2.getClientId(), oauth2.getSecret());
                ssoUrl = oauth2.getUrl() + stufix;
            }
        }
        return ssoUrl  + "?service=";
    }
    /**
     * 获取单点退出地址
     * @return
     */
    public String getSsoLogoutUrl() {
        String ssoLogoutUrl = null;
        if(ssoEnabled) {
            if(MODE_CAS.equals(authConfig.getMode())) {
                ssoLogoutUrl = cas.getUrl() + "/logout?service=";
            }else if(MODE_OAUTH2.equals(authConfig.getMode())) {
                ssoLogoutUrl = oauth2.getUrl() + "/logout?redirect_uri=";
            }
        }
        return ssoLogoutUrl + "/logout?service=";
    }
    /**
     * 获取oauth请求token的地址
     * @return
     */
    public String getOauthTokenUrl() {
        String url = null;

        if(ssoEnabled && authConfig.getMode().equals(AuthConfig.MODE.OAuth2.code())) {
            String stufix = String.format("%s?grant_type=authorization_code&client_id=%s&client_secret=%s", oauth2.getTokenPath(), oauth2.getClientId(), oauth2.getSecret());
            url = oauth2.getUrl() + stufix;
        }
        return url;
    }


    /**
     * 获取oauth验证token的地址
     * @return
     */
    public String getOauthCheckUrl() {
        String url = null;

        if(ssoEnabled && MODE_OAUTH2.equals(authConfig.getMode())) {
            String stufix = String.format("%s?token=", oauth2.getCheckPath());
            url = oauth2.getUrl() + stufix;
        }
        return url;
    }


    /**
     * 获取oauth认证时的basic头部
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getOauthBasicHeader() {
        String basicStr = oauth2.getClientId() + ":" + oauth2.getSecret();
        ObjectNode objectNode = JsonUtil.getMapper().createObjectNode();
        objectNode.put("Authorization", "Basic " + Base64.encodeBase64(basicStr));
        String json = objectNode.toString();
        return Base64.encodeBase64(json);
    }


}
