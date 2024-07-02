package com.nx.elasticsearch.api;

import org.apache.hadoop.hbase.client.BufferedMutator;

public interface MutatorCallback {
    void doInMutator(BufferedMutator var1) throws Throwable;
}