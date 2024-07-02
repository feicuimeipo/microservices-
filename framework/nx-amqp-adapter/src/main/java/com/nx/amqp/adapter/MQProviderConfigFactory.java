package com.nx.amqp.adapter;

import com.nx.amqp.adapter.enums.MQProviderEnum;
import lombok.Synchronized;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MQProviderConfigFactory {
    public static String DEFAULT_GROUP_NAME = "nx.amqp";
    //提供者，默认是kafka
    public   static MQProviderEnum provider=MQProviderEnum.redis;
    public   static boolean enabled = false;

    private static Map<String, MQProviderConfig> configMap = new ConcurrentHashMap();

    public static MQProviderConfig getConfig(){
        return getConfig(provider,DEFAULT_GROUP_NAME);
    }

    @Synchronized
    public static MQProviderConfig getConfig(MQProviderEnum provider, String groupName){
        String key =  provider.getCode() + "_" +groupName;
        MQProviderConfig config = configMap.get(key);

        if (config!=null){
            return config;
        }


            switch (provider) {
                case aliyun_mns:
                    synchronized (MQProviderConfigFactory.class) {
                        if (config == null) {
                            config = new MQProviderConfig(groupName);
                            configMap.put(key, config);
                        }
                    }
                    break;
            }


        return config;

    }
}
