package com.nx.cache.utils;

import java.io.Serializable;

/**
 *
 * Simple serializable class that serves as a {@code null} replacement
 * for cache stores which otherwise do not support {@code null} values.
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月15日
 */
public final class NullValue implements Serializable {

    public static final Object INSTANCE = new NullValue();

    private static final long serialVersionUID = 1L;

    private Object readResolve() {
        return INSTANCE;
    }
}
