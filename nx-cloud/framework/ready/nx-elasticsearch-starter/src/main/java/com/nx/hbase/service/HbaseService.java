package com.nx.hbase.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HbaseService {

    /**
     * 新增
     *
     * @param index
     * @param map   必须要有esid
     * @return
     */
    void insert(String index, Map<String, Object> map);

    void delete(String index, String esid);

    void delete(String index, List<String> esids);

    void BulkInsert(String index, List<Map<String, Object>> list);

    void truncateTable(String index) throws IOException;

    void createTable(String index);

    List<Map<String, Object>> query(String index, String row);

    List<Map<String, Object>> query(String index, List<String> row);

    List<Map<String, Object>> query(String index, String row, String field);
}
