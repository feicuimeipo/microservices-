package com.nx.elasticsearch.query.impl;


import com.nx.elasticsearch.constant.ESConstants;
import com.nx.elasticsearch.utils.EsSortType;
import com.nx.elasticsearch.constant.AlgorithmType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 作者：王坤造
 * 名称：
 * 备注：
 */
public class Aggregate implements Serializable {

    //所有别名集合,用来判断聚合函数列表里面的别名是否重名
    private final Set<String> aliases = new HashSet<String>();
    //聚合字段
    private String field;
    //分组字段
    private String[] fields;
    //分组/聚合别名
    private String alias;
    //大小
    private int size;
    //聚合函数
    private AlgorithmType algorithmType;
    //排序方式
    private EsSortType esSortType;
    //在MAX_OTHER和MIN_OTHER聚合函数有效
    private String fieldOther;
    //在MAX_OTHER和MIN_OTHER聚合函数有效
    private String aliasOther;
    //聚合函数列表
    private List<Aggregate> aggs = new ArrayList<>();
    //聚合函数是否有排序,排序字段当且仅当只有一个[如果聚合函数有排序,则分组的字段排序失效]
    private boolean isAggSort = false;
    //自定义的聚合函数排序方式
    private Aggregate DIY_Agg;
    //多个字段时的聚合函数排序方式
    private Aggregate Mul_Agg;
    //聚合函数列表更新的标志
    private boolean isUpdate = false;

    /**
     * @author: 王坤造
     * @date: 2017/8/28 11:49
     * @comment: dubbo调用的时候必须提供默认构造函数
     * @return:
     * @notes:
     */
    public Aggregate() {
    }

    /**
     * @author: 王坤造
     * @date: 2017/7/3 15:52
     * @comment: 分组的构造函数
     * @return:
     * @notes:
     */
    public Aggregate(EsSortType esSortType, String alias, String... fields) {
        this.fields = fields;
        if (StringUtils.isEmpty(alias)) {
            this.alias = fields[0];
        } else {
            this.alias = alias;
        }
        this.esSortType = esSortType;
    }

    /**
     * @author: 王坤造
     * @date: 2017/7/3 15:53
     * @comment: 聚合函数的构造函数
     * @return:
     * @notes: size:只针对COUNT_DISTINCT使用
     */
    public Aggregate(String field, String alias, AlgorithmType algorithmType, EsSortType esSortType, int size) {
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field + algorithmType;
        } else {
            this.alias = alias;
        }
        this.algorithmType = algorithmType;
        this.esSortType = esSortType;
        if (AlgorithmType.COUNT_DISTINCT == algorithmType) {
            if (size > 0 && size < ESConstants.MAX_SIZE_CARDINALITY) {
                this.size = size;
            } else {
                this.size = ESConstants.MAX_SIZE_CARDINALITY;
            }
        }
    }

    /**
     * @author: 王坤造
     * @date: 2017/7/3 15:53
     * @comment: 聚合函数的构造函数
     * @return:
     * @notes:
     */
    public Aggregate(String field, String alias, AlgorithmType algorithmType, EsSortType esSortType, String fieldOther, String aliasOther) {
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field + algorithmType;
        } else {
            this.alias = alias;
        }
        this.algorithmType = algorithmType;
        this.esSortType = esSortType;
        if (algorithmType == AlgorithmType.MAX_OTHER || algorithmType == AlgorithmType.MIN_OTHER) {
            this.fieldOther = fieldOther;
            if (StringUtils.isEmpty(aliasOther)) {
                this.aliasOther = fieldOther + algorithmType;
            } else {
                this.aliasOther = aliasOther;
            }
        }
    }

    public String getField() {
        return field;
    }

    public String getAlias() {
        return alias;
    }

    public int getSize() {
        return size;
    }

    public String[] getFields() {
        return fields;
    }

    public AlgorithmType getAggregateType() {
        return algorithmType;
    }

    public EsSortType getSortType() {
        return esSortType;
    }

    public String getFieldOther() {
        return fieldOther;
    }

    public String getAliasOther() {
        return aliasOther;
    }

    /**
     * @author: 王坤造
     * @date: 2017/7/3 15:54
     * @comment: 获取聚合函数集合
     * @return:
     * @notes:
     */
    public List<Aggregate> getAggs() {
        deal();
        return aggs;
    }

    public boolean isAggSort() {
        deal();
        return isAggSort;
    }

    public Aggregate getDIY_Agg() {
        return DIY_Agg;
    }

    public Aggregate getMul_Agg() {
        return Mul_Agg;
    }

    public void addChiAggregate(Aggregate... agg) {
        aggs.addAll(Arrays.asList(agg));
        isUpdate = true;
    }

    /**
     * @author: 王坤造
     * @date: 2017/7/4 14:37
     * @comment: 验证别名是否有冲突及设置聚合函数是否有排序
     * @return:
     * @notes:
     */
    private void deal() {
        //如果有添加聚合函数,需要重新验证是否有别名冲突
        if (isUpdate) {
            aliases.clear();
            for (Aggregate agg : aggs) {
                //别名存在,则添加聚合函数操作名称
                while (aliases.contains(agg.alias)) {
                    agg.alias = agg.alias + agg.algorithmType;
                }
                aliases.add(agg.alias);
                if (agg.algorithmType == AlgorithmType.MAX_OTHER || agg.algorithmType == AlgorithmType.MIN_OTHER) {
                    //别名存在,则添加聚合函数操作名称
                    while (aliases.contains(agg.aliasOther)) {
                        agg.aliasOther = agg.aliasOther + agg.algorithmType;
                    }
                    aliases.add(agg.aliasOther);
                }
                //设置聚合函数是否有排序
                if (agg.esSortType != null) {
                    if (isAggSort) {
                        agg.esSortType = null;
                    } else {
                        if (agg.algorithmType == AlgorithmType.MEDIAN || agg.algorithmType == AlgorithmType.MAX_OTHER || agg.algorithmType == AlgorithmType.MIN_OTHER) {
                            DIY_Agg = agg;
                        } else if (fields.length > 1) {
                            Mul_Agg = agg;
                        }
                        isAggSort = true;
                    }
                }
            }
            isUpdate = false;
        }
    }

    @Override
    public String toString() {
        return "Aggregate{" +
                "field='" + field + '\'' +
                ", fields=" + Arrays.toString(fields) +
                ", alias='" + alias + '\'' +
                ", size=" + size +
                ", aggregateType=" + algorithmType +
                ", esSortType=" + esSortType +
                ", fieldOther='" + fieldOther + '\'' +
                ", aliasOther='" + aliasOther + '\'' +
                ", aggs=" + aggs +
                ", isAggSort=" + isAggSort +
                ", DIY_Agg=" + DIY_Agg +
                ", Mul_Agg=" + Mul_Agg +
                ", isUpdate=" + isUpdate +
                ", aliases=" + aliases +
                '}';
    }
}