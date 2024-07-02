package com.nx.elasticsearch.utils;

import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.QueryType;
import com.nx.elasticsearch.service.ESService;
import com.nx.elasticsearch.entity.tree.TreeCube;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


public class EsTreeCubeUtils {

    public static Set<String> getAllIdSet(TreeCube treeCube) {
        Set<String> result = new HashSet<>();
        if (treeCube != null) {
            result.add(treeCube.getESID());
            List<TreeCube> childrenList = treeCube.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                childrenList.forEach(i -> result.addAll(getAllIdSet(i)));
            }
        }
        return result;
    }

    public static Set<String> getAllIdSet(TreeCube treeCube, String nodeId) {
        Set<String> result = new HashSet<>();
        if (treeCube != null) {
            TreeCube branch = getBranch(treeCube, nodeId);
            if (branch != null) {
                result = getAllIdSet(branch);
            }
        }
        return result;
    }

    /**
     * @Description: 根据需要的节点id获取森林中该节点id以及子孙id集合，如果nodeIdSet为空，则返回所有id集合
     * @Param:
     * @return:
     * @Author: Li Hao
     * @Date: 2019/12/11
     */
    public static Set<String> getChildrenAndSelfIdSetInForest(List<TreeCube> forest, Set<String> nodeIdSet) {
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isNotEmpty(forest)) {
            Set<String> allNodeIdSet = CollectionUtils.isNotEmpty(nodeIdSet) ? new HashSet<>(nodeIdSet) : new HashSet<>();
            forest.forEach(treeCube -> {
                if (CollectionUtils.isNotEmpty(nodeIdSet)) {
                    for (String nodeId : allNodeIdSet) {
                        if (!nodeIdSet.contains(nodeId)) {
                            continue;
                        }
                        TreeCube tc = EsTreeCubeUtils.findNodeByEsid(treeCube, nodeId);
                        if (tc != null) {
                            result.addAll(EsTreeCubeUtils.getAllIdSet(tc));
                            nodeIdSet.removeAll(result);
                        }
                    }
                } else {
                    result.addAll(getAllIdSet(treeCube));
                }
            });
        }
        return result;
    }

    public static TreeCube findNodeByEsid(TreeCube tc, String esid) {
        if (tc != null) {
            if (tc.getESID().equalsIgnoreCase(esid)) {
                return tc;
            } else {
                List<TreeCube> childrenList = tc.getChildrenTreeCubes();
                if (CollectionUtils.isNotEmpty(childrenList)) {
                    for (TreeCube treeCube : childrenList) {
                        tc = findNodeByEsid(treeCube, esid);
                        if (tc != null) {
                            return tc;
                        }
                    }
                }
            }
        }
        return null;
    }


    public static String getValue(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    public static TreeCube findNodeByField(TreeCube tc, String field, String value) {
        if (tc != null) {
            Map<String, Object> data = tc.getData();
            if (MapUtils.isNotEmpty(data) && StringUtils.equals(value, getValue(data.get(field)))) {
                return tc;
            }
            List<TreeCube> childrenList = tc.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                for (TreeCube treeCube : childrenList) {
                    tc = findNodeByField(treeCube, field, value);
                    if (tc != null) {
                        return tc;
                    }
                }
            }
        }
        return null;
    }

    public static List<TreeCube> getForestByField(List<TreeCube> forest, String field, String value) {
        List<TreeCube> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(forest)) {
            forest.forEach(tree -> {
                Map<String, Object> data = tree.getData();
                if (MapUtils.isNotEmpty(data) && StringUtils.equals(value, getValue(data.get(field)))) {
                    result.add(tree);
                }
                List<TreeCube> childrenList = tree.getChildrenTreeCubes();
                if (CollectionUtils.isNotEmpty(childrenList)) {
                    result.addAll(getForestByField(childrenList, field, value));
                }
            });
        }
        return result;
    }

    public static TreeCube findCopyNodeBySourceNode(TreeCube targetTree, TreeCube sourceNode, String field) {
        if (sourceNode != null) {
            String originTopic = getValue(sourceNode.getData().get(field));
            if (StringUtils.isNotBlank(originTopic)) {
                return EsTreeCubeUtils.findNodeByField(targetTree, field, originTopic);
            }
        }
        return null;
    }

    /**
     * @Description: 将树转换成节点链列表
     * @Param: prefix当前节点前缀，起点为null    pathField组成路径对应字段     splitter分隔符
     * @return:
     * @Author: Li Hao
     * @Date: 2020/3/26
     */
    public static List<Map<String, Object>> treeToList(TreeCube tc, String prefix, String pathField, String splitter) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (tc != null) {
            Map<String, Object> map = tc.getData();
            if (MapUtils.isNotEmpty(map)) {
                String value = getValue(map.get(pathField));
                value = StringUtils.isEmpty(prefix) ? value : prefix + splitter + value;
                map.put(pathField, value);
                map.put(ESService.ESID_FIELD, tc.getESID());
                result.add(map);
                prefix = value;
            }
            List<TreeCube> childrenList = tc.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                for (TreeCube treeCube : childrenList) {
                    result.addAll(treeToList(treeCube, prefix, pathField, splitter));
                }
            }
        }
        return result;
    }

    /**
     * @Description: 将树转换成esid->tree对应的map
     * @Param:
     * @return:
     * @Author: Li Hao
     * @Date: 2020/3/26
     */
    public static Map<String, TreeCube> treeToMap(TreeCube treeCube) {
        Map<String, TreeCube> result = new HashMap<>();
        if (treeCube != null) {
            result.put(treeCube.getESID(), treeCube);
            List<TreeCube> childrenList = treeCube.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                for (TreeCube tc : childrenList) {
                    result.putAll(treeToMap(tc));
                }
            }
        }
        return result;
    }

    public static String getUniqueEsidByNodeEsid(String index, String esid, ESService esService) {
        String uniqueEsid = null;
        try {
            Map<String, Object> relation = esService.getOne(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, esid, ESService.FIELD_UNIQUE_ESID);
            if (MapUtils.isNotEmpty(relation)) {
                uniqueEsid = getValue(relation.get(ESService.FIELD_UNIQUE_ESID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueEsid;
    }

    public static Set<String> getUniqueEsidByNodeEsid(String index, ESService esService, String... esids) {
        Set<String> uniqueEsidSet = null;
        try {
            List<Map<String, Object>> relations = esService.getList(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, new ArrayList<Query>() {{
                add(new Query(QueryType.IN, ESService.ESID_FIELD, esids));
            }}, null, null, -1, ESService.FIELD_UNIQUE_ESID);
            if (CollectionUtils.isNotEmpty(relations)) {
                uniqueEsidSet = new HashSet<>();
                for (Map<String, Object> relation : relations) {
                    uniqueEsidSet.add(getValue(relation.get(ESService.FIELD_UNIQUE_ESID)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueEsidSet;
    }

    public static List<Map<String, Object>> getTreeRelationshipByNodeEsid(String index, ESService esService, String... esids) {
        List<Map<String, Object>> relations = null;
        try {
            relations = esService.getList(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, new ArrayList<Query>() {{
                add(new Query(QueryType.IN, ESService.ESID_FIELD, esids));
            }}, null, null, -1, ESService.FIELD_UNIQUE_ESID, ESService.ESID_FIELD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relations;
    }

    public static TreeCube getTreeByNodeEsid(String index, String esid, ESService esService, String... fields) {
        String uniqueEsid = getUniqueEsidByNodeEsid(index, esid, esService);
        if (StringUtils.isNotEmpty(uniqueEsid)) {
            return esService.treeGetAll(index, uniqueEsid, fields);
        }
        return null;
    }

    public static String getRootEsidByNodeEsid(String index, String esid, ESService esService) {
        String uniqueEsid = getUniqueEsidByNodeEsid(index, esid, esService);
        if (StringUtils.isNotEmpty(uniqueEsid)) {
            Map<String, Object> relation = esService.getOne(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, new ArrayList<Query>() {{
                add(new Query(QueryType.EQ, ESService.FIELD_UNIQUE_ESID, uniqueEsid));
                add(new Query(QueryType.EQ, "parent_esid", null));
            }}, ESService.ESID_FIELD);
            if (MapUtils.isNotEmpty(relation)) {
                return getValue(relation.get(ESService.ESID_FIELD));
            }
        }
        return null;
    }

    public static Map<String, String> buildNodeRootEsidRelationMap(String index, ESService esService, String... esids) {
        Map<String, String> result = new HashMap<>();
        if (ArrayUtils.isNotEmpty(esids)) {
            List<Map<String, Object>> relationships = getTreeRelationshipByNodeEsid(index, esService, esids);
            if (CollectionUtils.isNotEmpty(relationships)) {
                Set<Object> uniqueEsidSet = getFieldSetInMapList(relationships, ESService.FIELD_UNIQUE_ESID, null);
                if (CollectionUtils.isNotEmpty(uniqueEsidSet)) {
                    List<Map<String, Object>> relations = esService.getList(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, new ArrayList<Query>() {{
                        add(new Query(QueryType.IN, ESService.FIELD_UNIQUE_ESID, uniqueEsidSet.toArray(new String[]{})));
                        add(new Query(QueryType.EQ, ESService.FIELD_PARENT_ESID, null));
                    }}, null, null, -1, ESService.ESID_FIELD, ESService.FIELD_UNIQUE_ESID);
                    if (CollectionUtils.isNotEmpty(relations)) {
                        Map<String, String> relationMap = relations.stream().collect(Collectors.toMap(i -> getValue(i.get(ESService.FIELD_UNIQUE_ESID)), i -> getValue(i.get(ESService.ESID_FIELD))));
                        relationships.forEach(relation -> {
                            String esid = getValue(relation.get(ESService.ESID_FIELD));
                            String uniqueEsid = getValue(relation.get(ESService.FIELD_UNIQUE_ESID));
                            if (StringUtils.isNotBlank(uniqueEsid) && relationMap.containsKey(uniqueEsid)) {
                                result.put(esid, relationMap.get(uniqueEsid));
                            }
                        });
                    }
                }
            }
        }
        return result;
    }

    public static Map<String, String> buildNodeRootEsidRelationMap(List<TreeCube> forest) {
        Map<String, String> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(forest)) {
            forest.forEach(tree -> {
                Set<String> idSet = getAllIdSet(tree);
                if (CollectionUtils.isNotEmpty(idSet)) {
                    idSet.forEach(id -> result.put(id, tree.getESID()));
                }
            });
        }
        return result;
    }

    public static Set<String> getRemovedNodeIdSet(TreeCube before, TreeCube after) {
        Set<String> beforeSet = getAllIdSet(before);
        Set<String> afterSet = getAllIdSet(after);
        beforeSet.removeAll(afterSet);
        return beforeSet;
    }

    /**
     * @Description:
     * @Param:
     * @return:
     * @Author: Li Hao
     * @Date: 2019/12/11
     */
    public static List<String> getBranchEsidList(String index, TreeCube treeCube, String bottomNodeEsid, ESService esService, boolean fromRoot) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(bottomNodeEsid) || treeCube != null) {
            treeCube = treeCube != null ? treeCube : getTreeByNodeEsid(index, bottomNodeEsid, esService, ESService.ESID_FIELD);
            if (treeCube != null) {
                Map<String, TreeCube> nodeMap = treeToMap(treeCube);
                TreeCube bottomCube = nodeMap.get(bottomNodeEsid);
                if (bottomCube != null) {
                    String parentEsid;
                    while (true) {
                        result.add(bottomCube.getESID());
                        parentEsid = bottomCube.getParentESID();
                        if (StringUtils.isNotEmpty(parentEsid)) {
                            bottomCube = nodeMap.get(parentEsid);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (fromRoot) {
            Collections.reverse(result);
        }
        return result;
    }

    public static boolean checkDuplicate(TreeCube treeCube, String field) {
        Set<String> valueSet = new HashSet<>();
        if (treeCube != null) {
            List<TreeCube> childrenList = treeCube.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                String value;
                for (TreeCube cube : childrenList) {
                    value = getValue(cube.getData().get(field));
                    if (valueSet.contains(value)) {
                        return true;
                    } else {
                        valueSet.add(value);
                    }
                }
                for (TreeCube cube : childrenList) {
                    if (checkDuplicate(cube, field)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @Description: 向下获取分支
     * @Param:
     * @Author: Li Hao
     * @Date: 2019/12/11
     */
    public static TreeCube getBranch(TreeCube treeCube, String nodeEsid) {
        if (treeCube != null) {
            if (treeCube.getESID().equalsIgnoreCase(nodeEsid)) {
                return treeCube;
            }
            List<TreeCube> childrenList = treeCube.getChildrenTreeCubes();
            if (CollectionUtils.isNotEmpty(childrenList)) {
                for (TreeCube cube : childrenList) {
                    treeCube = getBranch(cube, nodeEsid);
                    if (treeCube != null) {
                        return treeCube;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @Description: 向上获取分支
     * @Param:
     * @Author: Li Hao
     * @Date: 2019/12/11
     */
    public static List<TreeCube> getReverseBranch(TreeCube treeCube, String nodeEsid) {
        List<TreeCube> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(nodeEsid) && treeCube != null) {
            Map<String, TreeCube> nodeMap = treeToMap(treeCube);
            TreeCube bottomCube = nodeMap.get(nodeEsid);
            if (bottomCube != null) {
                String parentEsid;
                while (true) {
                    result.add(bottomCube);
                    parentEsid = bottomCube.getParentESID();
                    if (StringUtils.isNotEmpty(parentEsid)) {
                        bottomCube = nodeMap.get(parentEsid);
                    } else {
                        break;
                    }
                }
            }
        }
        Collections.reverse(result);
        return result;
    }

    /**
     * @Description: 获取所有分支
     * @Param: treeCube
     * @Return: List<List < TreeCube>>
     **/
    public static List<List<TreeCube>> getBranches(TreeCube treeCube) {
        if (null == treeCube) {
            return null;
        }
        List<List<TreeCube>> treeCubeList = new ArrayList<>();
        List<TreeCube> children = treeCube.getChildrenTreeCubes();
        if (CollectionUtils.isEmpty(children)) {
            List<TreeCube> list = new ArrayList<>();
            list.add(treeCube);
            treeCubeList.add(list);
            return treeCubeList;
        }
        for (TreeCube child : children) {
            List<List<TreeCube>> subBranches = getBranches(child);
            if (CollectionUtils.isNotEmpty(subBranches)) {
                for (List<TreeCube> subBranch : subBranches) {
                    subBranch.add(0, treeCube);
                }
            }
            treeCubeList.addAll(subBranches);
        }
        return treeCubeList;
    }

    /**
     * @Description: 获取所有叶子节点
     * @Param: treeCube
     * @Return: List<TreeCube>
     **/
    public static List<TreeCube> getLeaves(TreeCube treeCube) {
        List<TreeCube> result = new ArrayList<>();
        Map<String, TreeCube> nodeMap = treeToMap(treeCube);
        if (MapUtils.isNotEmpty(nodeMap)) {
            nodeMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(v.getChildrenTreeCubes())) {
                    result.add(v);
                }
            });
        }
        return result;
    }

    public static boolean isRoot(String index, String esid, ESService esService) {
        boolean result = false;
        Map<String, Object> relation = esService.getOne(index + ESService.INDEX_TREE_RELATIONSHIP_SUFFIX, esid, "parent_esid");
        if (MapUtils.isNotEmpty(relation)) {
            result = StringUtils.isBlank(relation.get("parent_esid").toString());
        }
        return result;
    }

    public static boolean isRoot(TreeCube tree, String rootEsid) {
        return tree != null && StringUtils.equals(tree.getESID(), rootEsid);
    }

    public static TreeCube computeDepth(TreeCube tree) {
        return computeDepth(tree, 1);
    }

    private static TreeCube computeDepth(TreeCube tree, Integer currentDepth) {
        if (tree != null) {
            tree.setDepth(currentDepth);
            if (CollectionUtils.isNotEmpty(tree.getChildrenTreeCubes())) {
                currentDepth++;
                for (TreeCube childrenTree : tree.getChildrenTreeCubes()) {
                    computeDepth(childrenTree, currentDepth);
                }
            }
        }
        return tree;
    }

    public static TreeCube findNode(TreeCube tree, String nodeEsid, Integer depth) {
        if (tree != null) {
            tree = computeDepth(tree);
            Map<String, TreeCube> treeMap = treeToMap(tree);
            TreeCube node = treeMap.get(nodeEsid);
            if (node != null) {
                int currentDepth = node.getDepth();
                if (depth == null || depth == 0 || depth.equals(currentDepth)) {
                    return node;
                } else if (currentDepth > depth) {
                    //向上关联
                    while (!Objects.equals(currentDepth, depth)) {
                        node = treeMap.get(node.getParentESID());
                        currentDepth = node.getDepth();
                    }
                    return node;
                } else {
                    //不允许向下关联
                    return null;
                }
            }
        }
        return null;
    }



    /**
     * @Description: 获取Map List中指定字段值set
     * @Param:
     * @return:
     * @Author: Li Hao
     * @Date: 2019/3/25
     */
    public static TreeSet<Object> getFieldSetInMapList(List<Map<String, Object>> list, String field, String nestedField) {
        if (isEmpty(list)) {
            return new TreeSet<>();
        }
        TreeSet<Object> set = new TreeSet<>();
        Object obj = null;
        for (Map<String, Object> map : list) {
            obj = Optional.ofNullable(map).orElse(new HashMap<>(0)).get(field);
            if (obj != null) {
                if (obj instanceof Collection && isEmpty((List) obj)) {
                    continue;
                }
                break;
            }
        }
        if (obj == null) {
            return new TreeSet<>();
        }
        if (obj instanceof String || obj instanceof Number) {
            list.forEach(i -> {
                if (i.get(field) != null) {
                    set.add(i.get(field));
                }
            });
        } else if (obj instanceof Collection) {
            for (Object object : ((List) obj)) {
                if (object != null) {
                    obj = object;
                    break;
                }
            }
            if (obj instanceof Map) {
                List<Map<String, Object>> mapList;
                for (Map<String, Object> map : list) {
                    mapList = (List) map.get(field);
                    if (isNotEmpty(mapList)) {
                        for (Map<String, Object> objectMap : mapList) {
                            obj = objectMap.get(nestedField);
                            if (obj != null) {
                                if (obj instanceof Collection) {
                                    set.addAll((Collection) obj);
                                } else {
                                    if (StringUtils.isNotBlank(getValue(obj))) {
                                        set.add(obj);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                List<Object> valueList;
                for (Map<String, Object> map : list) {
                    valueList = (List) map.get(field);
                    if (isNotEmpty(valueList)) {
                        set.addAll(valueList);
                    }
                }
            }
        }
        return set;
    }
}
