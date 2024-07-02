package com.nx.elasticsearch.query;

import java.util.List;

/**
 * 名称：包含子分组聚合函数【默认:实现该接口必须包含子分组聚合函数】
 */
public interface AggSubAggBuilderService<T> {

    T addAggBuilders(AggBuilderService... aggBuilderServices);

    List<AggBuilderService> getAggBuilders();

    AggBuilderService[] getAggBuilders2();

    //子分组聚合函数必须存在
    interface SubMust {
        //不存在则要抛出的异常
        void judgeForcedSubAggBuilder();
    }

    //子分组聚合函数可以不存在
    interface SubShould {
    }
}
