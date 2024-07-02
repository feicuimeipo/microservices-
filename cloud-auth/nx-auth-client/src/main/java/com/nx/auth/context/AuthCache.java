package com.nx.auth.context;


import com.nx.cache.CacheManager;
import com.nx.cache.ICache;

import java.util.concurrent.TimeUnit;

public class AuthCache {
    public final static String AUTH_CACHE_NAME = "nx-auth";

    public final static String CACHE_PREFIX = "nx-auth:";

    public static ICache getCache() {
        return CacheManager.getInstance().getCache(AUTH_CACHE_NAME);
    }

    public static <T> T get(String key,Class<T> tClass) {
        if (!key.toLowerCase().startsWith(CACHE_PREFIX)) {
            key = CACHE_PREFIX + ":" + key;
        }
        return getCache().get(key,tClass);
    }

    public static void set(String key, Object value, long expire, TimeUnit timeUnit) {
         getCache().set(key, value, expire, timeUnit);
    }

}
