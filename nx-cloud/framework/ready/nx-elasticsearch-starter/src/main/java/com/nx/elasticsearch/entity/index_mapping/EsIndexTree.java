package com.nx.elasticsearch.entity.index_mapping;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：刘向
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
@Data
public class EsIndexTree implements Serializable {

    private static final long serialVersionUID = 1L;

    //层级名称
    private String indexName;

    //字段所属父索引
    private String parentIndexName;

    //字段类型
    private FieldType type;

    //源数据项
    private List items = new ArrayList<>();

    //字段路径
    private String path;
}
