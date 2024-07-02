package com.nx.elasticsearch.utils;

import com.alibaba.fastjson2.JSONObject;
import com.nx.elasticsearch.entity.Link;
import com.nx.elasticsearch.entity.Node;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.QueryType;
import com.nx.elasticsearch.entity.tuple.TwoTuple;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Li Hao
 * @create_time: 2020-01-15 14:23
 * <p>
 * 图数据库工具类
 * <p>
 * "~"开头为反向路径
 */
public class EsGraphicsUtils {

    public final static String UID = "uid";


    public static final String FIELD_ALL = "uid,expand(_all_)";
    /**
     * 线上
     **/
    //基础查询
    private static final String BASE_INFO_QUERY_URL = "http://api.nx.com/dgraph/get";
    //返回多层图
    private static final String RECURSIVE_QUERY_URL = "http://api.nx.com/dgraph/get/with/recurse";
    //返回两个节点间路径
    private static final String PATH_QUERY_URL = "http://api.nx.com/dgraph/get/shortest";
    //返回两个节点间经过指定终点路径
    private static final String SPECIAL_PATH_QUERY_URL = "http://api.nx.com/dgraph/get/shortest/midpoint";


    /**
     * @Description: 根据查询条件返回基本信息, 最大10条, 目前只支持EQ
     * @Param:
     * @return:
     * @Author: Li Hao
     * @Date: 2020/1/15
     */
    public static List<Map<String, Object>> queryList(List<Query> queries, Long count, String... fields) throws Exception {
        List<Map<String, Object>> result = null;
        if (CollectionUtils.isEmpty(queries)) {
            throw new RuntimeException("未传递查询条件");
        }
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("query", getQuery(queries.remove(0)) + ",first:" + getCount(count, 100L)));
        list.add(new BasicNameValuePair("show_fields", getShowFields(fields)));
        list.add(new BasicNameValuePair("query_extra", getQuery(queries)));
        try {
            JSONObject response = EsHttpUtils.post(BASE_INFO_QUERY_URL, list);
            if (EsMapUtils.isNotEmpty(response)) {
                result = (List) response.get("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> queryOne(List<Query> queries, String... fields) throws Exception {
        List<Map<String, Object>> list = queryList(queries, 1L, fields);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public static Map<String, Object> queryOne(String uid, String... fields) throws Exception {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, UID, uid));
        List<Map<String, Object>> list = queryList(queries, 1L, fields);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public static List<List<Map<String, Object>>> queryPaths(String startUID, String endUID, String midUID, List<String> findFields, Long pathNum, Long depth, String... fields) {
        List<List<Map<String, Object>>> result = null;
        if (StringUtils.isEmpty(startUID) || StringUtils.isEmpty(endUID) || CollectionUtils.isEmpty(findFields)) {
            throw new RuntimeException("查询条件不足");
        }
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("uid1", startUID));
        list.add(new BasicNameValuePair("uid2", endUID));
        list.add(new BasicNameValuePair("show_fields", getShowFields(fields)));
        list.add(new BasicNameValuePair("find_fields", findFields.stream().collect(Collectors.joining(","))));
        list.add(new BasicNameValuePair("numpaths", String.valueOf(getCount(pathNum, 5L))));
        list.add(new BasicNameValuePair("depth", String.valueOf(getCount(depth, 10L))));

        JSONObject response;
        try {
            if (StringUtils.isNotEmpty(midUID)) {
                list.add(new BasicNameValuePair("uid_midpoint", midUID));
                response = EsHttpUtils.post(SPECIAL_PATH_QUERY_URL, list);
            } else {
                response = EsHttpUtils.post(PATH_QUERY_URL, list);
            }
            if (EsMapUtils.isNotEmpty(response)) {
                result = (List) response.get("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return
     * @Author LeeHao
     * @Description fields需要加上关联的边
     * @Date 13:26 2020/2/13
     * @Param [uid, recurseDepth深度, fields检索及展示的边]
     **/
    public static Map<String, Object> queryRecurse(String uid, Long recurseDepth, String... fields) {
        List<Map<String, Object>> result = null;
        if (StringUtils.isEmpty(uid)) {
            throw new RuntimeException("未传递uid");
        }
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("query", "uid(" + uid + ")"));
        list.add(new BasicNameValuePair("show_fields", getShowFields(fields)));
        list.add(new BasicNameValuePair("recurse_depth", String.valueOf(getCount(recurseDepth, 10L))));

        try {
            JSONObject response = EsHttpUtils.post(RECURSIVE_QUERY_URL, list);
            if (EsMapUtils.isNotEmpty(response)) {
                result = (List) response.get("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(val.toString().trim());
        } catch (Exception e) {
            return 0D;
        }
    }


    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }


    public static boolean isValid(TwoTuple<Set<Node>, Set<Link>> graphics, String... exceptUids) {
        if (graphics != null) {
            Set<Node> nodeSet = graphics.first;
            Set<Link> linkSet = graphics.second;
            Map<String, Integer> degreeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(linkSet)) {
                linkSet.forEach(link -> {
                    EsMapUtils.putOrAdd(degreeMap, link.getTarget(), 1);
                    EsMapUtils.putOrAdd(degreeMap, link.getSource(), 1);
                });
            }
            for (Node node : nodeSet) {
                if (!ArrayUtils.contains(exceptUids, node.getName()) && toInteger(degreeMap.get(node.getName())) < 2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static String getQuery(Query query) throws Exception {
        String result = "";
        if (query != null) {
            String field = query.getField();
            Object[] values = query.getValues();
            if (ArrayUtils.isEmpty(values)) {
                throw new RuntimeException("检索值不能为空");
            }
            if (field.equalsIgnoreCase(UID)) {
                result = "uid(" + Arrays.stream(values).map(i -> getValue(i)).distinct().collect(Collectors.joining(",")) + ")";
            } else {
                result = "eq(" + field + ",<" + Arrays.stream(values).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
            }
        }
        return result;
    }

    public static String getValue(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    private static String getQuery(List<Query> queries) {
        String result = "";
        if (CollectionUtils.isNotEmpty(queries)) {
            List<String> queryStrList = new ArrayList<>();
            String queryStr;
            for (Query query : queries) {
                if (ArrayUtils.isNotEmpty(query.getValues())) {
                    switch (query.getQueryType()) {
                        case LT:
                            queryStr = "lt(" + query.getField() + ",<" + Arrays.stream(query.getValues()).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
                            break;
                        case LE:
                            queryStr = "le(" + query.getField() + ",<" + Arrays.stream(query.getValues()).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
                            break;
                        case GT:
                            queryStr = "gt(" + query.getField() + ",<" + Arrays.stream(query.getValues()).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
                            break;
                        case GE:
                            queryStr = "ge(" + query.getField() + ",<" + Arrays.stream(query.getValues()).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
                            break;
                        case EQ:
                        case IN:
                        default:
                            queryStr = "eq(" + query.getField() + ",<" + Arrays.stream(query.getValues()).map(i -> getValue(i)).distinct().collect(Collectors.joining(">,<")) + ">)";
                            break;
                    }
                    queryStrList.add(queryStr);
                }
            }
            result = queryStrList.stream().collect(Collectors.joining(" and "));
        }
        return result;
    }

    private static String getShowFields(String... fields) {
        String result;
        if (ArrayUtils.isEmpty(fields)) {
            result = FIELD_ALL;
        } else {
            if (ArrayUtils.contains(fields, UID)) {
                result = Arrays.stream(fields).distinct().collect(Collectors.joining(","));
            } else {
                result = Arrays.stream(fields).distinct().collect(Collectors.joining(",")) + ",uid";
            }
        }
        return result;
    }

    private static long getCount(Long count, Long maxCount) {
        if (count == null || count == 0 || count > maxCount) {
            count = maxCount;
        }
        return count;
    }

    public static void main(String[] args) {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, "base_company.all_name", "科望生物"));

        try {
            List<Map<String, Object>> result = queryList(queries, 100L, UID, "base_company.id", "~base_company.parent_company_group @filter(eq(base_company.is_invest, \"是\")){base_company.id,base_company.name}");
            result.forEach(i -> {
                i.entrySet().forEach(j -> {
                    System.out.println(j.getKey() + ":" + j.getValue());
                });
            });
//            Map<String, Object> result = queryRecurse("0x3cbce4", 3L, "invest_capital.base_company", "~invest_capital.base_company", "base_company.parent_company_group", "invest_capital.name_short");
//            List<String> findFields = new ArrayList<String>() {{
//                add("~business_info.credit_id");
//                add("business_info.credit_id");
//                add("invest_china_fund.capital_id");
//                add("~invest_china_fund.capital_id");
//                add("business_info.financing_record.bind_organization");
//                add("~business_info.financing_record.bind_organization");
//                add("business_info.shareholder_info.bind_organization");
//                add("~business_info.shareholder_info.bind_organization");
//            }};
//            List<List<Map<String, Object>>> result = queryPaths("0x1733af", "0x169768", null, findFields, 5L, 2L, null);
//            result.forEach(i -> {
//                i.forEach(j -> {
//                    j.entrySet().forEach(k -> {
//                        System.out.println(k.getKey() + ":" + k.getValue());
//                    });
//                });
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
