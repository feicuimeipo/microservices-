package com.nx.auth.conf;


import com.nx.api.context.CustomRequestHeaders;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 *
 *
 @Value("${nx.auth.jwt.header:'Authorization'}")
 String tokenHeader;

 @Value("${nx.auth.jwt.route.path:'/auth'}")
 String authenticationPath;

 @Value("${nx.auth.feign.encry.key:feignCallEncry}")
 private String encryKey;

 //@Value("${hotent.security.deny.httpUrls:''}")
 @Value("${nx.auth.security.ignore.httpUrls:''}")
 String permitAll;

 //@Value("${hotent.security.deny.httpUrls:''}")
 @Value("${nx.auth.security.deny.httpUrls:''}")
 String denyAll;

 * 表单形式，oauth2形式
 */
@Data
@Configuration
@ConditionalOnMissingBean(AuthConfig.class)
@ConfigurationProperties(prefix = "nx.auth")
public class AuthConfig {

    protected String mode= MODE.JWT.code; //oauth2,form-basic

    // 是否只允许单用户登录
    protected boolean single=false;

    //有效期
    protected Long expiration=604800l;

    //@Value("${nx.auth.path:'/auth'}")
    protected String authenticationPath="/auth";


    //@Value("${nx.auth.security.ignore.httpUrls:''}")
    protected  String permitAll;


    //@Value("${nx.auth.security.deny.httpUrls:''}")
    protected String denyAll;


    protected JWT jwt=new JWT();

    /**
     * 过期时间，单位分钟
     */
    //@Ignore
    private int   overTime=30;

    public int getOverTime(){
        if (overTime>(60*12)){
            overTime = 60*12;
        }else if (overTime<15){
            overTime = 15;
        }
        return overTime;
    }

    public String getTokenHeader() {
        return this.getJwt().getHeader();
    }

    @Data
    public static class JWT{
        //@Value("${nx.auth.jwt.header:'Authorization'}")
        private String header=CustomRequestHeaders.HEADER_JWT_AUTHORITIES;

        //@Value("${nx.auth.jwt.secret:'eip7secret'}")
        private String secret="'eip7secret'";

        // 过期时间，默认为7天 604800秒
        //@Value("${nx.auth.jwt.expiration:604800}")
        private Long expiration= 604800l;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }


        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }
    }

    public enum MODE {
        CAS("cas"),OAuth2("oauth2"),JWT("jwt");
        String code;
        MODE(String code) {
            this.code = code;
        }

        public String code(){
            return this.code;
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }



    public String getAuthenticationPath() {
        return authenticationPath;
    }

    public void setAuthenticationPath(String authenticationPath) {
        this.authenticationPath = authenticationPath;
    }

    public String getPermitAll() {
        if (permitAll==null){
            permitAll = "";
        }
        return permitAll;
    }

    public void setPermitAll(String permitAll) {
        this.permitAll = permitAll;
    }

    public void addPermitPath(String path) {
        if ( this.permitAll==null){
            this.permitAll = "";
        }
        if (StringUtils.hasText(path)){
            if (path.endsWith("/**")){
                this.permitAll +=  "," + path;
            }else{
                this.permitAll +=  "," + path + "/**";
            }
        }

    }

    public String getDenyAll() {
        if (denyAll==null){
            denyAll = "";
        }
        return denyAll;
    }

    public void setDenyAll(String denyAll) {
        this.denyAll = denyAll;
    }

    public JWT getJwt() {
        return jwt;
    }

    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }


}
