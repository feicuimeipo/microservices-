package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.constant.AggregationType;
import com.nx.elasticsearch.constant.AlgorithmType;
import com.nx.elasticsearch.query.AbstractAggSize;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggFieldService;
import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 作者：王坤造
 * 时间：2018/1/29
 * 名称：聚合函数
 * 备注：
 */
public class AggAggregate extends AbstractAggSize implements Serializable, AggBuilderService, AggFieldService {

    //聚合字段
    private String field;
    //聚合别名
    private String alias;
    //大小,默认获取全部
    private int size = DEFAULT_SIZE;
    //聚合函数
    private AlgorithmType algorithmType;
    //在MAX_OTHER和MIN_OTHER聚合函数有效
    private String fieldOther;
    //在MAX_OTHER和MIN_OTHER聚合函数有效
    private String aliasOther;
    //是否使用最大size
    private boolean useMaxSize = false;

    public AggAggregate() {
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/29 16:33
     * @comment: 单个聚合函数的构造函数
     * @return:
     * @notes:
     */
    private AggAggregate(String field, String alias, AlgorithmType algorithmType) {
        EsUtils.judgeStringNotNull("field", field);
        EsUtils.judgeObjectNotNull("aggregateType", algorithmType);
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field;
        } else {
            this.alias = alias;
        }
        this.algorithmType = algorithmType;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes: 如果COUNT统计字段是数组类型, field必须设置成"_index",才能完美统计数量
     */
    public static AggAggregate getInstance(String field, AlgorithmType algorithmType) {
        return new AggAggregate(field, null, algorithmType);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes: 如果COUNT统计字段是数组类型, field必须设置成"_index",才能完美统计数量
     */
    public static AggAggregate getInstance(String field, String alias, AlgorithmType algorithmType) {
        return new AggAggregate(field, alias, algorithmType);
    }

    public AggAggregate setFieldOther(String fieldOther, String aliasOther) {
        this.fieldOther = fieldOther;
        if (StringUtils.isEmpty(aliasOther)) {
            this.aliasOther = fieldOther;
        } else {
            this.aliasOther = aliasOther;
        }
        return this;
    }

    public AlgorithmType getAggregateType() {
        return algorithmType;
    }

    public String getFieldOther() {
        return fieldOther;
    }

    public AggAggregate setFieldOther(String fieldOther) {
        return setFieldOther(fieldOther, null);
    }

    public String getAliasOther() {
        return aliasOther;
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
        return AggregationType.AGGREGATE;
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
    public AggAggregate setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public AggAggregate setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean isUseMaxSize() {
        return useMaxSize;
    }

    @Override
    public AggAggregate setUseMaxSize(boolean useMaxSize) {
        this.useMaxSize = useMaxSize;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (field.equals(alias)) {
            sb.append(String.format("AggAggregate.getInstance(\"%s\", AggregateType.%s)", field, algorithmType));
        } else {
            sb.append(String.format("AggAggregate.getInstance(\"%s\", \"%s\", AggregateType.%s)", field, alias, algorithmType));
        }
        if (size != DEFAULT_SIZE) {
            sb.append(String.format(".setSize(%s)", size));
        }
        if (StringUtils.isNotEmpty(fieldOther)) {
            if (fieldOther.equals(aliasOther)) {
                sb.append(String.format(".setFieldOther(\"%s\")", fieldOther));
            } else {
                sb.append(String.format(".setFieldOther(\"%s\", \"%s\")", fieldOther, aliasOther));
            }
        }
        if (useMaxSize) {
            sb.append(String.format(".setUseMaxSize(%s)", useMaxSize));
        }
        return sb.toString();
    }
}