package com.nx.elasticsearch.constant;

/**
 * 名称：王坤造
 * 时间：2017/6/29.
 * 名称：聚合函数
 * 备注：
 */
public enum AlgorithmType {
    //平均值
    AVG,
    //最大值【如果该字段不存在会返回Double.NEGATIVE_INFINITY】
    MAX,
    //最小值【如果该字段不存在会返回Double.POSITIVE_INFINITY】
    MIN,
    //和值
    SUM,
    //统计数量(比COUNT_DISTINCT效率高)【如果统计字段是数组类型或者要count(*),field必须设置成"_index",才能完美统计数量】
    COUNT,
    //统计去重的数量【通过Cardinality实现】【误差】
    COUNT_DISTINCT,
    /**
     * 底下3个禁止排序
     */
    //中值(中位数)【通过Percentiles实现】【误差】
    MEDIAN,
    //当A字段最大值时,此时B字段的值(A代表MAX,B代表Other,得到的是B)【如果B值为空,则会找有值的B,且A字段是相对最大的.通过Terms和Max实现】
    MAX_OTHER,
    //当A字段最小值时,此时B字段的值(A代表MIN,B代表Other,得到的是B)【如果B值为空,则会找有值的B,且A字段是相对最大的.通过Terms和Min实现】
    MIN_OTHER
}