package com.nx.elasticsearch.query;


import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

/**
 * 名称：王坤造
 * 时间：2017/1/18.
 * 名称：
 * 备注：
 */
public class Query implements Serializable {

    //字段/nested嵌套的对象路径,指父字段名
    private String field;
    //值
    private Object[] values;
    //查询类型
    private QueryType queryType;
    //是否nested查询,默认不是nested查询
    private boolean nested = false;
    //nested查询分数计算方式:默认使用avg
    //private ScoreType score_mode = ScoreType.AVG;
    //子查询只返回匹配到的子对象
    private boolean onlyMatchChildrens = false;
    //子查询只返回匹配到的子对象(最多只显示100条)
    private int onlyMatchChildrensSize = 100;
    //用于存储or/and关系的查询条件
    private Query[] chiQuerys;
    //关系运算符,默认是or关系
    private boolean andOperator = false;

    public Query(QueryType queryType, String field, Object... values) {
        EsUtils.judgeObjectNotNull("queryType", queryType);
        EsUtils.judgeStringNotNull("field", field);
        this.queryType = queryType;
        this.field = field;
        if (ArrayUtils.isNotEmpty(values)) {
            //Instant对象不用转换
            this.values = Arrays.stream(values).map(o -> o instanceof Date ? ((Date) o).getTime() : (o instanceof LocalDateTime ? getTimestamp((LocalDateTime) o) : o)).toArray();
        }
    }

    //public <T> Query(QueryType queryType, String field, Collection<T> values) {
    //	this(queryType, field, (Object[]) (CollectionUtils.isEmpty(values) ? null : values.toArray()));
    //}

