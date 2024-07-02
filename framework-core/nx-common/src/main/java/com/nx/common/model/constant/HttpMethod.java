package com.nx.common.model.constant;

import java.util.HashMap;
import java.util.Map;



/**
 * 请求方式
 *
 * @author ruoyi
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> MAPPING = new HashMap(16);

    static {
        MAPPING.values().stream().forEach(item->{
            MAPPING.put(item.name(),item);
        });
    }

    public static HttpMethod resolve(String method) {
        return (method != null ? MAPPING.get(method) : null);
    }

    public boolean matches(String method) {
        return (this == resolve(method));
    }

}
