package com.nx.elasticsearch.query;

/**
 * 作者：王坤造
 * 时间：2018/1/31
 * 名称：
 * 备注：
 */
public interface AggOrderService<T> {


    T setOrder(Boolean order);


    Boolean getOrder();
}
