package com.nx.elasticsearch.api;

import org.apache.hadoop.hbase.client.Table;

public interface TableCallback<T> {
    T doInTable(Table var1) throws Throwable;
}
