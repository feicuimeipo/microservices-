package com.nx.elasticsearch.query;

import org.apache.commons.lang3.StringUtils;

/**
 * 作者：王坤造
 * 时间：2018/9/12
 * 名称：
 * 备注：
 */
public class QueryHelper {

    public static void getIllegalArgumentException(Object... objs) {
        throw new IllegalArgumentException(String.format("参数类型错误:%s", StringUtils.join(objs, ',')));
    }
}
