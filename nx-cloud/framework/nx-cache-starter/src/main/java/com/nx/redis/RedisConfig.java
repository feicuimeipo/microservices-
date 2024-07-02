package com.nx.redis;


import com.nx.redis.enums.RedisConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import static com.nx.redis.enums.RedisConstant.DEFAULT_MODE;

@Data
@AllArgsConstructor
public class RedisConfig {
    private RedisConfig(){

    }
    public RedisConfig(String groupName){
        this.groupName = groupName;
    }
    private String mode= DEFAULT_MODE.getCode();
    private boolean keyUseStringSerializer= RedisConstant.DEFAULT_KEY_USE_StringSerializer;
    private String valueSerializerType=RedisConstant.DEFAULT_VALUE_USE_valueSerializerType;
    private String groupName;
    private String clientName;
    private String servers;
    private String host;
    private String password;
    private int port;
    private int database=0;
    private int timeout = 5000;
    private JedisPoolConfig pool;
    private Sentinel sentinel;
    private Cluster cluster;


    public void setPassword(String password) {
       if (StringUtils.isEmpty(password)) return;
        this.password = password;
    }

    @Data
    public static class Sentinel{
        private final String groupName;
        private String nodes;
        private String masterName;

    }

    @Data
    public static class Cluster{
        private String nodes;
        private String masterName;
        private int maxRedirections;

    }


}
