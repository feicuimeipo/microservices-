package com.nx.boot.config;

import com.nx.common.banner.BannerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName XxlJobConfig
 * @Description TODO
 * @Author 李昊
 * @Date 2022/6/6 11:11
 * @Version 1.0
 **/
@Configuration
@ConditionalOnClass(com.xxl.job.core.executor.impl.XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = "xxl.job",name = "enabled",havingValue = "true",matchIfMissing = true)
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    public XxlJobConfig(){

    }

    @Value("${xxl.job.admin.addresses:''}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken:''}")
    private String accessToken;

    @Value("${xxl.job.executor.appname:''}")
    private String appname;

    @Value("${xxl.job.executor.address:''}")
    private String address;

    @Value("${xxl.job.executor.ip:''}")
    private String ip;

    @Value("${xxl.job.executor.port:''}")
    private int port;

    @Value("${xxl.job.executor.logpath:''}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:''}")
    private int logRetentionDays;


    @Bean
    public com.xxl.job.core.executor.impl.XxlJobSpringExecutor xxlJobSpringExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        com.xxl.job.core.executor.impl.XxlJobSpringExecutor xxlJobSpringExecutor = new com.xxl.job.core.executor.impl.XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter：xxljob enabled"});

        return xxlJobSpringExecutor;
    }



}