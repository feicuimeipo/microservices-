package com.nx.cache.exception;

import com.nx.common.exception.BaseException;

/**
 * 方法加载缓存值的包装异常
 */
public class LoaderCacheValueException extends BaseException {
    private static final long serialVersionUID = 1L;

    private final Object key;

    public LoaderCacheValueException(String key, Throwable ex) {
        super(String.format("加载key为 %s 的缓存数据,执行被缓存方法异常", key), ex);
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }



}