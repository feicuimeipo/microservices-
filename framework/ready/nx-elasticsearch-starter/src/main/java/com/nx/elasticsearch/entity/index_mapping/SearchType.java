package com.nx.elasticsearch.entity.index_mapping;

/**
 * 作者：王坤造
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
public enum SearchType {
    /**
     * 精确检索
     */
    PreciseSearch,
    /**
     * 模糊检索
     */
    FuzzySearch,
    /**
     * 精确检索+模糊检索
     */
    PreciseSearchAndFuzzySearch
}
