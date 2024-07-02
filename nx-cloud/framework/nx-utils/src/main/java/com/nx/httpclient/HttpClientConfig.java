package com.nx.httpclient;



import com.nx.common.context.SpringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HttpClientConfig {
    private String provider;
    private int readTimeout=10000;
    private int connectTimeout=2000;
    private int chunkSizeLimit=1024 * 1024;
    private List<String> domainsList = new ArrayList<>();
    private Map<String, String> loadBalanceCustomizeMapping = new HashMap<>();
    private Map<String, String> loadBalanceContextPathMapping = new HashMap<>();


    public static String getHttpClientProvider() {
        return SpringUtils.Env.getProperty("nx.http-client.provider");
    }

    public static int getReadTimeout() {
        return SpringUtils.Env.getInt("nx.http-client.read-timeout",10000);
    }

    public  static int getConnectTimeout() {
        return SpringUtils.Env.getInt("nx.http-client.connect-timeout",2000);
    }

    public static int getFileUploadChunkSizeLimit() {
        return SpringUtils.Env.getInt("nx.http-client.file-upload.chunk-size-limit",1024 * 1024);
    }

    //内部域名
    public static List<String> getDomainsList(){
        List<String> list  = SpringUtils.Env.getList("nx.internal.dns.domains");
        return list;
    }

    public static Map<String, String> getLoadBalanceCustomizeMapping(){
        Map<String, String> ret  = SpringUtils.Env.getMappingValues("nx.loadbalancer.customize.mapping");
        return ret;
    }

    public static Map<String, String> getLoadBalanceContextPathMapping(){
        Map<String, String> ret  = SpringUtils.Env.getMappingValues("nx.loadbalancer.context-path.mapping");
        return ret;
    }


}
