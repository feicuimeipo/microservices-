package com.nx.elasticsearch.query;

import com.nx.elasticsearch.entity.index_mapping.EsIndexField;
import com.nx.elasticsearch.entity.index_mapping.EsIndexTree;
//import com.nx.server.newsearch.entity.index_mapping.EsIndexField;
//import com.nx.server.newsearch.entity.index_mapping.EsIndexTree;
//import com.nx.server.newsearch.entity.index_mapping.Mapping;
import com.nx.elasticsearch.entity.index_mapping.Mapping;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 作者：刘向
 * 时间：21/5/24
 * 名称：
 * 备注：
 */
public class SearchUtils {

    //private static final Logger logger = LoggerFactory.getLogger(SearchUtils.class);

    /**
     * 生成元数据结构主方法
     *
     * @param esMetadataList
     * @return
     */
    public static List<EsIndexTree> getEsMetadataTree(List<Mapping> esMetadataList) {
        Set<String> floors = new HashSet<>();
        //获取最上层的nested
        for (Mapping m : esMetadataList) {
            if (m.getParent() == null) {
                floors.add(m.getNestedFloor());
            }
        }
        //生成nested层级结构
        List<EsIndexTree> list = new ArrayList<>();
        for (String floor : floors) {
            list.add(getEsIndexTreeVo(floor, esMetadataList));
        }
        //元数据结构拼装最末端字段
        for (EsIndexTree treeVo : list) {
            getFieldSub(treeVo, esMetadataList);
        }
        return list;
    }

    /**
     * 拼装字段类型元数据
     *
     * @param treeVo
     * @param esMetadataList
     * @return
     */
    private static void getFieldSub(EsIndexTree treeVo, List<Mapping> esMetadataList) {
        List tmpList = treeVo.getItems();
        if (CollectionUtils.isEmpty(tmpList)) {
            // 设置下级属性字段
            List fields = esMetadataList.stream().filter(item -> item.getNestedFloor().equals(treeVo.getIndexName()))
                    .map(item -> {
                        EsIndexField vo = new EsIndexField();
                        BeanUtils.copyProperties(item, vo);
                        vo.setPath(treeVo.getPath() + "." + vo.getField());
                        return vo;
                    })
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(treeVo.getItems())) {
                treeVo.getItems().addAll(fields);
            } else {
                treeVo.setItems(fields);
            }
        } else {
            for (int i = 0; i < tmpList.size(); i++) {
                if (tmpList.get(i) instanceof EsIndexTree) {
                    getFieldSub((EsIndexTree) tmpList.get(i), esMetadataList);

                    // 设置同级属性字段
                    List fields = esMetadataList.stream().filter(item -> item.getNestedFloor().equals(treeVo.getIndexName()))
                            .map(item -> {
                                EsIndexField vo = new EsIndexField();
                                BeanUtils.copyProperties(item, vo);
                                vo.setPath(treeVo.getPath() + "." + vo.getField());
                                return vo;
                            })
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(fields)) {
                        if (CollectionUtils.isNotEmpty(treeVo.getItems())) {
                            treeVo.getItems().addAll(fields);
                        } else {
                            treeVo.setItems(fields);
                        }
                    }
                }
            }
        }
    }

    /**
     * 拼装元数据层级结构
     *
     * @param floor
     * @param esMetadataList
     * @return
     */
    private static EsIndexTree getEsIndexTreeVo(String floor, List<Mapping> esMetadataList) {
        EsIndexTree vo = new EsIndexTree();
        //拼装第一层元数据结构
        List<EsIndexTree> subTree = esMetadataList.stream().filter(item -> item.getNestedFloor().equals(floor))
                .map(item -> {
                    EsIndexTree subVo = new EsIndexTree();
                    subVo.setIndexName(item.getNestedFloor());
                    subVo.setParentIndexName(item.getParent());
                    subVo.setPath(subVo.getIndexName());
                    return subVo;
                })
                .distinct()
                .collect(Collectors.toList());
        //拼装下级结构
        if (!CollectionUtils.isEmpty(subTree)) {
            getSub(esMetadataList, subTree);
            vo = subTree.get(0);
        }
        return vo;
    }

    /**
     * 递归拼装元数据下级层级结构
     *
     * @param esTree
     * @param list
     * @return
     */
    private static void getSub(List<Mapping> esTree, List<EsIndexTree> list) {
        //递归拼装下级结构
        if (!CollectionUtils.isEmpty(list)) {
            for (EsIndexTree esIndexTree : list) {
                List<EsIndexTree> tmpList = getSubTree(esTree, esIndexTree);
                if (!CollectionUtils.isEmpty(tmpList)) {
                    esIndexTree.setItems(tmpList);
                    getSub(esTree, tmpList);
                }
            }
        }
    }

    /**
     * 递归拼装元数据下级层级结构
     *
     * @param esTree
     * @param parentEsIndexMetadata
     * @return
     */
    private static List<EsIndexTree> getSubTree(List<Mapping> esTree, EsIndexTree parentEsIndexMetadata) {
        List<EsIndexTree> subTree = esTree.stream().filter(item -> item.getParent() != null && item.getParent().equals(parentEsIndexMetadata.getIndexName()))
                .map(item -> {
                    EsIndexTree subVo = new EsIndexTree();
                    subVo.setIndexName(item.getNestedFloor());
                    subVo.setParentIndexName(parentEsIndexMetadata.getIndexName());
                    subVo.setType(item.getFieldType());
                    subVo.setPath(subVo.getParentIndexName() + "." + subVo.getIndexName());
                    return subVo;
                })
                .distinct()
                .collect(Collectors.toList());
        return subTree;
    }
}