    /**
     * 适用于where (a =1 or b=1) and (c=3 or d=4)
     * ArrayList<Query> list = new ArrayList<Query>();
     * list.add(new Query(new Query(QueryType.EQ, "a", 1), new Query(QueryType.EQ, "b", 1)));
     * list.add(new Query(new Query(QueryType.EQ, "c", 1), new Query(QueryType.EQ, "d", 1)));
     */
    public Query(Query... chiQuerys) {
        this(false, chiQuerys);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/18 21:27
     * @comment: 里面的query是and/or关系
     * @return:
     * @notes:
     */
    public Query(boolean andOperator, Query... chiQuerys) {
        //EsUtils.judgeIterLen2AndENotNull(chiQuerys, "chiQuerys");
        this.andOperator = andOperator;
        this.chiQuerys = chiQuerys;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/18 21:26
     * @comment: 子查询方式, field是当前字段名称, 里面的query是and/or关系
     * @return:
     * @notes: 如果user字段为存在或者为null, 则这个子查询无效
     * or关系示例:where user.first='John' or user.last='Smith'
     * new Query("user", false, new Query(QueryType.EQ, "user.first", "John"), new Query(QueryType.EQ, "user.last", "Smith"))
     * and关系示例:where user.first='John' and user.last='Smith'
     * new Query("user", true, new Query(QueryType.EQ, "user.first", "John"), new Query(QueryType.EQ, "user.last", "Smith"))
     */
    public Query(String field, boolean andOperator, Query... chiQuerys) {
        //EsUtils.judgeStringNotNull(field, "field");
        //EsUtils.judgeIterNotNullAndENotNull(chiQuerys, "chiQuerys");
        this.nested = true;
        this.field = field;
        this.andOperator = andOperator;
        this.chiQuerys = chiQuerys;
    }

    //public Query setField(String field) {
    //	this.field = field;
    //	return this;
    //}
    //
    //public Query setValues(Object... values) {
    //	this.values = values;
    //	return this;
    //}
    //
    //public Query setQueryType(QueryType queryType) {
    //	this.queryType = queryType;
    //	return this;
    //}

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public Query[] getChiQuerys() {
        return chiQuerys;
    }

    public void setChiQuerys(Query[] chiQuerys) {
        this.chiQuerys = chiQuerys;
    }

    public boolean isNested() {
        return nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public boolean isAndOperator() {
        return andOperator;
    }

    public void setAndOperator(boolean andOperator) {
        this.andOperator = andOperator;
    }

    public boolean isOnlyMatchChildrens() {
        return onlyMatchChildrens;
    }

    public Query setOnlyMatchChildrens(boolean onlyMatchChildrens) {
        this.onlyMatchChildrens = onlyMatchChildrens;
        return this;
    }

    public int getOnlyMatchChildrensSize() {
        return onlyMatchChildrensSize;
    }

    public void setOnlyMatchChildrensSize(int onlyMatchChildrensSize) {
        this.onlyMatchChildrensSize = onlyMatchChildrensSize;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        //单个/多个
        if (StringUtils.isNotEmpty(field)) {
            sb.append(String.format("\"field\": \"%s\", ", field));
        }
        //单个
        if (queryType != null) {
            sb.append(String.format("\"queryType\": \"%s\", ", queryType));
        }
        if (ArrayUtils.isNotEmpty(values)) {
            sb.append("\"values\": [");
            int length = values.length;
            if (length > 1) {
                for (int i = 0, len = length - 1; i < len; i++) {
                    Object value = values[i];
                    if (value instanceof String || value instanceof Character) {
                        sb.append(String.format("\"%s\", ", value));
                    } else {
                        sb.append(String.format("%s, ", value));
                    }
                }
            }
            Object value = values[length - 1];
            if (value instanceof String || value instanceof Character) {
                sb.append(String.format("\"%s\"", value));
            } else {
                sb.append(value);
            }
            sb.append("], ");
        }
        if (onlyMatchChildrens) {
            sb.append("\"onlyMatchChildrens\": true, ");
        }
        //多个
        if (nested) {
            sb.append("\"nested\": true, ");
        }
        if (andOperator) {
            sb.append("\"andOperator\": true, ");
        }
        if (ArrayUtils.isNotEmpty(chiQuerys)) {
            sb.append("\"chiQuerys\": [");
            int length = chiQuerys.length;
            if (length > 1) {
                for (int i = 0, len = length - 1; i < len; i++) {
                    sb.append(String.format("%s, ", chiQuerys[i].toJson()));
                }
            }
            sb.append(chiQuerys[length - 1].toJson());
            sb.append("], ");
        }
        return StringUtils.removeEnd(sb.toString(), ", ") + "}";
    }

    /**
     * @author: 王坤造
     * @date: 2018/2/26 16:11
     * @comment: 不解释
     * @return:
     * @notes:
     */
    @Override
    public String toString() {
        //将values转化为字符串
        String valuesStr;
        if (ArrayUtils.isEmpty(values)) {
            valuesStr = null;
        } else {
            int length = values.length;
            String[] valueStrs = new String[length];
            for (int i = 0; i < length; ++i) {
                if (values[i] instanceof String) {
                    valueStrs[i] = String.format("\"%s\"", values[i]);
                } else if (values[i] instanceof Character) {
                    valueStrs[i] = String.format("'%s'", values[i]);
                } else {
                    valueStrs[i] = String.valueOf(values[i]);
                }
            }
            valuesStr = StringUtils.join(valueStrs, ", ");
        }
        //Query里面没有子Query
        if (ArrayUtils.isEmpty(chiQuerys)) {
            //field字段为空,这种情况禁止出现
            if (queryType == null) {
                if (StringUtils.isEmpty(valuesStr)) {
                    if (field == null) {
                        return String.format("new Query(%s, %s)", queryType, field);
                    }
                    return String.format("new Query(%s, \"%s\")", queryType, field);
                }
                if (field == null) {
                    return String.format("new Query(%s, %s, %s)", queryType, field, valuesStr);
                }
                return String.format("new Query(%s, \"%s\", %s)", queryType, field, valuesStr);
            } else {
                if (StringUtils.isEmpty(valuesStr)) {
                    if (field == null) {
                        return String.format("new Query(QueryType.%s, %s)", queryType, field);
                    }
                    return String.format("new Query(QueryType.%s, \"%s\")", queryType, field);
                }
                if (field == null) {
                    return String.format("new Query(QueryType.%s, %s, %s)", queryType, field, valuesStr);
                }
                return String.format("new Query(QueryType.%s, \"%s\", %s)", queryType, field, valuesStr);
            }
        }
        //Query里面有子Query
        int length = chiQuerys.length;
        String[] chiQueryStrs = new String[length];
        for (int i = 0; i < length; ++i) {
            chiQueryStrs[i] = chiQuerys[i].toString();
        }
        String chiQuerysStr = StringUtils.join(chiQueryStrs, ", ");
        //子查询
        if (nested) {
            if (field == null) {
                return String.format("new Query(%s, %s, %s)", field, andOperator, chiQuerysStr);
            } else {
                return String.format("new Query(\"%s\", %s, %s)", field, andOperator, chiQuerysStr);
            }
        }
        //非子查询
        if (andOperator) {
            return String.format("new Query(%s, %s)", andOperator, chiQuerysStr);
        }
        return String.format("new Query(%s)", chiQuerysStr);
    }

    /**
     * @author: 王坤造
     * @date: 2018/8/7 11:06
     * @comment: 本地時间对象(LocalDateTime)转化为時间戳
     * @return:
     * @notes: 默认使用东8区(跟LocalDateTime使用時区要一致)
     */
    private long getTimestamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }
}