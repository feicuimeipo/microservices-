package com.nx.logger.config;

import com.nx.common.banner.BannerUtils;
import com.nx.logger.enums.LogStorageType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConditionalOnMissingBean(NxLoggerConfig.class)
@ConfigurationProperties(prefix = "nx.logger")
public class NxLoggerConfig {
    public NxLoggerConfig(){
        BannerUtils.push(new BannerUtils.BannerInfo(this.getClass(),"http://www.915zb.com",new String[]{"nx-logging-starter："+ this.getClass().getSimpleName() +" enabled"}));
    }

    @Value("${spring.application.name:'-'}")
    private String applicationName="-";

    @Value("${spring.profiles.active:'dev'}")
    private String profile="dev";


    private LogStorageType logStorageType = LogStorageType.spi;
    private HttpApi httpApi=new HttpApi();
    private FileLog fileLog=new FileLog();
    private MongodbConfig mongodb=new MongodbConfig();

    @Data
    public static class FileLog {
        private String logPath="../logs/";
        private String fileName = "nx_logger";
        private int maxHistory = 30; //单位天
        private int maxFileSize = 100; //单位MB
        private String charset = "UTF-8";
    }

    @Data
    public static class MongodbConfig{
        private String host;
        private int port = 27017;
        private String dbName;
        private String username;
        private String password;
    }

    @Data
    public static class HttpApi{
        private String accessToken;
        private String secret;
        private String postUrl;
    }




}
