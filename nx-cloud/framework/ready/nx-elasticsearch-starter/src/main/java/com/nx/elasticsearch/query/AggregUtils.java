package com.nx.elasticsearch.query;

import com.nx.elasticsearch.query.impl.AggGroup;
import com.nx.elasticsearch.constant.AlgorithmType;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.query.impl.AggNested;
import com.nx.elasticsearch.query.impl.AggReverseNested;


/**
 * 查询的工具类
 */
public class AggregUtils {
    /**
     * 分组数量统计(非子集)
     */
    public static AggGroup groupForCount(String fieldGroup, String fieldCount) {
        AggGroup aggGroup = AggGroup.getInstance(fieldGroup);
        //aggGroup.setOrder(true);
        aggGroup.setUseMaxSize(true);
        aggGroup.addAggBuilders(AggAggregate.getInstance(fieldCount, fieldCount, AlgorithmType.COUNT));
        return aggGroup;
    }

    public static AggGroup groupForCountDistinct(String fieldGroup, String fieldCount) {
        AggGroup aggGroup = AggGroup.getInstance(fieldGroup);
        //aggGroup.setOrder(true);
        aggGroup.setUseMaxSize(true);
        aggGroup.addAggBuilders(AggAggregate.getInstance(fieldCount, fieldCount, AlgorithmType.COUNT_DISTINCT));
        return aggGroup;
    }

    public static AggGroup groupForCount(String fieldGroup, String fieldCount, boolean asc) {
        AggGroup aggGroup = AggGroup.getInstance(fieldGroup);
        //aggGroup.setOrder(true);
        aggGroup.addAggBuilders(AggAggregate.getInstance(fieldCount, fieldCount, AlgorithmType.COUNT));
        aggGroup.setOrder(AggGroup.Order.orderAggregation(asc, fieldCount));
        return aggGroup;
    }

    public static AggGroup groupForMax(String fieldGroup, String fieldCount) {
        AggGroup aggGroup = AggGroup.getInstance(fieldGroup);
        //aggGroup.setOrder(true);
        aggGroup.addAggBuilders(AggAggregate.getInstance(fieldCount, fieldCount, AlgorithmType.MAX));
        return aggGroup;
    }

    /**
     * 分组(包括子集)数量统计
     */
    public static AggNested groupNestedForCount(String parent, String... fieldGroups) {
        AggNested aggNested = AggNested.getInstance(parent, parent);
        for (String fieldGroup : fieldGroups) {
            AggGroup aggGroup = AggGroup.getInstance(fieldGroup);
            AggReverseNested reverseNested = AggReverseNested.getInstance(null, fieldGroup);
            reverseNested.addAggBuilders(AggAggregate.getInstance("_id", "main_count", AlgorithmType.COUNT));
            aggGroup.addAggBuilders(reverseNested);
            aggNested.addAggBuilders(aggGroup);
        }
        return aggNested;
    }

    /**
     * 聚合排除统计
     */
    public static AggAggregate aggForDCount(String field) {
        return AggAggregate.getInstance(field, field, AlgorithmType.COUNT_DISTINCT);
    }

    public static AggAggregate aggForCount(String field) {
        return AggAggregate.getInstance(field, field, AlgorithmType.COUNT);
    }

    public static AggGroup group(String field) {
        return AggGroup.getInstance(field);
    }

    public static AggAggregate indexCount() {
        AggAggregate aggAggregate = AggAggregate.getInstance("_index", AlgorithmType.COUNT);
        return aggAggregate;
    }

    public static AggGroup list(AggGroup... aggGroups) {
        return AggGroup.getInstance(aggGroups);
    }
}
