package com.nx.elasticsearch.service;

import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.impl.AggAggregate;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


public interface ESDataService {

    /**
     * @Description:无脑导出所有数据
     * @Param: index 索引名
     * @Param: queries 查询条件
     * @Param: size 批次数
     * @Param: func 处理函数
     * @Param: showFields 字段
     * @Return: void
     **/
    void listAndAction(String index, List<Query> queries, int size,
                       Consumer<List<Map<String, Object>>> func,
                       String... showFields);

    /**
     * @Description: 单字段分组
     * @Param: index
     * @Param: queries
     * @Param: field
     * @Param: asc
     * @Param: batchSize
     * @Param: func
     * @Return: void
     **/
    void groupOneField(String index, List<Query> queries, String field, boolean asc, int batchSize,
                       Consumer<List<Object>> func);

    /**
     * @Description: 分组并聚合
     * @Param: index
     * @Param: queries
     * @Param: size
     * @Param: fields
     * @Param: func
     * @Param: aggBuilders
     * @Return: void
     **/
    void groupAndAgg(String index, List<Query> queries,
                     String[] fields, int batchSize, Consumer<List<Map<String, Object>>> func,
                     AggAggregate... aggBuilders);

    /**
     * 检索并批量更新
     *
     * @param index  索引名
     * @param qry    搜索条件
     * @param func   数据处理函数
     * @param fields 检索字段,可为空
     */
    void listAndUpdate(String index, List<Query> qry, String field,
                       Function<Map<String, Object>, Map<String, Object>> func,
                       int batchGetCount, int batchUpdateCount,
                       String... fields);

    /**
     * @Description:无脑导出所有数据
     * @Param: index 索引名
     * @Param: queries 查询条件
     * @Param: size 批次数
     * @Param: showFields 字段
     * @Return: void
     **/
    List<Map<String, Object>> batchGetAllData(String index, List<Query> queries, int size,
                                              String... showFields);

}
