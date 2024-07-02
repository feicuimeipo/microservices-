package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.constant.AggregationType;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggFieldService;
import com.nx.elasticsearch.query.AggSubAggBuilderService;
import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 名称：子查询
 * 备注：
 */
public class AggNested implements Serializable, AggBuilderService, AggFieldService, AggSubAggBuilderService, AggSubAggBuilderService.SubMust {

    //分组/聚合
    private final List<AggBuilderService> aggBuilderServices = new ArrayList<>();
    private String field;
    private String alias;

    /**
     * @author: 王坤造
     * @date: 2018/1/28 16:34
     * @comment: dubbo调用的时候必须提供默认构造函数
     * @return:
     * @notes:
     */
    public AggNested() {
    }

    private AggNested(String field, String alias) {
        EsUtils.judgeStringNotNull("field", field);
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field;
        } else {
            this.alias = alias;
        }
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggNested getInstance(String field) {
        return new AggNested(field, null);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggNested getInstance(String field, String alias) {
        return new AggNested(field, alias);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/28 21:06
     * @comment: 获取查询类型
     * @return:
     * @notes:
     */
    @Override
    public AggregationType getType() {
        return AggregationType.NESTED;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public AggNested setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public AggNested addAggBuilders(AggBuilderService... aggBuilderServices) {
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
            throw new IllegalArgumentException("【子查询】分组/聚合/分组聚合至少1个.aggBuilderServices参数禁止为空!!!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (field.equals(alias)) {
            sb.append(String.format("AggNested.getInstance(\"%s\")", field));
        } else {
            sb.append(String.format("AggNested.getInstance(\"%s\", \"%s\")", field, alias));
        }
        if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
            sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
        }
        return sb.toString();
    }
}