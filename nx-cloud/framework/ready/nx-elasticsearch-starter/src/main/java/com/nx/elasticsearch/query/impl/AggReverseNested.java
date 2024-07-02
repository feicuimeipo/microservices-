package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.query.AggFieldService;
import com.nx.elasticsearch.utils.EsUtils;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggSubAggBuilderService;
import com.nx.elasticsearch.constant.AggregationType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 名称：反向子查询
 * 备注：
 */
public class AggReverseNested implements Serializable, AggBuilderService, AggFieldService, AggSubAggBuilderService, AggSubAggBuilderService.SubMust {
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
    public AggReverseNested() {
    }

    /**
     * @param field 可为null
     * @param alias
     */
    private AggReverseNested(String field, String alias) {
        EsUtils.judgeStringsNotAllNull("field", "alias", field, alias);
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
     * @notes: alias禁止设置为null
     */
    public static AggReverseNested getInstance(String field, String alias) {
        return new AggReverseNested(field, alias);
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
        return AggregationType.REVERSE_NESTED;
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
    public AggReverseNested setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public AggReverseNested addAggBuilders(AggBuilderService... aggBuilderServices) {
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
            throw new IllegalArgumentException("【反向子查询】分组/聚合/分组聚合至少1个.aggBuilderServices参数禁止为空!!!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(field)) {
            sb.append(String.format("AggReverseNested.getInstance(%s, \"%s\")", field, alias));
        } else if (StringUtils.isEmpty(alias)) {
            sb.append(String.format("AggReverseNested.getInstance(\"%s\", %s)", field, alias));
        } else {
            sb.append(String.format("AggReverseNested.getInstance(\"%s\", \"%s\")", field, alias));
        }
        if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
            sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
        }
        return sb.toString();
    }
}
