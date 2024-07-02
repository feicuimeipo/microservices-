package com.nx.elasticsearch.entity.index_mapping;

import lombok.Data;

import java.io.Serializable;

/**
 * 作者：王坤造
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
@Data
public class Mapping implements Serializable {
    //字段名
    private String field;
    //字段类型
    private FieldType fieldType;
    //nested父级 非nested字段和nested第一层字段无父级
    private String parent;
    //nested当前层级
    private String nestedFloor;

    public Mapping(String field, FieldType fieldType, String parent, String nestedFloor) {
        this.field = field;
        this.fieldType = fieldType;
        this.parent = parent;
        this.nestedFloor = nestedFloor;
    }
}
