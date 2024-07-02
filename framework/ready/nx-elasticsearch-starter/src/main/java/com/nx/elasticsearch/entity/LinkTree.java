package com.nx.elasticsearch.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LinkTree implements Serializable {
    private String id;
    private String topic;
    private List<LinkTree> children;
    private boolean expanded;

    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<LinkTree> getChildren() {
        return children;
    }

    public void setChildren(List<LinkTree> children) {
        this.children = children;
    }

    public LinkTree(String id, String topic, boolean expanded) {
        this.id = id;
        this.topic = topic;
        this.expanded = expanded;
        this.children=new ArrayList<>();
        this.level=1;
    }


    public boolean addTree(LinkTree tree){
         tree.setLevel(this.getLevel()+1);
         return children.add(tree);
    }
}