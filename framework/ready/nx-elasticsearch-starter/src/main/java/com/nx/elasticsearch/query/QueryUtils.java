package com.nx.elasticsearch.query;

import com.google.gson.Gson;
import com.nx.elasticsearch.service.ESService;
import com.nx.elasticsearch.utils.EsMyLocalizable;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询的工具类
 */
public class QueryUtils {
    private static final Gson GSON = new Gson();

    public static Query GT(String field, Long val) {
        return new Query(QueryType.GT, field, val);
    }

    public static Query GE(String field, Long val) {
        return new Query(QueryType.GE, field, val);
    }

    public static Query LE(String field, Long val) {
        return new Query(QueryType.LE, field, val);
    }

    public static Query LT(String field, Long val) {
        return new Query(QueryType.LT, field, val);
    }

    public static Query Eq(String field, Object val) {
        return new Query(QueryType.EQ, field, val);
    }

    public static Query Eq(String field, String val) {
        return new Query(QueryType.EQ, field, val);
    }

    public static Query isNull(String field) {
        return new Query(QueryType.EQ, field, ESService.NULL_VALUE);
    }

    public static Query Ne(String field, String val) {
        return new Query(QueryType.NE, field, val);
    }

    public static Query Ne(String field, Object val) {
        return new Query(QueryType.NE, field, val);
    }

    public static Query NeOrNull(String field, String val) {
        return StringUtils.isNotEmpty(val) ? new Query(QueryType.NE, field, val) : null;
    }

    public static Query Like(String field, String val) {
        return new Query(QueryType.LIKE, field, val);
    }

    public static Query Like(String field, Object val) {
        return new Query(QueryType.LIKE, field, val);
    }

    public static Query NL(String field, Object val) {
        return new Query(QueryType.NL, field, val);
    }

