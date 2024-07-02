package com.nx.elasticsearch.entity.tree;

import com.github.sd4324530.jtuple.Tuple;
import com.github.sd4324530.jtuple.Tuples;
import com.nx.elasticsearch.constant.ESConstants;
import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者：王坤造
 * 时间：2019/2/26
 * 名称：
 * 备注：
 */
public class TreeCube implements Serializable {
    //操作方式(新增使用)
    private int operType;
    //子esid(新增使用)
    private String temp1;
    private String temp2;
    //唯一esid(查询使用)
    private String uniqueESID;
    //父esid(新增使用)
    private String parentESID;
    //子esid集合(删除使用)
    private List<String> childrenESIDs;
    //存储数据(新增,删除使用)
    private Map<String, Object> data;
    //父对象
    private TreeCube parentTreeCube;
    //子对象集合(删除使用)
    private List<TreeCube> childrenTreeCubes;
    //data中的esid
    private String esid;

    //暂时LeeHao使用
    private int depth;

    public TreeCube() {
    }

    private TreeCube(Map<String, Object> data, int operType) {
        esid = (String) data.get(ESConstants.ESID_FIELD);
        if (StringUtils.isEmpty(esid)) {
            EsUtils.throwRuntimeException("data中esid禁止为空!");
        }
        this.data = data;
        this.operType = operType;
    }

    /**
     * 根节点无子节点【新增】
     */
    public static TreeCube getAddRootNode(Map<String, Object> current) {
        return new TreeCube(current, 1);
    }

     public static TreeCube getInstance(Map<String, Object> current) {
        return new TreeCube(current, 0);
    }

    public static TreeCube getInstance(Map<String, Object> current, String uniqueESID) {
        TreeCube tc = new TreeCube(current, 0);
        tc.uniqueESID = uniqueESID;
        return tc;
    }

    public int getOperType() {
        return operType;
    }

    public String getUniqueESID() {
        return uniqueESID;
    }

    public String getParentESID() {
        return parentESID;
    }

