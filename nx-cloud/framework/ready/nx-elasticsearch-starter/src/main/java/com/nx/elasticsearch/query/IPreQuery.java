package com.nx.elasticsearch.query;


import java.util.List;
import java.util.Map;

/**
 * 前置条件接口
 */
public interface IPreQuery {
    List<Query> action(List<Query> queries, Map<String, String[]> parameterMap);
}
