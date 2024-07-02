package com.nx.elasticsearch.service.impl;

import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.service.ES7Service;
import com.nx.elasticsearch.service.ESDataService;
import com.nx.elasticsearch.service.ESService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


@Service
public class ESDataServiceImpl implements ESDataService {
    private final static Logger logger = LoggerFactory.getLogger(ESDataServiceImpl.class);

    @Resource
    private ES7Service es7Service;

    @Resource
    private ESService esService;

    @Override
    public void listAndAction(String index, List<Query> queries, int size, Consumer<List<Map<String, Object>>> func, String... showFields) {
        String id = null;
        try {
            while (true) {
                List<Object> ls = es7Service.getListSub(index, queries, null, id, size, showFields);
                List<Map<String, Object>> ls1 = (List<Map<String, Object>>) ls.get(0);
                id = (String) ls.get(1);
                if (CollectionUtils.isNotEmpty(ls1) && ls1 != null) {
                    func.accept(ls1);
                }
                if (ls1.size() < size) {
                    break;
                }
            }
        } finally {
            try {
                es7Service.clearScrollID(index, id);
            } catch (IllegalArgumentException e) {
                logger.error("滚动id为空", e);
            }
        }
    }

    @Override
    public void groupOneField(String index, List<Query> queries, String field, boolean asc, int batchSize, Consumer<List<Object>> func) {
        Object after = null;
        while (true) {
            List<Object> ls = es7Service.getAggregationsGroup(index, queries, batchSize, after, field, asc);
            if (CollectionUtils.isEmpty(ls)) {
                break;
            }
            if (func != null) {
                func.accept(ls);
            }
            if (ls.size() < batchSize) {
                break;
            }
            after = ls.get(batchSize - 1);
        }
    }

    @Override
    public void groupAndAgg(String index, List<Query> queries,
                            String[] fields, int batchSize, Consumer<List<Map<String, Object>>> func,
                            AggAggregate... aggBuilders) {
        Map<String, Object> after = null;

        List<String> field_list = Arrays.asList(fields);
        while (true) {
            List<Map<String, Object>> ls = es7Service.getAggregationsGroupsAgg(index, queries, batchSize, after, field_list, aggBuilders);
            if (CollectionUtils.isEmpty(ls)) {
                break;
            }
            if (func != null) {
                func.accept(ls);
            }
            if (ls.size() < batchSize) {
                break;
            }
            after = ls.get(batchSize - 1);
        }
    }

    @Override
    public void listAndUpdate(String index, List<Query> qry, String field,
                              Function<Map<String, Object>, Map<String, Object>> func,
                              int batchGetCount, int batchUpdateCount,
                              String... fields) {
        LinkedHashMap<String, Boolean> orders = new LinkedHashMap<>();
        orders.put(field, true);
        Object[] searchValues = null;
        Page<Map<String, Object>> page;
        List<Map<String, Object>> itemList;
        List<Map<String, Object>> update = new ArrayList<>();
        do {
            page = esService.getPage(index, qry, batchGetCount, orders, searchValues, fields);
            itemList = page.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                for (Map<String, Object> item : itemList) {
                    Map<String, Object> rlt = func.apply(item);
                    if (rlt != null) {
                        update.add(rlt);
                    }
                }
                if (update.size() > batchUpdateCount) {
                    try {
                        esService.updateFieldsBulk(index, update);
                        update.clear();
                    } catch (Exception err) {
                        logger.error("批量更新错误:{}", err);
                    }
                }
                Map<String, Object> lastOne = itemList.get(itemList.size() - 1);
                searchValues = new Object[]{lastOne.get(field)};
            }
        }
        while (CollectionUtils.isNotEmpty(itemList));
        if (update.size() > 0) {
            try {
                esService.updateFieldsBulk(index, update);
            } catch (Exception e) {
                logger.error("最后批量更新错误:{}", e);
            }
        }
    }


    @Override
    public List<Map<String, Object>> batchGetAllData(String index, List<Query> queries, int size,
                                                     String... showFields) {
        List<Map<String, Object>> data = new ArrayList<>();
        String id = null;
        try {
            while (true) {
                List<Object> ls = es7Service.getListSub(index, queries, null, id, size, showFields);
                List<Map<String, Object>> ls1 = (List<Map<String, Object>>) ls.get(0);
                id = (String) ls.get(1);
                if (CollectionUtils.isNotEmpty(ls1) && ls1 != null) {
                    data.addAll(ls1);
                }
                if (ls1.size() < size) {
                    break;
                }
            }
        } finally {
            try {
                es7Service.clearScrollID(index, id);
            } catch (IllegalArgumentException e) {
                logger.error("滚动id为空", e);
            }
        }
        return data;
    }
}