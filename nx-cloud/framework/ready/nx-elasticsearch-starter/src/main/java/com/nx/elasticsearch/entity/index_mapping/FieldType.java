package com.nx.elasticsearch.entity.index_mapping;

/**
 * 作者：王坤造
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
public enum FieldType {
    Integer,
    Long,
    Double,
    /**
     * 精确检索
     */
    StringPreciseSearch,
    /**
     * 模糊检索
     */
    StringFuzzySearch,
    /**
     * 精确检索+模糊检索
     */
    StringPreciseSearchAndFuzzySearch,
    /**
     * 模糊检索(按字)
     */
    StringFuzzySearch2,
    /**
     * 精确检索+模糊检索(按字)
     */
    StringPreciseSearchAndFuzzySearch2,
    /**
     * 嵌套类型
     */
    Nested,
    /**
     * 不参与检索
     */
    StringNoSearch

}
