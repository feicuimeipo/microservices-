package com.nx.elasticsearch.entity.index_mapping;

import lombok.Data;

import java.io.Serializable;

/**
 * 作者：刘向
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
@Data
public class EsIndexField implements Serializable {

    private static final long serialVersionUID = 1L;

    //字段名称
    private String field;

    //字段路径
    private String path;

    //字段值类型
    private FieldType fieldType;
}
