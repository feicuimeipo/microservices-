package com.nx.elasticsearch.service.impl;

import com.github.sd4324530.jtuple.Tuple;
import com.nx.elasticsearch.constant.AlgorithmType;
import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.entity.tuple.TupleUtil;
import com.nx.elasticsearch.entity.tuple.TwoTuple;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.QueryUtils;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.query.impl.AggGroup;
import com.nx.elasticsearch.service.CommonDataService;
import com.nx.elasticsearch.service.ESService;
import com.nx.elasticsearch.utils.EsMapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者：王坤造
 * 时间：2017/10/24
 * 名称：
 * 备注：
 */
@Service
public class CommonESServiceImpl implements CommonDataService {
    private static final Logger logger = LoggerFactory.getLogger(CommonESServiceImpl.class);
    private final ESService esService = new ESServiceImpl();

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * page:分页对象【可以设置当前查询页数,每页条数,排序字段及排序方式】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public Page<Map<String, Object>> getPage(String index, List<Query> queries, Page page, String... showFields) {
        return esService.getPage(index, queries, page, showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(不分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * orderColumns:排序字段【为null,则不排序】
     * orderDirs:排序方式【为null或者长度小于排序字段,则默认为升序】
     * count:要获取总条数
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public List<Map<String, Object>> getList(String index, List<Query> queries, String[] orderColumns, String[] orderDirs, long count, String... showFields) {
        return esService.getList(index, queries, orderColumns, orderDirs, count, showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(不分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * orderColumns:排序字段【为null,则不排序】
     * orderDirs:排序方式【为null或者长度小于排序字段,则默认为升序】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public List<Map<String, Object>> getList(String index, List<Query> queries, String[] orderColumns, String[] orderDirs, String... showFields) {
        return esService.getList(index, queries, orderColumns, orderDirs, -1, showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-23 下午6:24
     * 名称：根据查询条件查询一条(只显示第一条,底下的忽略)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public Map<String, Object> getOne(String index, List<Query> queries, String... showFields) {
        return esService.getOne(index, queries, showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 2017/1/20 16:34
     * 名称：根据esid查询一条
     * 备注：
     * index:索引名称
     * esid:esid
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public Map<String, Object> getOne(String index, Object esid, String... showFields) {
        return esService.getOne(index, esid, showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：根据查询条件获取总条数
     * 备注：
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回0,list为null则查询全部数据条数】
     */
    @Override
    public long getCount(String index, List<Query> queries) {
        return esService.getCount(index, queries);
    }

    /**
     * @author: 王坤造
     * @date: 2017/3/15 16:25
     * @comment: 新增/修改字段
     * @return:
     * @notes: 1.必须传esid
     * 2.需要修改的字段和值
     */
    @Override
    public TwoTuple<Boolean, Object> insertOrUpdateFields(String index, Object esid, Map<String, Object> map) {
        map.remove(ESService.ESID_FIELD);
        boolean b = esService.insertOrUpdateFields(index, esid, map);
        if (b) {
            return TupleUtil.tuple(b, esid);
        }
        return TupleUtil.tuple(b, null);
    }

    /**
     * @author: 王坤造
     * @date: 2017/3/22 14:27
     * @comment: 真正物理删除
     * @return:
     * @notes:
     */
    @Override
    public boolean delete(String index, String esid) {
        return esService.deleteReal(index, esid);
    }

    @Override
    public boolean deleteIndexAllData(String index) {
        return esService.deleteIndexAllData(index);
    }

    @Override
    public List<Integer> insertOrUpdateBulk(String index, List<Map<String, Object>> objs) {
        Tuple tuple = esService.insertOrReplaceBulk(index, objs, ArrayList::new, (l, t) -> l.add(t.get(2)), null, null);
        Object result = tuple.get(0);
        if (result == null) {
            return Collections.emptyList();
        }
        return (List<Integer>) result;
    }

    @Override
    public List<Map<String, Object>> updateFieldsBulk(String index, List<Map<String, Object>> list) throws Exception {
        return esService.updateFieldsBulk(index, list);
    }

    @Override
    public int queryForInt(String sql, Map<String, Object> data) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> data) {
        return null;
    }

    @Override
    public List<String> getFieldGroup(String index, List<Query> queries, String field) {
        Object object = getSingleAggregationsObject(index, queries, -1, field, true);
        List<String> list = (List<String>) object;
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public Object getSingleAggregationsObject(String indexTable, List<Query> queryList, int total, String field, Boolean orderType) {
        ArrayList<AggBuilderService> aggss = new ArrayList<>();
        AggGroup group = AggGroup.getInstance(field);
        if (orderType != null) {
            group.setOrder(AggGroup.Order.orderField(orderType));
        }
        if (total == -1 || total > 1000) {
            group.setUseMaxSize(true);
        }
        aggss.add(group);

        Object object = null;
        try {
            object = esService.getAggregations(indexTable, queryList, total, aggss.toArray(new AggBuilderService[aggss.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public List<Map<String, Object>> getMutiAggregationsObject(String indexTable, List<Query> queryList, int total, String[] aggNames, boolean useMaxSize, String... fieldsGroup) {
        AggGroup[] aggGroups = new AggGroup[fieldsGroup.length];
        for (int i = 0; i < fieldsGroup.length; i++) {
            aggGroups[i] = AggGroup.getInstance(fieldsGroup[i]);
        }
        AggGroup aggGroup = null;
        if (aggNames != null && aggNames.length > 0) {
            AggBuilderService[] aggBuilderServices = new AggBuilderService[aggNames.length];
            for (int n = 0; n < aggNames.length; n++) {
                aggBuilderServices[n] = AggAggregate.getInstance(aggNames[n], aggNames[n], AlgorithmType.COUNT_DISTINCT);
            }
            aggGroup = AggGroup.getInstance(aggGroups).addAggBuilders(aggBuilderServices);
        } else {
            aggGroup = AggGroup.getInstance(aggGroups);
        }
        aggGroup.setUseMaxSize(useMaxSize);

        Object aggregations = null;
        try {
            aggregations = esService.getAggregations(indexTable, queryList, total, aggGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) aggregations;
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getMutiAggregationsObject(String indexTable, List<Query> queryList, int total, String[] aggNames, String... fieldsGroup) {
        return getMutiAggregationsObject(indexTable, queryList, total, aggNames, false, fieldsGroup);
    }

    @Override
    public List<Map<String, Object>> getMutiAggregationsCount(String indexTable, List<Query> queryList, int total, String[] aggNames, String... fieldsGroup) {
        return getMutiAggregationsCount(indexTable, queryList, total, aggNames, false, fieldsGroup);
    }

    @Override
    public List<Map<String, Object>> getMutiAggregationsCount(String indexTable, List<Query> queryList, int total, String[] aggNames, boolean useMaxSize, String... fieldsGroup) {
        AggGroup[] aggGroups = new AggGroup[fieldsGroup.length];
        for (int i = 0; i < fieldsGroup.length; i++) {
            aggGroups[i] = AggGroup.getInstance(fieldsGroup[i]);
        }
        AggGroup aggGroup = null;
        if (fieldsGroup.length > 1) {
            aggGroup = AggGroup.getInstance(aggGroups);
        } else {
            aggGroup = aggGroups[0];
        }

        aggGroup.setUseMaxSize(useMaxSize);
        if (aggNames.length > 0) {
            AggBuilderService[] aggBuilderServices = new AggBuilderService[aggNames.length];
            for (int n = 0; n < aggNames.length; n++) {
                aggBuilderServices[n] = AggAggregate.getInstance(aggNames[n], aggNames[n] + "_count", AlgorithmType.COUNT);
            }
            aggGroup = aggGroup.addAggBuilders(aggBuilderServices);
        }

        Object aggregations = null;
        try {
            aggregations = esService.getAggregations(indexTable, queryList, total, aggGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) aggregations;
        return resultList;
    }

    @Override
    public Page<List<Map<String, Object>>> getMutiAggregationsPage(String indexTable, List<Query> queryList, Page page, String[] aggNames, String... fieldsGroup) {
        AggGroup[] aggGroups = new AggGroup[fieldsGroup.length];
        String[] orderColumns = page.getOrderColumns();
        String[] orderDirs = page.getOrderDirs();
        for (int i = 0; i < fieldsGroup.length; i++) {
            aggGroups[i] = AggGroup.getInstance(fieldsGroup[i]);
            if (orderColumns != null) {
                for (int m = 0; m < orderColumns.length; m++) {
                    boolean isAsc = true;
                    if ("desc".equalsIgnoreCase(orderDirs[m])) {
                        isAsc = false;
                    }
                    if (fieldsGroup[i].equals(orderColumns[m])) {
                        aggGroups[i].setOrder(AggGroup.Order.orderField(isAsc));
                    }
                }
            }
        }
        AggGroup aggGroup = null;

        if (aggNames.length > 0) {
            AggBuilderService[] aggBuilderServices = new AggBuilderService[aggNames.length];
            for (int n = 0; n < aggNames.length; n++) {
                aggBuilderServices[n] = AggAggregate.getInstance(aggNames[n], aggNames[n], AlgorithmType.COUNT_DISTINCT);
            }
            aggGroup = AggGroup.getInstance(aggGroups).addAggBuilders(aggBuilderServices);
        } else {
            aggGroup = AggGroup.getInstance(aggGroups);
        }

        try {
            esService.getAggregations(indexTable, queryList, page, aggGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) page.getItemList();
        page.setItemList(resultList);
        return page;
    }

    @Override
    public Map<String, Object> getAggAggregates(String indexTable, List<Query> queryList, AggAggregate... aggs) {
        Object object = null;
        try {
            object = esService.getAggregations(indexTable, queryList, -1, aggs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object != null) {
            return (Map<String, Object>) object;
        }
        return Collections.emptyMap();
    }

    @Override
    public Page<Map<String, Object>> getAggAggregateGroupPage(String indexTable, List<Query> queryList, Page page, String aggName, String... fieldsGroup) {
        AggGroup[] aggGroups = new AggGroup[fieldsGroup.length];
        String[] orderColumns = page.getOrderColumns();
        String[] orderDirs = page.getOrderDirs();
        if (orderColumns != null) {
            for (int i = 0; i < fieldsGroup.length; i++) {
                aggGroups[i] = AggGroup.getInstance(fieldsGroup[i]);
                for (int m = 0; m < orderColumns.length; m++) {
                    boolean isAsc = true;
                    if ("desc".equalsIgnoreCase(orderDirs[m])) {
                        isAsc = false;
                    }
                    if (fieldsGroup[i].equals(orderColumns[m])) {
                        aggGroups[i].setOrder(AggGroup.Order.orderField(isAsc));
                    }
                }
            }
        } else {
            for (int i = 0; i < fieldsGroup.length; i++) {
                aggGroups[i] = AggGroup.getInstance(fieldsGroup[i]).setOrder(AggGroup.Order.orderField(false));
            }
        }
        AggBuilderService aggBuilderServices = AggAggregate.getInstance(aggName, aggName, AlgorithmType.COUNT_DISTINCT);
        AggGroup agg = AggGroup.getInstance(aggGroups).addAggBuilders(aggBuilderServices);

        try {
            page = esService.getAggregations(indexTable, queryList, page, agg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) page.getItemList();
        page.setItemList(mapList);
        return page;
    }

    @Override
    public List<Map<String, Object>> getAggAggregatesGroup(String indexTable, List<Query> queryList, int total, String field) {
        AggGroup aggGroup = AggGroup.getInstance(field);
        aggGroup.setOrder(AggGroup.Order.orderField(true));
        AggAggregate aggAggregate = AggAggregate.getInstance(field, field + "_alias", AlgorithmType.COUNT);
        aggGroup.addAggBuilders(aggAggregate).setOrder(AggGroup.Order.orderAggregation(false, field + "_alias"));
        Object object = null;
        try {
            object = esService.getAggregations(indexTable, queryList, total, aggGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) object;
        return mapList;
    }

    @Override
    public List<Map<String, Object>> getAggGroupAgg(String indexTable, List<Query> queryList, int total, String field, boolean sortType, String aggName) {
        AggGroup aggGroup = AggGroup.getInstance(field);
        aggGroup.setOrder(AggGroup.Order.orderField(sortType));
        aggGroup.addAggBuilders(AggAggregate.getInstance(aggName, aggName, AlgorithmType.COUNT_DISTINCT));
        Object object = null;
        try {
            object = esService.getAggregations(indexTable, queryList, total, aggGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list;
    }

    @Override
    public List<String> getSingleAggregationsList(String indexTableName, List<Query> queries, String field) {
        Object object = getSingleAggregationsObject(indexTableName, queries, -1, field, true);
        List<String> list = (List<String>) object;
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public void updateData(String tableName, Map<String, Object> data) {
        boolean isSucc = false;
        try {
            isSucc = StringUtils.isNotEmpty(esService.insertOrReplace(tableName, data.get("esid"), data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isSucc) {
            logger.info("ES数据存储失败，索引:" + tableName + " 数据:" + data.toString());
        }
    }

    @Override
    public void updateDataList(String tableName, List<Map<String, Object>> dataList) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            List<Map<String, Object>> errorList = esService.insertOrReplaceBulk(tableName, dataList);
            if (CollectionUtils.isNotEmpty(errorList)) {
                for (Map<String, Object> mapError : errorList) {
                    logger.info("ES数据存储失败，索引:" + tableName + " 数据:" + mapError.toString());
                }
            }
        }
    }

    @Override
    public void updateBySelect(String tableName, List<Query> queryList, Map<String, Object> updateMap) {
        List<Map<String, Object>> errorList = esService.updateBySelect(tableName, queryList, updateMap);
        if (CollectionUtils.isNotEmpty(errorList)) {
            for (Map<String, Object> map : errorList) {
                logger.info("ES数据存储失败，索引:" + tableName + " 数据:" + map.toString());
            }
        }
    }

    @Override
    public List<String> deleteRealBulk(String index, List<String> list) throws Exception {
        return esService.deleteRealBulk(index, list);
    }

    @Override
    public List<Map<String, Object>> associateFields(String index, List<Map<String, Object>> list, List<Query> queries, String field, boolean isCollection, String associatedField, String... fields) {
        if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(index) || StringUtils.isEmpty(field) || StringUtils.isEmpty(associatedField) || ArrayUtils.isEmpty(fields)) {
            return list;
        }
        Set<String> valueSet = list.stream().flatMap(i -> EsMapUtils.getListString(i, field).stream()).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(valueSet)) {
            if (queries == null) {
                queries = new ArrayList<>();
            }
            queries.add(QueryUtils.In(associatedField, valueSet.toArray()));
            if (!ArrayUtils.contains(fields, associatedField)) {
                fields = ArrayUtils.add(fields, associatedField);
            }
            List<Map<String, Object>> associatedList = getList(index, queries, null, null, fields);
            Map<String, Map<String, Object>> groupMap = new HashMap<>();
            associatedList.forEach(i -> {
                List<String> values = EsMapUtils.getListString(i, associatedField);
                if (CollectionUtils.isNotEmpty(values)) {
                    values.forEach(value -> groupMap.put(value, i));
                }
            });
            for (Map<String, Object> map : list) {
                if (isCollection) {
                    List<String> values = EsMapUtils.getListString(map, field);
                    for (String s : fields) {
                        map.put(index + "_" + s,
                                values.stream().map(value -> EsMapUtils.getString(Optional.ofNullable(groupMap.get(value)).orElse(new HashMap<>()), s))
                                        .filter(StringUtils::isNotBlank)
                                        .collect(Collectors.toList()));
                    }
                } else {
                    String val = getValue(map.get(field));
                    Map<String, Object> associatedMap = groupMap.get(val);
                    if (EsMapUtils.isNotEmpty(associatedMap)) {
                        EsMapUtils.copyTo(associatedMap, map, fields);
                    }
                }
            }
        }
        return list;
    }

    public static String getValue(Object obj) {
        return (obj == null) ? null : obj.toString();
    }



}