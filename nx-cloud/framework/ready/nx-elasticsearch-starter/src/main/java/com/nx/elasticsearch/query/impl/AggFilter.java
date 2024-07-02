package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.constant.AggregationType;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggSubAggBuilderService;
import com.nx.elasticsearch.query.Query;


import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：王坤造
 * 时间：2018/4/1
 * 名称：过滤
 * 备注：
 */
public class AggFilter implements Serializable, AggBuilderService, AggSubAggBuilderService, AggSubAggBuilderService.SubMust {

    private final List<AggBuilderService> aggBuilderServices = new ArrayList<>();
    private String alias;
    private Query filter;

    public AggFilter() {
    }

    private AggFilter(String alias, Query filter) {
        EsUtils.judgeStringNotNull("alias", alias);
        EsUtils.judgeObjectNotNull("filter", filter);
        this.filter = filter;
        this.alias = alias;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggFilter getInstance(String alias, Query filter) {
        return new AggFilter(alias, filter);
    }

    public Query getFilter() {
        return filter;
    }

    @Override
    public AggregationType getType() {
        return AggregationType.FILTER;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public AggFilter setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public AggFilter addAggBuilders(AggBuilderService... aggBuilderServices) {
        if (ArrayUtils.isNotEmpty(aggBuilderServices)) {
            this.aggBuilderServices.addAll(Arrays.asList(aggBuilderServices));
        }
        return this;
    }

    @Override
    public List<AggBuilderService> getAggBuilders() {
        return aggBuilderServices;
    }

    @Override
    public AggBuilderService[] getAggBuilders2() {
        return aggBuilderServices.toArray(new AggBuilderService[aggBuilderServices.size()]);
    }

    @Override
    public void judgeForcedSubAggBuilder() {
        if (CollectionUtils.isEmpty(aggBuilderServices)) {
            //强制必须有子分组聚合
            throw new IllegalArgumentException("【过滤】分组/聚合/分组聚合至少1个.aggBuilderServices参数禁止为空!!!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("AggFilter.getInstance(\"%s\", %s)", alias, filter));
        if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
            sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
        }
        return sb.toString();
    }
}