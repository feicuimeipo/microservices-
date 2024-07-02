package com.nx.cache.exception;

import com.nx.common.exception.BaseException;

/**
 * 方法加载缓存值的包装异常
 */
public class LoaderCacheException extends BaseException {
    private static final long serialVersionUID = 1L;


    public LoaderCacheException(String message) {
        super(message);
        this.code = 500;
    }



}