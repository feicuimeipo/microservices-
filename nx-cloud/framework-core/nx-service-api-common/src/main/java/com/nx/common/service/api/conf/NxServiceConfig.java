package com.nx.common.service.api.conf;

import com.nx.common.banner.BannerUtils;
import com.nx.common.context.constant.ServiceProtocol;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * token
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nx.service.consumer")
public class NxServiceConfig {

    private String accessToken="feignCallEncry";
    private String appId = "default";
    private String address="http://localhost:8080";
    private String version="v1.0.0";
    private ServiceProtocol protocol;

    public static String appId(){
        return Handler.INSTANCE.getAppId();
    }

    public static String accessToken(){
        return Handler.INSTANCE.getAccessToken();
    }


    public static String address(){
        return Handler.INSTANCE.getAddress();
    }

    public static String version(){
        return Handler.INSTANCE.getVersion();
    }


    public static void printBanner() {

        String str = "" + "       _                                     _          \n"
                + " _ __ | |__   __ _ _ __ _ __ ___   ___ _   _| |__   ___ \n"
                + "| '_ \\| '_ \\ / _` | '__| '_ ` _ \\ / __| | | | '_ \\ / _ \\\n"
                + "| |_) | | | | (_| | |  | | | | | | (__| |_| | |_) |  __/\n"
                + "| .__/|_| |_|\\__,_|_|  |_| |_| |_|\\___|\\__,_|_.__/ \\___|\n"
                + "|_|                                                     " + "\r\n"
                + Handler.INSTANCE.getAddress()
                + " (" + Handler.INSTANCE.getVersion() + ")\n\n";

        BannerUtils.push(new BannerUtils.BannerInfo(NxServiceConfig.class,"",new String[]{"nx-service-api-common enabled!"}));
        BannerUtils.push(new BannerUtils.BannerInfo(NxServiceConfig.class,"",new String[]{str}));
    }

    private static class Handler {
        static NxServiceConfig INSTANCE =  new NxServiceConfig();
    }
}
