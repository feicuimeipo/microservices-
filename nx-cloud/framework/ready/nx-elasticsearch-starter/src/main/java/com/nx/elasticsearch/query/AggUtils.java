package com.nx.elasticsearch.query;


import com.nx.elasticsearch.constant.AlgorithmType;
import com.nx.elasticsearch.query.impl.Aggregate;
import com.nx.elasticsearch.utils.EsSortType;

/**
 * 查询的工具类
 */
@Deprecated
public class AggUtils {


    public static Aggregate Agg(String fields) {
        return new Aggregate(EsSortType.DESC, fields, fields);
    }

    public static Aggregate Agg(String... fields) {
        return new Aggregate(EsSortType.DESC, "分组", fields);
    }

    public static Aggregate AggDCount(String field) {
        return new Aggregate(field, field, AlgorithmType.COUNT_DISTINCT, null, null, null);
    }

    public static Aggregate AggDCount(String field, String alias) {
        return new Aggregate(field, alias, AlgorithmType.COUNT_DISTINCT, null, null, null);
    }

    public static Aggregate AggCount(String field) {
        return new Aggregate(field, field, AlgorithmType.COUNT, null, null, null);
    }

    public static Aggregate AggCount(String field, String alias) {
        return new Aggregate(field, alias, AlgorithmType.COUNT, EsSortType.DESC, null, null);
    }


    public static Aggregate AggSum(String field) {
        return new Aggregate(field, field, AlgorithmType.SUM, null, null, null);
    }

    public static Aggregate AggMax(String field) {
        return new Aggregate(field, field, AlgorithmType.MAX, null, null, null);
    }

    public static Aggregate AggMin(String field) {
        return new Aggregate(field, field, AlgorithmType.MIN, null, null, null);
    }

    public static Aggregate groupBy(String fieldGroup, String fieldCount) {
        Aggregate agg = Agg(fieldGroup);
        agg.addChiAggregate(AggCount(fieldCount, fieldCount));
        return agg;
    }

    public static Aggregate groupAll(String fieldGroup, String alias) {
        Aggregate agg = Agg(fieldGroup);
        agg.addChiAggregate(AggCount(fieldGroup, alias));
        return agg;
    }

}
