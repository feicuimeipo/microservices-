package com.nx.gateway.route.core;



import com.alibaba.nacos.api.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Config data type.
 *
 * @author liaochuntao
 **/
public enum DynamicRouteConfigType {

    /**
     * config type is "json".
     */
    JSON("json"),

    /**
     * config type is "yaml".
     */
    YAML("yaml");

    private final String type;

    private static final Map<String, DynamicRouteConfigType> LOCAL_MAP = new HashMap<String, DynamicRouteConfigType>();

    static {
        for (DynamicRouteConfigType configType : values()) {
            LOCAL_MAP.put(configType.getType(), configType);
        }
    }

    DynamicRouteConfigType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static DynamicRouteConfigType getDefaultType() {
        return DynamicRouteConfigType.YAML;
    }

    /**
     * check input type is valid.
     *
     * @param type config type
     * @return it the type valid
     */
    public static Boolean isValidType(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        return null != LOCAL_MAP.get(type);
    }
}
