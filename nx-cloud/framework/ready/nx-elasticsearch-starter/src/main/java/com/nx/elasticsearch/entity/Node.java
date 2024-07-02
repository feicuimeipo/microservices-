package com.nx.elasticsearch.entity;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by yymofang on 17/12/19.
 */
public class Node implements Serializable{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    private String name;

    public Integer getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(Integer symbolSize) {
        this.symbolSize = symbolSize;
    }

    public Attribute getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute attributes) {
        this.attributes = attributes;
    }

    private Attribute attributes;
    private Integer symbolSize;

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    private String short_name;
    private int category;

    private int x;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private int y;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    private String full_name;

    private static int[] Symbols={60,70,80,90,100,110};
    private static int[] small_Symbols={2,3,4,5,6,8};

    private static int[] modularity_class={1,2,3,4,5};
    public Node(String name, String short_name, String full_name, boolean bigSize ) {
        this.name = name;
        this.short_name = short_name;
        this.category = 0;
        this.full_name=full_name;
        this.setAttributes(new Attribute(modularity_class[RandomUtils.nextInt(0,modularity_class.length)]));
        if(bigSize) {
            this.setSymbolSize(Symbols[RandomUtils.nextInt(0, Symbols.length)]);
        }else {
            this.setSymbolSize(small_Symbols[RandomUtils.nextInt(0, small_Symbols.length)]);
        }
    }

    public Node(String name, String short_name, String full_name, int modularity_class, int size) {
        this.name = name;
        this.short_name = short_name;
        this.category = 0;
        this.full_name = full_name;
        this.setAttributes(new Attribute(modularity_class));
        this.setSymbolSize(size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return new EqualsBuilder()
                .append(category, node.category)
                .append(name, node.name)
                .append(short_name, node.short_name)
                .append(full_name, node.full_name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(short_name)
                .append(category)
                .append(full_name)
                .toHashCode();
    }
}
