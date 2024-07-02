package com.nx.elasticsearch.utils;


import java.util.HashMap;
import java.util.Map;

class EsMapBuilder<K, V> {
    Map<K, V> hm = new HashMap();

    EsMapBuilder() {
    }

    EsMapBuilder<K, V> append(K k, V v) {
        this.hm.put(k, v);
        return this;
    }

    Map<K, V> builder() {
        return this.hm;
    }
}