    public String getTemp1() {
        return temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public List<String> getChildrenESIDs() {
        return childrenESIDs;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getESID() {
        return esid;
    }

    public TreeCube getParentTreeCube() {
        return parentTreeCube;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * 仅在返回查询数据使用,其它時候禁用
     *
     * @param parentTreeCube
     * @param parentESID
     * @return
     */
    @Deprecated
    public TreeCube setParentTreeCube(TreeCube parentTreeCube, String parentESID) {
        if (parentTreeCube == null || StringUtils.isEmpty(parentESID)) {
            EsUtils.throwRuntimeException("parentTreeCube和parentESID禁止为空!");
        }
        this.parentTreeCube = parentTreeCube;
        this.parentESID = parentESID;
        return this;
    }

    public List<TreeCube> getChildrenTreeCubes() {
        return childrenTreeCubes;
    }

    public TreeCube setChildrenTreeCubes(List<TreeCube> childrenTreeCubes) {
        if (CollectionUtils.isEmpty(childrenTreeCubes)) {
            EsUtils.throwRuntimeException("childrenTreeCubes禁止为空!");
        }
        this.childrenTreeCubes = childrenTreeCubes;
        this.childrenESIDs = childrenTreeCubes.stream().map(o -> (String) o.data.get(ESConstants.ESID_FIELD)).collect(Collectors.toList());
        return this;
    }

    /**
     * childrenESIDs必须和childrenTreeCubes中的esid顺序完全一致,否则禁止调用此方法
     *
     * @param childrenTreeCubes
     * @param childrenESIDs
     * @return
     */
    @Deprecated
    public TreeCube setChildrenTreeCubes(List<TreeCube> childrenTreeCubes, List<String> childrenESIDs) {
        if (CollectionUtils.isEmpty(childrenTreeCubes) || CollectionUtils.isEmpty(childrenESIDs)) {
            EsUtils.throwRuntimeException("childrenTreeCubes和childrenESIDs禁止为空!");
        }
        this.childrenTreeCubes = childrenTreeCubes;
        this.childrenESIDs = childrenESIDs;
        return this;
    }

    public String toJson() {
        if (MapUtils.isEmpty(data)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(String.format("{\"id\": \"%s\", \"topic\": \"%s\", \"expanded\": %s ,\"tag\": %s ,\"update\": \"%s\"", data.get(ESConstants.ESID_FIELD), data.get("topic"), data.get("expanded") != null ? data.get("expanded") : false, data.get("tag"), data.get("update")));
        String direction = (String) data.get("direction");
        if (StringUtils.isNotEmpty(direction)) {
            sb.append(String.format(", \"direction\": \"%s\"", direction));
        }
        String name = (String) data.get("name");
        if (StringUtils.isNotEmpty(name)) {
            sb.append(String.format(", \"name\": \"%s\"", name));
        }
        if (CollectionUtils.isNotEmpty(childrenTreeCubes)) {
            String[] jsons = childrenTreeCubes.stream().map(o -> o.toJson()).filter(StringUtils::isNotEmpty).toArray(String[]::new);
            if (ArrayUtils.isNotEmpty(jsons)) {
                sb.append(String.format(", \"children\": [%s]", String.join(", ", jsons)));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 根据esid获取它的父esid
     *
     * @param esid
     * @return
     */
    public String getParentESID(String esid) {
        if (Objects.equals(esid, this.esid)) {
            return parentESID;
        }
        if (CollectionUtils.isNotEmpty(childrenESIDs)) {
            if (childrenESIDs.contains(esid)) {
                return this.esid;
            }
            return getParentESID(esid, childrenTreeCubes);
        }
        return null;
    }

    /**
     * 根据esid从当前TreeCube或者子类获取TreeCube对象
     *
     * @param esid
     * @return
     */
    public TreeCube getCurrentOrChildren(String esid) {
        if (Objects.equals(esid, data.get(ESConstants.ESID_FIELD))) {
            return this;
        }
        if (CollectionUtils.isNotEmpty(childrenTreeCubes)) {
            Optional<TreeCube> one = childrenTreeCubes.stream().filter(o -> Objects.equals(esid, o.data.get(ESConstants.ESID_FIELD))).findAny();
            if (one.isPresent()) {
                return one.get();
            }
            return getCurrentOrChildren(esid, childrenTreeCubes);
        }
        return null;
    }

    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(childrenTreeCubes)) {
            return "TreeCube{" +
                    "parentESID='" + parentESID + '\'' +
                    ", data=" + data +
                    ", childrenESIDs=" + childrenESIDs +
                    ", childrenTreeCubes=" + childrenTreeCubes +
                    '}';
        }
        return "TreeCube{" +
                "parentESID='" + parentESID + '\'' +
                ", data=" + data +
                ", childrenESIDs=" + childrenESIDs +
                ", childrenTreeCubes=" + System.lineSeparator() + String.join(System.lineSeparator(), childrenTreeCubes.stream().map(o -> o.toString()).toArray(String[]::new)) +
                '}';
    }

    private String getParentESID(String esid, List<TreeCube> childrenTCs) {
        Stack<Tuple> stack = new Stack<>();
        stack.push(Tuples.tuple(childrenTCs, 0));
        Tuple tuple;
        int index;
        TreeCube tc;
        List<String> childrenESIDs;
        while (!stack.isEmpty()) {
            tuple = stack.pop();
            childrenTCs = tuple.get(0);
            index = tuple.get(1);
            tc = childrenTCs.get(index);
            childrenESIDs = tc.childrenESIDs;
            if (CollectionUtils.isEmpty(childrenESIDs)) {
                if (childrenTCs.size() - index > 1) {
                    stack.push(Tuples.tuple(childrenTCs, index + 1));
                }
            } else {
                if (childrenESIDs.contains(esid)) {
                    return tc.esid;
                }
                if (childrenTCs.size() - index > 1) {
                    stack.push(Tuples.tuple(childrenTCs, index + 1));
                }
                stack.push(Tuples.tuple(tc.childrenTreeCubes, 0));
            }
        }
        return null;
    }

    private TreeCube getCurrentOrChildren(String esid, List<TreeCube> childrenTCs) {
        Stack<Tuple> stack = new Stack<>();
        stack.push(Tuples.tuple(childrenTCs, 0));
        Tuple tuple;
        int index;
        List<TreeCube> childrenTreeCubes;
        Optional<TreeCube> one;
        while (!stack.isEmpty()) {
            tuple = stack.pop();
            childrenTCs = tuple.get(0);
            index = tuple.get(1);
            childrenTreeCubes = childrenTCs.get(index).childrenTreeCubes;
            if (CollectionUtils.isEmpty(childrenTreeCubes)) {
                if (childrenTCs.size() - index > 1) {
                    stack.push(Tuples.tuple(childrenTCs, index + 1));
                }
            } else {
                one = childrenTreeCubes.stream().filter(o -> Objects.equals(esid, o.data.get(ESConstants.ESID_FIELD))).findAny();
                if (one.isPresent()) {
                    return one.get();
                }
                if (childrenTCs.size() - index > 1) {
                    stack.push(Tuples.tuple(childrenTCs, index + 1));
                }
                stack.push(Tuples.tuple(childrenTreeCubes, 0));
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeCube treeCube = (TreeCube) o;
        return Objects.equals(esid, treeCube.esid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(esid);
    }
}
