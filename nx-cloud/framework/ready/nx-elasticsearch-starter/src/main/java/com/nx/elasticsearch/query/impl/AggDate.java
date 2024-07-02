package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.utils.EsDateInterval;
import com.nx.elasticsearch.utils.EsUtils;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggFieldService;
import com.nx.elasticsearch.query.AggSubAggBuilderService;
import com.nx.elasticsearch.constant.AggregationType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者：王坤造
 * 时间：2018/12/13
 * 名称：
 * 备注：
 */
public class AggDate implements Serializable, AggBuilderService, AggFieldService, AggSubAggBuilderService {
    //单字段分组字段名
    private String field;
    //单字段分组别名/多字段分组第1个别名
    private String alias;
    //单字段分组别名/多字段分组第1个别名
    private EsDateInterval esDateInterval;
    //排序方式
    private AggGroup.Order order;
    //设置每个桶偏移毫秒数(默认:0-24h,3600000:1-1h
    private long offset = -28800000;
    //设置返回字符串格式,不可用
    //private String format;
    //设置当前使用時区,不可用
    //private DateTimeZone timeZone;
    //聚合函数
    private List<AggBuilderService> aggBuilderServices = Collections.EMPTY_LIST;

    public AggDate() {
    }

    private AggDate(String field, String alias, EsDateInterval esDateInterval) {
        EsUtils.judgeStringNotNull("field", field);
        EsUtils.judgeObjectNotNull("esDateInterval", esDateInterval);
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field;
        } else {
            this.alias = alias;
        }
        this.esDateInterval = esDateInterval;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggDate getInstance(String field, String alias, EsDateInterval esDateInterval) {
        return new AggDate(field, alias, esDateInterval);
    }

    public static AggDate getInstance(String field, EsDateInterval esDateInterval) {
        return new AggDate(field, null, esDateInterval);
    }

    public EsDateInterval getDateInterval() {
        return esDateInterval;
    }

    public long getOffset() {
        return offset;
    }

    public AggGroup.Order getOrder() {
        return order;
    }

    public AggDate setOrder(AggGroup.Order order) {
        this.order = order;
        return this;
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
        return AggregationType.DATE;
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
    public AggDate setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public AggDate addAggBuilders(AggBuilderService... aggBuilderServices) {
        if (ArrayUtils.isNotEmpty(aggBuilderServices)) {
            if (CollectionUtils.isEmpty(this.aggBuilderServices)) {
                this.aggBuilderServices = new ArrayList<>(aggBuilderServices.length);
            }
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (field.equals(alias)) {
            sb.append(String.format("AggDate.getInstance(\"%s\", %s)", field, esDateInterval));
        } else {
            sb.append(String.format("AggDate.getInstance(\"%s\", \"%s\", %s)", field, alias, esDateInterval));
        }
        if (order != null) {
            sb.append(String.format(".setOrder(%s)", order));
        }
        if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
            sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
        }
        return sb.toString();
    }
}
