package com.nx.elasticsearch.conf;

import com.nx.common.context.SpringUtils;
import lombok.Data;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class EsProperties {
    private static Map<String,EsProperties> esPropertiesMap = new ConcurrentHashMap<>();
    public static String DEFAULT_GROUP_NAME = "es";

    private String clusterName;
    private String ip;
    private int port;
    private int nodeport;
    private String user;
    private String pass;
    private String profile;
    private String[] indices;
    private Set<String> hsLogs;


    @Synchronized
    public static EsProperties getEsProperties(String groupName){
        EsProperties esProperties = esPropertiesMap.get(groupName);
        if (esProperties==null) {
            esProperties = new EsProperties();
            esProperties.clusterName = SpringUtils.Env.getProperty(groupName+".clusterName");
            esProperties.ip = SpringUtils.Env.getProperty(groupName+".host");
            esProperties.port = SpringUtils.Env.getInt(groupName+".urlport",-1);
            esProperties.nodeport = SpringUtils.Env.getInt(groupName+".nodeport",-1);
            esProperties.user = SpringUtils.Env.getProperty(groupName+".user");
            esProperties.pass = SpringUtils.Env.getProperty(groupName+".pass");
            esProperties.profile = SpringUtils.Env.getProperty("spring.profiles.active","dev");
            String split  = SpringUtils.Env.getProperty(groupName+".indices","dev");
            String[] arr = StringUtils.split(split,"|");
            esProperties.indices = arr;
            esProperties.hsLogs = (Set) Arrays.stream(arr).collect(Collectors.toSet());

            esPropertiesMap.put(groupName,esProperties);
        }
        return esProperties;
    }

   // private static EsProperties esProperties;
    private EsProperties(){}
}
