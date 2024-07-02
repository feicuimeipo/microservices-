package com.hotent.base.conf;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.annotation.Resource;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "pharmcube.admin")
@Data
public class AdminConfig {
    /**
     * 默认全部
     */
    private Set<String> sysLogsAspectPackageName= new HashSet();
    {
        sysLogsAspectPackageName.add("all");
    }

    /**
     * 必须登记的系统日志
     */
    private Map<String,String> mustRecordSysLog = new HashMap();
    {
        mustRecordSysLog.put("/sso/auth","登录日志");
        mustRecordSysLog.put("/auth","登录日志");
    }

    private SysLogHttpApi sysLogHttpApi= new SysLogHttpApi();

    @Data
    public static class SysLogHttpApi {
        private String accessToken;
        private String secret;
        private String postUrl="http://localhost:8088/admin/syslog/v1";
    }






}
