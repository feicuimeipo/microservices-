package com.nx.elasticsearch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: 刘向
 * @date: 22/06/14 14:37
 * @comment: MaxCoumputeService接口
 * @return:
 * @notes:
 */
public interface MaxCoumputeService {

    /**
     *
     * @param table 表名
     * @param fields 字段名
     * @param where where条件
     * @param group 聚合字段
     * @param order 排序字段
     * @param limit 条目
     * @return
     */
    List<Map<String, Object>> Query(String table, ArrayList<String> fields, String where, List<String> group, Map<String,List<String>> order, String limit);

    /**
     *
     * @param tableName 表名
     * @return
     */
    List<String> Columns(String tableName);

    /**
     *
     * @param table 表名
     * @param set
     * @param where
     */
    Boolean Update(String table,String set,String where);

    /**
     *
     * @param table 表名
     * @param partition
     * @param value
     * @return
     */
    Boolean Insert(String table,String partition,String value);
}
