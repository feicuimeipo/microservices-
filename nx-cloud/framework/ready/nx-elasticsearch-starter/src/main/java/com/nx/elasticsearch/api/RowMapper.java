package com.nx.elasticsearch.api;

import org.apache.hadoop.hbase.client.Result;

public interface RowMapper<T> {
    T mapRow(Result var1, int var2) throws Exception;
}
