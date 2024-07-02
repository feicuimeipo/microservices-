package com.nx.elasticsearch.entity;

import java.io.Serializable;

/**
 * Created by yymofang on 18/2/6.
 */
public class Attribute implements Serializable {
    public Attribute(Integer modularity_class) {
        this.modularity_class = modularity_class;
    }

    public Integer getModularity_class() {
        return modularity_class;
    }

    public void setModularity_class(Integer modularity_class) {
        this.modularity_class = modularity_class;
    }

    private Integer modularity_class;
}
