package com.nx.extra.number;


import org.apache.commons.lang3.StringUtils;

/**
 * 数字的工具类，补全 {@link org.apache.commons.lang3.StringUtils} 的功能
 *
 * @author 芋道源码
 */
public class NumberUtils {

    public static Long parseLong(String str) {
        return StringUtils.isNotEmpty(str) ? Long.valueOf(str) : null;
    }

}