    public static Query NotIn(String field, String... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE, "查询NotIn参数时值不能为空");
        }
        return new Query(QueryType.NI, field, val);
    }

    public static Query NotIn(String field, Object... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询NotIn参数时值不能为空");
        }
        return new Query(QueryType.NI, field, val);
    }

    public static Query NotIn(String field, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询NotIn参数时值不能为空");
        }
        return new Query(QueryType.NI, field, collection.toArray());
    }

    public static Query In(String field, String... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询In参数时值不能为空");
        }
        return new Query(QueryType.IN, field, val);
    }

    public static Query In(String field, Object... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询In参数时值不能为空");
        }
        return new Query(QueryType.IN, field, val);
    }

    public static Query In(String field, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询In参数时值不能为空");
        }
        return new Query(QueryType.IN, field, collection.toArray());
    }

    public static Query InLike(String field, Object... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询InLike参数时值不能为空");
        }
        return new Query(QueryType.IN_LIKE, field, val);
    }

    public static Query NotInLike(String field, Object... val) {
        if (ArrayUtils.isEmpty(val)) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询NotInLike参数时值不能为空");
        }
        return And(Arrays.stream(val).map(i -> NL(field, i)).collect(Collectors.toList()));
    }

    public static Query Likes(String val, String... fields) {
        if (StringUtils.isNotEmpty(val)) {
            Query[] arrs = new Query[fields.length];
            for (int i = 0; i < fields.length; i++) {
                arrs[i] = new Query(QueryType.LIKE, fields[i], val);
            }
            return new Query(arrs);
        } else {
            return null;
        }
    }

    public static Query Bn(String field, Object arg1, Object arg2) {
        if (StringUtils.isBlank(field) || arg1 == null || arg2 == null) {
            throw new NullArgumentException(EsMyLocalizable.MESSAGE,"查询BN参数时值不能为空");
        }
        return new Query(QueryType.BN, field, arg1, arg2);
    }

    public static List<Query> list(Query... queries) {
        List<Query> list = new ArrayList<>();
        for (Query s : queries) {
            if (s != null) {
                list.add(s);
            }
        }
        return list;
    }

    public static Query Or(Query... queries) {
        return new Query(queries);
    }

    public static Query Or(List<Query> queries) {
        return CollectionUtils.isNotEmpty(queries) ? new Query(queries.toArray(new Query[0])) : null;
    }

    public static Query And(Query... queries) {
        return new Query(true, queries);
    }

    public static Query And(List<Query> queries) {
        return CollectionUtils.isNotEmpty(queries) ? new Query(true, queries.toArray(new Query[0])) : null;
    }

    private static Query fromJson(Map map) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        Object chiQuerys = map.get("chiQuerys");
        Query query;
        if (chiQuerys == null) {
            String field = (String) map.get("field");
            QueryType queryType = QueryType.valueOf((String) map.get("queryType"));
            Object values = map.get("values");
            if (values == null) {
                query = new Query(queryType, field);
            } else {
                List values1 = (List) values;
                int size = values1.size();
                for (int i = 0; i < size; i++) {
                    Object o = values1.get(i);
                    if (o != null && o instanceof Double) {
                        double d1 = (Double) o;
                        long l1 = (long) d1;
                        if (d1 - l1 == 0) {
                            values1.set(i, l1);
                        }
                    }
                }
                query = new Query(queryType, field, ((List) values).toArray(new Object[size]));
            }
        } else {
            Query[] chiQuerys2 = ((List<Map>) chiQuerys).stream().map(QueryUtils::fromJson).toArray(Query[]::new);
            Object andOperator = map.get("andOperator");
            boolean andOperator1 = andOperator != null && (boolean) andOperator;
            Object nested = map.get("nested");
            if (nested != null) {
                query = new Query((String) map.get("field"), andOperator1, chiQuerys2);
            } else {
                query = new Query(andOperator1, chiQuerys2);
            }

        }
        return query;
    }

    private static Query fromJson(String s) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(s)) {
            return null;
        }
        return fromJson(GSON.fromJson(s, Map.class));
    }

    public static String toJsonList(List<Query> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        int size = list.size();
        if (size > 1) {
            for (int i = 0, len = size - 1; i < len; i++) {
                sb.append(String.format("%s, ", list.get(i).toJson()));
            }
        }
        sb.append(list.get(size - 1).toJson()).append("]");
        return sb.toString();
    }

    public static List<Query> fromJsonList(String s) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(s)) {
            return null;
        }
        return ((List<Map>) GSON.fromJson(s, List.class)).stream().map(QueryUtils::fromJson).collect(Collectors.toList());
    }

    /*
     * @Description: 转换原始query列表
     * @Param: queries
     * @Param: field 转换query字段
     * @Param: func 转换方法
     * @Return: void
     **/
    public static void convert(@NonNull List<Query> queries, @NonNull String field, @NonNull Function<Query, Query> func) throws RuntimeException {
        Optional<Query> qry = queries.stream().filter(e -> field.equals(e.getField())).findFirst();
        if (qry.isPresent()) {
            Query query = qry.get();
            if (ArrayUtils.isEmpty(query.getValues())) {
                throw new RuntimeException("Query转换失败");
            }
            queries.remove(query);
            query = func.apply(query);
            if (query != null) {
                queries.add(query);
            } else {
                throw new RuntimeException("Query转换失败");
            }
        }
    }

    /*
     * @Description: 转换原始query列表
     * @Param: queries
     * @Param: field 转换query字段
     * @Param: func 转换方法
     * @Return: void
     **/
    public static void convertMulti(@NonNull List<Query> queries, @NonNull String field, @NonNull Function<List<Query>, List<Query>> func) throws RuntimeException {
        List<Query> queryList = queries.stream().filter(e -> field.equals(e.getField())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(queryList)) {
            queries.removeAll(queryList);
            queryList = func.apply(queryList);
            queries.addAll(queryList);
        }
    }

    /*
     * @Description: 转换原始query列表
     * @Param: queries
     * @Param: fields 转换query多字段
     * @Param: func 转换方法
     * @Param: reduceFunc 多字段对应值合并方法
     * @Return: void
     **/
    public static void convert(@NonNull List<Query> queries, @NonNull String[] fields, @NonNull Function<Query, Query> func, BinaryOperator<Object[]> reduceFunc) throws RuntimeException {
        List<Query> qrys = queries.stream().filter(e -> ArrayUtils.contains(fields, e.getField())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(qrys)) {
            if (fields.length == 1) {
                Query query = qrys.get(0);
                if (ArrayUtils.isEmpty(query.getValues())) {
                    throw new RuntimeException("Query转换失败");
                }
                queries.remove(query);
                query = func.apply(query);
                if (query != null) {
                    queries.add(query);
                } else {
                    throw new RuntimeException("Query转换失败");
                }
            } else if (reduceFunc != null) {
                Object[] values = qrys.stream().map(Query::getValues).reduce(reduceFunc).orElseThrow(() -> new RuntimeException("Query转换失败"));
                if (ArrayUtils.isEmpty(values)) {
                    throw new RuntimeException("Query转换失败");
                }
                queries.removeAll(qrys);
                Query query = func.apply(QueryUtils.In(fields[0], values));
                if (query != null) {
                    queries.add(query);
                } else {
                    throw new RuntimeException("Query转换失败");
                }
            } else {
                throw new RuntimeException("Query转换失败");
            }
        }
    }

    /**
     * @Description: 合并简单查询
     * @Param: queries
     * @Return: List<Query>
     * @Throws: 合并字段互斥，直接返回异常
     **/
    public static List<Query> merge(List<Query> queries) throws RuntimeException {
        List<Query> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queries)) {
            List<Query> nestList = queries.stream().filter(Query::isNested).collect(Collectors.toList());
            List<Query> chiQueryList = queries.stream().filter(i -> ArrayUtils.isNotEmpty(i.getChiQuerys()) && !i.isNested()).collect(Collectors.toList());
            Map<String, Map<QueryType, List<Query>>> group = queries.stream()
                    .filter(i -> !i.isNested() && ArrayUtils.isEmpty(i.getChiQuerys()))
                    .collect(Collectors.groupingBy(Query::getField, Collectors.groupingBy(Query::getQueryType)));
            group.forEach((field, v) -> {
                v.forEach(((queryType, queryList) -> {
                    Set<Object> values = queryList.stream().flatMap(i -> Arrays.stream(i.getValues())).collect(Collectors.toSet());
                    switch (queryType) {
                        case EQ:
                            if (values.size() > 1) {
                                throw new RuntimeException("Query EQ 条件互斥，检索结果为0");
                            }
                            list.add(new Query(queryType, field, CollectionUtils.isNotEmpty(values) ? values.stream().iterator().next() : null));
                            break;
                        case NE:
                            list.add(new Query(QueryType.NI, field, CollectionUtils.isNotEmpty(values) ? values.toArray() : null));
                            break;
                        case IN:
                            values = new HashSet<>(Arrays.asList(queryList.get(0).getValues()));
                            for (Query query : queryList) {
                                values.retainAll(Arrays.asList(query.getValues()));
                            }
                            if (CollectionUtils.isEmpty(values)) {
                                throw new RuntimeException("Query IN 条件互斥，检索结果为0");
                            }
                            list.add(new Query(queryType, field, CollectionUtils.isNotEmpty(values) ? values.toArray() : null));
                            break;
                        case NI:
                            list.add(new Query(queryType, field, CollectionUtils.isNotEmpty(values) ? values.toArray() : null));
                            break;
                        default:
                            list.addAll(queryList);
                            break;
                    }
                }));
            });
            list.addAll(mergeNestQuery(nestList));
            list.addAll(chiQueryList);
        }
        return list;
    }

    /*
     * @Description: 合并nest查询
     * @Param: queries
     * @Return: List<Query>
     **/
    public static List<Query> mergeNestQuery(List<Query> queries) {
        List<Query> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queries)) {
            Map<String, List<Query>> nest = new HashMap<>();
            for (Query item : queries) {
                if (!item.isNested()) {
                    list.add(item);
                } else {
                    List<Query> nestQry = nest.get(item.getField());
                    if (nestQry == null) {
                        nestQry = new ArrayList<>();
                    }
                    nestQry.add(item);
                    nest.put(item.getField(), nestQry);
                }
            }
            for (Map.Entry<String, List<Query>> item : nest.entrySet()) {
                if (item.getValue() == null) {
                    continue;
                }
                int nestSize = item.getValue().size();
                if (nestSize == 1) {
                    list.add(item.getValue().get(0));
                } else {
                    Query[] arr = new Query[nestSize];
                    for (int i = 0; i < nestSize; i++) {
                        arr[i] = item.getValue().get(i).getChiQuerys()[0];
                    }
                    list.add(new Query(item.getKey(), false, new Query(true, arr)));
                }
            }
        }
        return list;
    }
}
