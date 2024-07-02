package com.nx.common.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 树结构对象，用于将列表数据转换成树结构。
 */
@Data
public class TreeVO<T> implements Serializable {
    private Long id;
    private String label;
    private Long pid;
    private List<T> children;

    /**
     * 生成一棵树
     * @param nodeList
     * @param rootId
     * @return
     */
    public static List<TreeVO> buildTree(List<TreeVO> nodeList, Long rootId){
        TreeBuilder treeBuilder = new TreeBuilder(nodeList,rootId==null?0:rootId);
        return treeBuilder.buildTree();
    }


    public static class TreeBuilder {
        private List<TreeVO> nodeList;
        private Long rootId;

        public TreeBuilder(List<TreeVO> nodeList, Long rootId){
            this.nodeList = nodeList;
            this.rootId = rootId;
        }

        public List<TreeVO> getRootNode(){
            List<TreeVO> rootNodeList = new ArrayList<>();
            for (TreeVO nodeVO:nodeList){
                if (nodeVO.getPid().longValue()==rootId.longValue()){
                    rootNodeList.add(nodeVO);
                }
            }
            return rootNodeList;
        }

        public List<TreeVO> buildTree(){
            List<TreeVO> treeNodes = new ArrayList<>();
            for(TreeVO treeRootNode:getRootNode()){
                treeRootNode = getChildTree(treeRootNode);
                treeNodes.add(treeRootNode);
            }
            return treeNodes;
        }

        public TreeVO getChildTree(TreeVO parentNode){
            List<TreeVO> childTree = new ArrayList<>();
            for(TreeVO treeNodeVO:nodeList){
                if (treeNodeVO.getPid().equals(parentNode.getId())){
                    childTree.add(treeNodeVO);
                }
            }
            parentNode.setChildren(childTree);
            return parentNode;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}