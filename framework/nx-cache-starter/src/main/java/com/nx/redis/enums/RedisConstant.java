package com.nx.redis.enums;

import com.nx.redis.serializer.SerializerTypeEnum;

/**
 * spring.redis:
 *   groups:
 *  redis:
 *
 */
public class RedisConstant {

    public static RedisMode DEFAULT_MODE = RedisMode.standalone;
    public static boolean DEFAULT_KEY_USE_StringSerializer = true;
    public static String DEFAULT_VALUE_USE_valueSerializerType = SerializerTypeEnum.Json.getCode();

    public static final String TenantModeEnabledName = "tenantEnabled";
    //public static final String DEFAULT_REDIS_GROUP_NAME = "spring";

    //public static final String SESSION_CACHE_NAME = "session";
    public static final String REDIS_PROVIDER_SUFFIX = "RedisProvider";



}
