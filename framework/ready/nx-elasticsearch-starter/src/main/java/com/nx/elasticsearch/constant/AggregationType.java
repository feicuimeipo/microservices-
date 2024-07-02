package com.nx.elasticsearch.constant;

/**
 * 作者：王坤造
 * 时间：2018/1/28
 * 名称：分组聚合方式
 * 备注：
 */
public enum AggregationType {
    //复合中的分组
    CHI_GROUP,
    //复合
    COMPOSITE,
    //单字段分组
    SINGLE_GROUP,
    //多字段分组
    MULTIPLE_GROUP,
    //单字段分组聚合
    SINGLE_GROUP_AGGREGATE,
    //多字段分组聚合
    MULTIPLE_GROUP_AGGREGATE,
    //聚合
    AGGREGATE,
    //子查询
    NESTED,
    //反向子查询
    REVERSE_NESTED,
    //过滤
    FILTER,
    //時间分组聚合
    DATE
}
