package com.nx.elasticsearch.service.impl;

import com.github.sd4324530.jtuple.Tuple;
import com.github.sd4324530.jtuple.Tuple2;
import com.github.sd4324530.jtuple.Tuples;
import com.nx.elasticsearch.constant.ESConstants;
import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.entity.tree.TreeCube;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.QueryType;
import com.nx.elasticsearch.query.impl.*;
import com.nx.elasticsearch.service.ESService;
import com.nx.elasticsearch.utils.EsDateInterval;
import com.nx.elasticsearch.utils.EsUtils;
import com.nx.hbase.service.HbaseService;
import com.nx.hbase.service.impl.HbaseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.Percentiles;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.nx.elasticsearch.constant.AlgorithmType.MAX;

@Service
public class ESServiceImpl implements ESService {

    private static final Logger LOG = LoggerFactory.getLogger(ESServiceImpl.class);
    private static final String STR_NULL = "null";
    //时间输出格式
    private static final String DATE_PATTERN1 = "yyyy-MM-dd";
    private static final String DATE_PATTERN2 = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN3 = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
    private static final String FIELD_PUBLISHDATE = "publishDate";
    private static final String FIELD_INVALIDDATE = "invalidDate";
    private static final String FIELD_UNITPRICE = "unitPrice";
    private static final String FIELD_ES_MODIFY_TIME = "es_modify_time";
    private static final String FIELD_MODIFY_TIME = "modify_time";
    private static final String[] FIELDS_GETPRICEMEDIAN = new String[]{FIELD_PUBLISHDATE};
    private static final String[] treeGetAuditOperateHistoryFields = {FIELD_ID, FIELD_OPERATE_NAME, FIELD_CONTENT, FIELD_CONTENT_PRE};
    private static final int SCROLL_SIZE = ESConstants.SCROLL_SIZE;
    private static final Map<String, List<String>> indexMap = new HashMap<>();

    //统一添加 修改時间的索引(适用于增量更新时使用)
    private static final HashSet<String> HS_ADD_MODIFYTIME = new HashSet<String>() {{
        add("discover_indication");
        add("discover_target");
        add("lin_ingredient_company_legality");
        add("lin_ingredient_similarity");
        add("lin_ingredient_all_name");
        add("base_company_data");
        add("lin_company_similarity");
        add("base_atc_data");
    }};

    //统一添加 修改時间的索引(适用于增量更新时使用)
    private static final HashSet<String> HS_ADD_MODIFYTIME2 = new HashSet<String>() {{
        add("drug_associated_accepted_nos_inn");
        add("drug_ipo");
        add("drug_earth");
        add("drug_register");
        add("invest_capital");
        add("invest_china_fund");
    }};

    public static HbaseService hbaseService = new HbaseServiceImpl();
    private static Map<Integer, Integer> MAP_QUARTER_END = new HashMap<Integer, Integer>() {{
        put(1, 3);
        put(2, 3);
        put(3, 3);
        put(4, 6);
        put(5, 6);
        put(6, 6);
        put(7, 9);
        put(8, 9);
        put(9, 9);
        put(10, 12);
        put(11, 12);
        put(12, 12);
    }};

    private static LinkedHashMap<String, Boolean> MAP_ORDER_ESID = new LinkedHashMap<String, Boolean>() {{
        put(ESID_FIELD, true);
    }};

    //特殊字符
    private final String REGEX_CHARS = "[ \\n\\r\\t!\"#$%&*\\\\'()\\[\\]+,-./:;<=>?@^_`{|}~，。：、…【】《》（）“”‘’]";
    private final Pattern p = Pattern.compile(REGEX_CHARS);
    private final Pattern p_en = Pattern.compile("[a-zA-z]");
    private final String ES_ID_FIELD = ESConstants.ES_ID_FIELD;
    private final Set<String> FIELDS_ESID = new HashSet<String>() {{
        add(ESID_FIELD);
        add(ES_ID_FIELD);
    }};
    private final String ALIAS_SUFFIX = ESConstants.ALIAS_SUFFIX;
    private final String INDEX_AUDIT_TREE_ALIAS = INDEX_AUDIT_TREE + ALIAS_SUFFIX;

    private static Map<String, Object> getPriceMedianResult(Aggregations aggregations) {
        Map<String, Object> map = getHashMap(aggregations.asList().size());
        for (Aggregation aggregationFilter : aggregations) {
            List<Aggregation> aggregations1 = ((Filter) aggregationFilter).getAggregations().asList();
            if (CollectionUtils.isNotEmpty(aggregations1)) {
                map.put(aggregationFilter.getName(), ((Percentiles) ((aggregations1.get(0)))).percentile(50));
            }
        }
        return map.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private static Map<String, Map<String, Object>> getAggregateGroupAndDateRangeResult(Aggregations aggregations) {
        Map<String, Map<String, Object>> maps = getHashMap(aggregations.asList().size());
        for (Aggregation aggregationFilter : aggregations) {
            Map<String, Object> map = getLinkedHashMap(DEFAULT_AREA.length);
            setDefaultArea(map, DEFAULT_AREA);
            List<Aggregation> aggregations1 = ((Filter) aggregationFilter).getAggregations().asList();
            if (CollectionUtils.isNotEmpty(aggregations1)) {
                List<? extends Terms.Bucket> bucketsProvince = ((Terms) aggregations1.get(0)).getBuckets();
                if (CollectionUtils.isNotEmpty(bucketsProvince)) {
                    for (Terms.Bucket province : bucketsProvince) {
                        List<Aggregation> aggsPublishDate = province.getAggregations().asList();
                        if (CollectionUtils.isNotEmpty(aggsPublishDate)) {
                            //先获取最大publishDate,再获取最小unitPrice
                            Aggregation aggregation = null;
                            List<? extends Terms.Bucket> aggsPublishDate2 = ((Terms) aggsPublishDate.get(0)).getBuckets();
                            try {
                                Tuple2<Long, ? extends Terms.Bucket> tuple3;
                                if (aggsPublishDate2.size() > 1) {
                                    tuple3 = aggsPublishDate2.stream().map(o -> Tuples.tuple((Long) o.getKey(), o)).max(Comparator.comparing(o -> o.first)).get();
                                } else {
                                    tuple3 = Tuples.tuple(null, aggsPublishDate2.get(0));
                                }
                                //这里要获取最小unitPrice因此用NumericMetricsAggregation.SingleValue
                                aggregation = tuple3.second.getAggregations().asList().stream().min(Comparator.comparingDouble(o -> ((NumericMetricsAggregation.SingleValue) o).value())).get();
                            } catch (Exception e) {
                            }
                            Object objResult = null;
                            //获取聚合结果
                            if (aggregation instanceof NumericMetricsAggregation.SingleValue) {//这里获取的是标准的聚合函数:avg,max,min,sum,count
                                NumericMetricsAggregation.SingleValue aggTerm = (NumericMetricsAggregation.SingleValue) aggregation;
                                objResult = aggTerm.value();
                            }
                            //key:省份名称
                            map.put(province.getKeyAsString(), objResult);
                        }
                    }
                }
            }
            //key:年月份
            maps.put(aggregationFilter.getName(), map);
        }
        return maps;
    }

    private static Map<String, Map<String, Object>> getAggregateGroupAndDateRangeResult2(Aggregations aggregations) {
        Map<String, Map<String, Object>> maps = getHashMap(aggregations.asList().size());
        for (Aggregation aggregationFilter : aggregations) {
            Map<String, Object> map = getLinkedHashMap(DEFAULT_AREA.length);
            List<Aggregation> aggregations1 = ((Filter) aggregationFilter).getAggregations().asList();
            if (CollectionUtils.isNotEmpty(aggregations1)) {
                aggregations1.forEach(aggregation -> Optional.ofNullable(aggregation instanceof NumericMetricsAggregation.SingleValue ? ((NumericMetricsAggregation.SingleValue) aggregation).value() : aggregation instanceof Percentiles ? ((Percentiles) aggregation).percentile(50) : null).ifPresent(v -> map.put(aggregation.getName(), v)));
            }
            maps.put(aggregationFilter.getName(), map);
        }
        return maps;
    }

    private static List<Map<String, Object>> sub(String index, List<Map<String, Object>> list, boolean hbase) {
        if (indexMap.containsKey(index)) {
            List<Map<String, Object>> resultlist = new ArrayList<>();
            List<String> fields = indexMap.get(index);
            for (Map<String, Object> m : list) {
                Map<String, Object> tmp = new HashMap<>();
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    if ((hbase && fields.contains(entry.getKey())) || (!hbase && !fields.contains(entry.getKey()))) {
                        tmp.put(entry.getKey(), entry.getValue());
                    }
                }
                resultlist.add(tmp);
            }
            return resultlist;
        } else {
            return null;
        }
    }

    private static Map<String, Object> sub(String index, Map<String, Object> map, boolean hbase) {
        if (indexMap.containsKey(index)) {
            List<String> fields = indexMap.get(index);
            Map<String, Object> tmp = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if ((hbase && fields.contains(entry.getKey())) || (!hbase && !fields.contains(entry.getKey()))) {
                    tmp.put(entry.getKey(), entry.getValue());
                }
            }
            return tmp;
        } else {
            return null;
        }
    }

    private static void setDefaultArea(Map<String, Object> map, String[] DefaultArea) {
        for (String key : DefaultArea) {
            map.put(key, null);
        }
    }

    private static <K, V> LinkedHashMap<K, V> getLinkedHashMap(Integer length) {
        if (length == null || length < 1) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>((int) Math.ceil(length / .75f));
    }

    private static <K, V> Map<K, V> getHashMap(Integer length) {
        if (length == null) {
            return new HashMap<>();
        }
        if (length < 1) {
            return Collections.emptyMap();
        }
        return new HashMap<>((int) Math.ceil(length / .75f));
    }

    private static <E> Set<E> getHashSet(Integer length) {
        if (length == null) {
            return new HashSet<>();
        }
        if (length < 1) {
            return Collections.emptySet();
        }
        return new HashSet<>((int) Math.ceil(length / .75f));
    }

    static int getPageTotal(long count, int size) {
        return (int) Math.ceil(count * 1.0 / size);
    }

    /**
     * str=>Date
     *
     * @param s
     * @param pattern
     * @return
     */
    private static Date getDateFromStr(String s, String pattern) {
        try {
            return DateUtils.parseDate(s.replace("Z", " UTC"), pattern);
        } catch (ParseException e) {
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    /**
     * @author: 王坤造
     * @date: 2018/8/7 10:39
     * @comment: 時间戳转化为本地時间对象(LocalDateTime)
     * @return:
     * @notes: 默认使用亚洲/上海時区
     */
    private static LocalDateTime getLocalDateTime(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    /**
     * @author: 王坤造
     * @date: 2018/8/7 10:39
     * @comment: 获取指定年月第1天的本地時间对象(LocalDateTime)
     * @return:
     * @notes: 默认使用亚洲/上海時区,month从1开始
     */
    private static LocalDateTime getLocalDateTime(int year, int month) {
        return getLocalDateTime(year, month, 1);
    }

    private static LocalDateTime getLocalDateTime(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    /**
     * @author: 王坤造
     * @date: 2018/8/7 11:06
     * @comment: 本地時间对象(LocalDateTime)转化为時间戳
     * @return:
     * @notes: 默认使用东8区(跟LocalDateTime使用時区要一致)
     */
    private static long getTimestamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * Date转化为String
     *
     * @param d
     * @param pattern
     * @return
     */
    private static String getString(Date d, String pattern) {
        return DateFormatUtils.format(d, pattern);
    }

    private static void logInfo(Object... objs) {
        LOG.info(StringUtils.join(objs, '|'));
    }

    private static void getESIDList(String esid, TreeCube tc1, TreeCube tc2, Set<String> esidsDeleteAdd,
                                    Set<String> esidsUpdateRelationship, Set<String> esidsUpdateData, String... topicFields) {
        TreeCube tc = tc2.getCurrentOrChildren(esid);
        //删除的esid
        if (esidsUpdateRelationship == null) {
            if (tc == null) {
                esidsDeleteAdd.add(esid);
            }
        } else {
            //新对象的子类esid
            List<String> childrenESIDsNew = tc1.getChildrenESIDs();
            //新增的esid
            if (tc == null) {
                esidsDeleteAdd.add(esid);
                //添加新增的子类esid
                if (CollectionUtils.isNotEmpty(childrenESIDsNew)) {
                    esidsUpdateRelationship.addAll(childrenESIDsNew);
                }
            } else {
                //判断data中字段是否有修改
                if (Arrays.stream(topicFields).anyMatch(o -> !Objects.equals(tc1.getData().get(o), tc.getData().get(o)))) {
                    esidsUpdateData.add(esid);
                }
                //旧对象的子类esid
                List<String> childrenESIDs = tc.getChildrenESIDs();
                //判断新旧的子类esid是否不一致
                if (CollectionUtils.isEmpty(childrenESIDsNew)) {
                    if (CollectionUtils.isNotEmpty(childrenESIDs)) {
                        esidsUpdateRelationship.add(esid);
                    }
                } else {
                    if (CollectionUtils.isEmpty(childrenESIDs)) {
                        esidsUpdateRelationship.addAll(childrenESIDsNew);
                        esidsUpdateRelationship.add(esid);
                    } else {
                        //伪异或
                        Collection<String> disjunction = CollectionUtils.disjunction(childrenESIDsNew, childrenESIDs);
                        if (CollectionUtils.isNotEmpty(disjunction)) {
                            esidsUpdateRelationship.addAll(disjunction);
                            esidsUpdateRelationship.add(esid);
                        }
                    }
                }
            }
        }
    }

    private static void getESIDList(String esid, TreeCube tc1, TreeCube tc2, Set<String> esidsDeleteAdd,
                                    Set<String> esidsUpdateRelationship, Set<String> esidsUpdateData, Map<String, TreeCube> map,
                                    Map<String, String> mapParentESID, String... topicFields) {
        Stack<Tuple> stack = new Stack<>();
        stack.push(Tuples.tuple(1, esid, tc1));
        int type;
        Tuple tuple;
        int index;
        List<TreeCube> childrenTreeCubes;
        TreeCube childrenTreeCube;
        while (!stack.isEmpty()) {
            tuple = stack.pop();
            type = tuple.get(0);
            if (type == 1) {
                esid = tuple.get(1);
                tc1 = tuple.get(2);
                getESIDList(esid, tc1, tc2, esidsDeleteAdd, esidsUpdateRelationship, esidsUpdateData, topicFields);
                if (map != null) {
                    map.put(esid, tc1);
                }
                childrenTreeCubes = tc1.getChildrenTreeCubes();
                if (CollectionUtils.isNotEmpty(childrenTreeCubes)) {
                    if (mapParentESID == null) {
                        stack.push(Tuples.tuple(2, childrenTreeCubes, 0));
                    } else {
                        stack.push(Tuples.tuple(3, childrenTreeCubes, 0, esid));
                    }
                }
            } else if (type == 2) {
                childrenTreeCubes = tuple.get(1);
                index = tuple.get(2);
                if (childrenTreeCubes.size() - index > 1) {
                    stack.push(Tuples.tuple(type, childrenTreeCubes, index + 1));
                }
                childrenTreeCube = childrenTreeCubes.get(index);
                stack.push(Tuples.tuple(1, childrenTreeCube.getESID(), childrenTreeCube));
            } else if (type == 3) {
                childrenTreeCubes = tuple.get(1);
                index = tuple.get(2);
                esid = tuple.get(3);
                if (childrenTreeCubes.size() - index > 1) {
                    stack.push(Tuples.tuple(type, childrenTreeCubes, index + 1, esid));
                }
                childrenTreeCube = childrenTreeCubes.get(index);
                String esidChi = childrenTreeCube.getESID();
                mapParentESID.put(esidChi, esid);
                stack.push(Tuples.tuple(1, esidChi, childrenTreeCube));
            }
        }
    }

    @Override
    public Page<Map<String, Object>> getPage(String index, List<Query> queries, Page page, String... showFields) {
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        int limit = page.getLimit().intValue();
        Integer currentPage = page.getCurrentPage();
        getSorts(page.getOrderColumns(), page.getOrderDirs(), searchSourceBuilder);
        boolean isScroll = EsUtils.setPage(searchSourceBuilder, currentPage, limit);
        //searchSourceBuilder.query(QueryBuilders.boolQuery().should(QueryBuilders.matchAllQuery()).must(QueryBuilders.existsQuery("field1")).filter(QueryBuilders.idsQuery().addIds("1")).mustNot(QueryBuilders.termQuery("field2","haha")));
        //searchSourceBuilder.query(QueryBuilders.boolQuery().should(QueryBuilders.matchAllQuery()));
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        EsUtils.setFetchSource(searchSourceBuilder, showFields);
        //searchSourceBuilder.explain(true);
        try {
            Tuple2<List<Map<String, Object>>, Long> tuple = EsUtils.operSearch2(index, null, searchSourceBuilder);
            page.setItemList(tuple.first);
            page.setTotalCount(tuple.second);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s", "getPage", index, queries, page, Arrays.toString(showFields)), e);
            EsUtils.throwRuntimeException(e);
        }
        return page;
    }

    @Override
    public Page<Map<String, Object>> getPage(String index, List<Query> queries, int size, LinkedHashMap<String, Boolean> sorts, Object[] after, String... showFields) {
        EsUtils.judgeIterNotNullAndKNotNull("orders", sorts);
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        if (ArrayUtils.isNotEmpty(after)) {
            searchSourceBuilder.searchAfter(after);
        }
        EsUtils.setPage(searchSourceBuilder, 1, size);
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        getSorts(sorts, searchSourceBuilder);
        EsUtils.setFetchSource(searchSourceBuilder, showFields);
        Page<Map<String, Object>> page = new Page<>();
        try {
            Tuple2<List<Map<String, Object>>, Long> tuple = EsUtils.operSearch2(index, null, searchSourceBuilder);
            page.setItemList(tuple.first);
            page.setTotalCount(tuple.second);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getPage", index, queries, size, sorts, Arrays.toString(after), Arrays.toString(showFields)), e);
            EsUtils.throwRuntimeException(e);
        }
        return page;
    }

    @Override
    public Page<Map<String, Object>> getPageCollapse(String index, String collapse, List<Query> queries, int size, LinkedHashMap<String, Boolean> sorts, Object[] after, String... showFields) {
        EsUtils.judgeIterNotNullAndKNotNull("orders", sorts);
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        if (ArrayUtils.isNotEmpty(after)) {
            searchSourceBuilder.searchAfter(after);
        }
        ESUtils.setPage(searchSourceBuilder, 1, size);
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        getSorts(sorts, searchSourceBuilder);
        ESUtils.setFetchSource(searchSourceBuilder, showFields);
        searchSourceBuilder.collapse(new CollapseBuilder(collapse));
        Page<Map<String, Object>> page = new Page<>();
        try {
            Tuple2<List<Map<String, Object>>, Long> tuple = ESUtils.operSearch2(index, null, searchSourceBuilder);
            page.setItemList(tuple.first);
            page.setTotalCount(tuple.second);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getPage", index, queries, size, sorts, Arrays.toString(after), Arrays.toString(showFields)), e);
            EsUtils.throwRuntimeException(e);
        }
        return page;
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(不分页)
     * 备注： 请谨慎使用参数size=-1,会直接触发游标查询,尤其体现于1G以上的索引可能会导致Elasticsearch集群崩溃,建议大于1G的索引size=-1的情况请使用getListSub方法
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * orderColumns:排序字段【为null,则不排序】
     * orderDirs:排序方式【为null或者长度小于排序字段,则默认为升序】
     * count:要获取总条数
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public List<Map<String, Object>> getListCollapse(String index, String collapse, List<Query> queries, String[] orderColumns, String[] orderDirs, long size, String... showFields) {
        if (size == 0) {
            return null;
        }
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        getSorts(orderColumns, orderDirs, searchSourceBuilder);
        EsUtils.setFetchSource(searchSourceBuilder, showFields);
        searchSourceBuilder.collapse(new CollapseBuilder(collapse));
        try {
            return EsUtils.operSearchScroll(index, null, searchSourceBuilder, size, -1);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getList", index, queries, Arrays.toString(orderColumns), Arrays.toString(orderDirs), size, Arrays.toString(showFields)), e);
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    /**
     * 请谨慎使用参数size=-1,会直接触发游标查询,尤其体现于1G以上的索引可能会导致Elasticsearch集群崩溃,建议大于1G的索引size=-1的情况请使用getListSub方法
     * @param index
     * @param queries
     * @param orderColumns
     * @param orderDirs
     * @param size
     * @param showFields
     * @return
     */
    @Override
    public List<Map<String, Object>> getList(String index, List<Query> queries, String[] orderColumns, String[] orderDirs, long size, String... showFields) {
        if (size == 0) {
            return null;
        }
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        getSorts(orderColumns, orderDirs, searchSourceBuilder);
        EsUtils.setFetchSource(searchSourceBuilder, showFields);
        try {
            return EsUtils.operSearchScroll(index, null, searchSourceBuilder, size, -1);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getList", index, queries, Arrays.toString(orderColumns), Arrays.toString(orderDirs), size, Arrays.toString(showFields)), e);
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    /**
     * 作者: 王坤造
     * 日期: 2017/1/20 16:34
     * 名称：根据esid查询一条
     * 备注：
     * index:索引名称
     * esid:esid
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public Map<String, Object> getOne(String index, Object esid, String... showFields) {
        if (esid == null || (esid instanceof String && StringUtils.isEmpty((String) esid))) {
            return null;
        }
        return getOne(index, Collections.singletonList(new Query(QueryType.EQ, ESID_FIELD, esid)), showFields);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-23 下午6:24
     * 名称：根据查询条件查询一条(只显示第一条,底下的忽略)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    @Override
    public Map<String, Object> getOne(String index, List<Query> queries, String... showFields) {
        List<Map<String, Object>> list = getList(index, queries, null, null, 1, showFields);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：根据查询条件获取总条数
     * 备注：
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回0,list为null则查询全部数据条数】
     */
    @Override
    public long getCount(String index, List<Query> queries) {
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        searchSourceBuilder.size(0);
        try {
            return EsUtils.operSearchCount(index, null, searchSourceBuilder);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s", "getCount", index, queries), e);
            EsUtils.throwRuntimeException(e);
        }
        return 0;
    }

    /**
     * @author: 王坤造
     * @date: 2017/8/17 11:33
     * @comment: 对分组後字段进行聚合操作(有加时间范围)
     * @return:
     * @notes: beginTimeField:开始时间字段
     * endTimeField:结束时间字段
     * queries:可以包含beginTimeField或者endTimeField字段,没有这2个字段,会从索引中搜索最小时间和最大时间
     * aggBuilder:分组後的聚合函数有且仅有一个
     * showType:显示的类型:0年份+月份,1年份,2月份
     */
    @Override
    public Map<String, Map<String, Object>> getAggregateGroupAndDateRange(String index, List<Query> queries, AggBuilderService agg, String beginTimeField, String endTimeField, int showType) {
        return (Map<String, Map<String, Object>>) getImportBidding(index, queries, agg, beginTimeField, endTimeField, showType, ESServiceImpl::getAggregateGroupAndDateRangeResult);
    }

    @Override
    public Map<String, Map<String, Object>> getAggregateGroupAndDateRange2(String index, List<Query> queries, List<AggAggregate> agg, String beginTimeField, String endTimeField, int showType) {
        return (Map<String, Map<String, Object>>) getImportBidding2(index, queries, agg, beginTimeField, endTimeField, showType, ESServiceImpl::getAggregateGroupAndDateRangeResult2);
    }

    @Override
    public Map<String, Object> getPriceMedian(String index, List<Query> queries, String beginTimeField, String endTimeField, int showType) {
        if (CollectionUtils.isEmpty(queries)) {
            EsUtils.throwRuntimeException("queries禁止为空!");
        }
        if (showType != 4) {
            EsUtils.throwRuntimeException("showType错误!");
        }
        if (queries.stream().map(Query::getField).filter(o -> o.equals(beginTimeField) || o.equals(endTimeField)).count() != 2) {
            EsUtils.throwRuntimeException(String.format("queries中必须要有%s和%s的時间过滤条件!", beginTimeField, endTimeField));
        }

        List<Map<String, Object>> itemList = getList(index, queries, FIELDS_GETPRICEMEDIAN, null, -1, FIELD_PUBLISHDATE, FIELD_INVALIDDATE, FIELD_UNITPRICE);

        if (CollectionUtils.isEmpty(itemList)) {
            return null;
        }
        int i = itemList.size() / 2;
        if (i < 1) {
            i = 1;
        }
        Map<String, Date> dateMap = getHashMap(i);
        BiConsumer<Map<String, Object>, String> consumer = (map, s) -> {
            String publishDate = (String) map.get(s);
            Date d = dateMap.get(publishDate);
            if (d == null) {
                d = getDateFromStr(publishDate, DATE_PATTERN3);
                dateMap.put(publishDate, d);
            }
            map.put(s, d);
        };
        itemList.forEach(o -> {
            consumer.accept(o, FIELD_PUBLISHDATE);
            consumer.accept(o, FIELD_INVALIDDATE);
        });
        List<Date> publishDate = itemList.stream().map(o -> (Date) o.get(FIELD_PUBLISHDATE)).distinct().sorted().collect(Collectors.toList());
        Map<String, Object> map = getLinkedHashMap(publishDate.size());
        for (Date d : publishDate) {
            List<Double> ls = itemList.stream().filter(o -> ((Date) o.get(FIELD_PUBLISHDATE)).compareTo(d) < 1 && ((Date) o.get(FIELD_INVALIDDATE)).compareTo(d) > -1).map(o -> (Double) o.get(FIELD_UNITPRICE)).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            int size = ls.size();
            if (size > 0) {
                map.put(getString(d, DATE_PATTERN1), size == 1 ? ls.get(0) : (size == 2 ? (ls.get(0) + ls.get(1)) / 2 : ((size & 1) > 0 ? ls.get(size / 2) : (ls.get(size / 2 - 1) + ls.get(size / 2)) / 2)));
            }
        }
        return map;
    }

    private Object getImportBidding(String index, List<Query> queries, AggBuilderService agg, String beginTimeField, String endTimeField, int showType, Function<Aggregations, Object> func) {
        index = getNewIndex(index);
        Tuple2<Long, Long> pair = getTimes(index, queries, beginTimeField, endTimeField);
        if (pair == null) {
            return null;
        }
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        AbstractAggregationBuilder aggregationBuilder = getAbstractAggregationBuilder(-1, agg);
        getFilterAgg2(pair, beginTimeField, endTimeField, aggregationBuilder, showType, searchSourceBuilder);
        try {
            SearchResponse searchResponse = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            return func.apply(searchResponse.getAggregations());
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getImportBidding", index, queries, agg, beginTimeField, endTimeField, showType), e);
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    private Object getImportBidding2(String index, List<Query> queries, List<AggAggregate> agg, String beginTimeField, String endTimeField, int showType, Function<Aggregations, Object> func) {
        index = getNewIndex(index);
        Tuple2<Long, Long> pair = getTimes(index, queries, beginTimeField, endTimeField);
        if (pair == null) {
            return null;
        }
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        List<AbstractAggregationBuilder> aggregationBuilder = agg.stream().map(o -> getAbstractAggregationBuilder(-1, o)).collect(Collectors.toList());
        getFilterAgg22(pair, beginTimeField, endTimeField, aggregationBuilder, showType, searchSourceBuilder);
        try {
            SearchResponse searchResponse = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            return func.apply(searchResponse.getAggregations());
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s|%s|%s", "getImportBidding", index, queries, agg, beginTimeField, endTimeField, showType), e);
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/29 15:13
     * @comment:
     * @return:
     * @notes: 聚合:只能计算1个聚合函数
     * 分组:单字段/多字段最多获取10000条
     */
    @Override
    public Object getAggregations(String index, List<Query> queries, int total, AggBuilderService... aggBuilders) {
        if (total == 0 || ArrayUtils.isEmpty(aggBuilders)) {
            return null;
        }
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        for (AggBuilderService aggBuilder : aggBuilders) {
            searchSourceBuilder.aggregation(getAbstractAggregationBuilder(total, aggBuilder));
        }
        try {
            SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            List<Aggregation> aggregations = response.getAggregations().asList();
            if (aggregations.isEmpty()) {
                return null;
            }
            MulFieldsGroupAgg mulFieldsGroupAgg = new MulFieldsGroupAgg();
            mulFieldsGroupAgg.total = total;
            mulFieldsGroupAgg.skip = 0;
            Object o = getAggregationsValue(mulFieldsGroupAgg, new ArrayList<>(aggregations), null, true, null, aggBuilders);
            if (o instanceof List) {
                List o1 = (List) o;
                if (CollectionUtils.isNotEmpty(o1) && o1.size() > 10000) {
                    logWarn("分组个数超过10000!!!", index, queries, total, aggBuilders, o1.size());
                }
            }
            return o;
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s", "getAggregations", index, queries, total, Arrays.toString(aggBuilders)), e);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    @Override
    public Object getAggregationsTmp(String index, List<Query> queries, int total, AggBuilderService... aggBuilders) {
        return getAggregations(index, queries, total, aggBuilders);
    }

    @Override
    public Object getAggregations(String index, List<Query> queries, int total, AggGroup aggGroup) {
        if (total == 0 || aggGroup == null) {
            return null;
        }
        index = getNewIndex(index);
        if (total == -1) {
            //多字段分组字段
            AggGroup[] aggGroups = aggGroup.getAggGroups();
            //聚合函数/其它
            List<AggBuilderService> aggs = aggGroup.getAggBuilders();
            int size = 1000;
            if (ArrayUtils.isEmpty(aggGroups)) {
                String f = aggGroup.getField();
                AggGroup.Order order = aggGroup.getOrder();
                //单字段分组
                if (CollectionUtils.isEmpty(aggs)) {
                    List<Object> lsTotal = new ArrayList<>();
                    Object after = null;
                    boolean asc = order == null || order.isAsc();
                    while (true) {
                        List<Object> ls = getAggregationsGroup(index, queries, size, after, f, asc);
                        lsTotal.addAll(ls);
                        if (ls.size() < size) {
                            break;
                        }
                        after = ls.get(size - 1);
                    }
                    return lsTotal;
                }
                //单字段分组聚合
                boolean asc = order == null || order.getTypeValue() != 2 || order.isAsc();
                LinkedHashMap<String, Boolean> m = new LinkedHashMap<>();
                m.put(f, asc);
                Map<String, Object> after = null;
                AggAggregate[] aggAggregates = aggs.stream().map(o -> (AggAggregate) o).toArray(AggAggregate[]::new);
                List<Map<String, Object>> lsTotal = new ArrayList<>();
                while (true) {
                    List<Map<String, Object>> ls = getAggregationsGroupsAgg(index, queries, size, after, m, aggAggregates);
                    lsTotal.addAll(ls);
                    if (ls.size() < size) {
                        break;
                    }
                    after = ls.get(size - 1);
                }
                if (CollectionUtils.isNotEmpty(lsTotal) && order != null && order.getTypeValue() == 3) {
                    String name = order.getName();
                    boolean asc1 = order.isAsc();
                    ToDoubleFunction<Map<String, Object>> keyExtractor = asc1 ? o -> o.get(name) == null ? Double.MAX_VALUE : (double) o.get(name) : o -> o.get(name) == null ? Double.MIN_VALUE : -(double) o.get(name);
                    lsTotal.sort(Comparator.comparingDouble(keyExtractor));
                }
                return lsTotal;
            }
            int length = aggGroups.length;
            //设置多字段排序顺序:有排序大小>有排序>有大小>都没有
            List<AggGroup> aggGroupsSortSize = new ArrayList<>(length);
            List<AggGroup> aggGroupsSort = new ArrayList<>(length);
            List<AggGroup> aggGroupsSize = new ArrayList<>(length);
            List<AggGroup> aggGroupsNo = new ArrayList<>(length);
            for (AggGroup field : aggGroups) {
                if (field.getOrder() == null) {
                    if (field.getSize() > 0) {
                        aggGroupsSize.add(field);
                    } else {
                        aggGroupsNo.add(field);
                    }
                } else {
                    if (field.getSize() > 0) {
                        aggGroupsSortSize.add(field);
                    } else {
                        aggGroupsSort.add(field);
                    }
                }
            }
            LinkedHashMap<String, Boolean> m = new LinkedHashMap<>();
            for (List<AggGroup> groups : Arrays.asList(aggGroupsSortSize, aggGroupsSort, aggGroupsSize, aggGroupsNo)) {
                for (AggGroup group : groups) {
                    m.put(group.getField(), true);
                }
            }
            Map<String, Object> after = null;
            AggAggregate[] aggAggregates = CollectionUtils.isEmpty(aggs) ? null : aggs.stream().map(o -> (AggAggregate) o).toArray(AggAggregate[]::new);
            List<Map<String, Object>> lsTotal = new ArrayList<>();
            while (true) {
                List<Map<String, Object>> ls = getAggregationsGroupsAgg(index, queries, size, after, m, aggAggregates);
                lsTotal.addAll(ls);
                if (ls.size() < size) {
                    break;
                }
                after = ls.get(size - 1);
            }
            if (CollectionUtils.isNotEmpty(lsTotal)) {
                List<AggGroup> union = ListUtils.union(aggGroupsSortSize, aggGroupsSort);
                if (CollectionUtils.isNotEmpty(union)) {
                    Comparator<Map<String, Object>> c = null;
                    for (AggGroup group : union) {
                        boolean asc1 = group.getOrder().isAsc();
                        String name;
                        if (group.getOrder().getTypeValue() == 3) {
                            name = group.getOrder().getName();
                            ToDoubleFunction<Map<String, Object>> keyExtractor = asc1 ? o -> o.get(name) == null ? Double.MAX_VALUE : (double) o.get(name) : o -> o.get(name) == null ? Double.MIN_VALUE : -(double) o.get(name);
                            c = c == null ? Comparator.comparingDouble(keyExtractor) : c.thenComparingDouble(keyExtractor);
                        } else {
                            name = group.getField();
                            Function<Map<String, Object>, Comparable> keyExtractor = o -> o.get(name) == null ? "" : o.get(name).toString();
                            if (asc1) {
                                c = c == null ? Comparator.comparing(keyExtractor) : c.thenComparing(keyExtractor);
                            } else {
                                Comparator<Map<String, Object>> c2 = Comparator.comparing(keyExtractor).reversed();
                                c = c == null ? c2 : c.thenComparing(c2);
                            }
                        }
                    }
                    lsTotal.sort(c);
                }
            }
            return lsTotal;
        } else {
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
            getQuery(index, queries, searchSourceBuilder, false);
            searchSourceBuilder.aggregation(getAbstractAggregationBuilder(total, aggGroup));
            try {
                SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
                List<Aggregation> aggregations = response.getAggregations().asList();
                if (aggregations.isEmpty()) {
                    return null;
                }
                MulFieldsGroupAgg mulFieldsGroupAgg = new MulFieldsGroupAgg();
                mulFieldsGroupAgg.total = total;
                mulFieldsGroupAgg.skip = 0;
                Object o = getAggregationsValue(mulFieldsGroupAgg, new ArrayList<>(aggregations), null, true, null, aggGroup);
                if (o instanceof List) {
                    List o1 = (List) o;
                    if (CollectionUtils.isNotEmpty(o1) && o1.size() > 10000) {
                        logWarn("分组个数超过10000!!!", index, queries, total, aggGroup, o1.size());
                    }
                }
                return o;
            } catch (Exception e) {
                LOG.error(String.format("es报错:%s|%s|%s|%s|%s", "getAggregations", index, queries, total, aggGroup), e);
                throw EsUtils.getRuntimeException(null, e);
            }
        }
    }

    public List<Map<String, Object>> getAggregationsGroupsAgg(String index, List<Query> queries, int size, Map<String, Object> after, LinkedHashMap<String, Boolean> orders, AggAggregate... aggBuilders) {
        Set<String> keys = orders.keySet();
        checkGroupFields(keys);
        List list = orders.entrySet().stream().map(o -> getTermsValuesSourceBuilder(o.getKey(), o.getValue())).collect(Collectors.toList());
        CompositeAggregationBuilder compositeAggregationBuilder = new CompositeAggregationBuilder(keys.stream().findFirst().get(), list);
        compositeAggregationBuilder.size(size);
        if (MapUtils.isNotEmpty(after)) {
            if (after.size() != orders.size()) {
                after = (Map<String, Object>) (((HashMap) after).clone());
                after.keySet().retainAll(keys);
            }
            compositeAggregationBuilder.aggregateAfter(after);
        }
        boolean b;
        if (ArrayUtils.isNotEmpty(aggBuilders)) {
            b = true;
            Arrays.stream(aggBuilders).forEach(agg -> compositeAggregationBuilder.subAggregation(getAbstractAggregationBuilder(agg)));
        } else {
            b = false;
        }
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        searchSourceBuilder.aggregation(compositeAggregationBuilder);
        try {
            SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            List<Aggregation> aggregations = response.getAggregations().asList();
            if (aggregations.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            for (Aggregation aggregation : aggregations) {
                if (aggregation != null) {
                    return getCompositeAggregationValues1(aggregation, b);
                }
                break;
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            logError(e, index, queries, after, size, orders, aggBuilders);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    private List<Map<String, Object>> getCompositeAggregationValues1(Aggregation aggregation, boolean b) {
        CompositeAggregation multiBucketsAggregation = (CompositeAggregation) aggregation;
        List<? extends CompositeAggregation.Bucket> buckets = multiBucketsAggregation.getBuckets();
        if (CollectionUtils.isEmpty(buckets)) {
            return Collections.EMPTY_LIST;
        }
        Function<CompositeAggregation.Bucket, Map<String, Object>> f;
        if (b) {
            f = o -> {
                Map<String, Object> map = o.getKey();
                List<Aggregation> aggregations = o.getAggregations().asList();
                if (CollectionUtils.isNotEmpty(aggregations)) {
                    getMetrics(aggregations, map);
                }
                return map;
            };
        } else {
            f = o -> o.getKey();
        }
        return buckets.stream().map(f).collect(Collectors.toList());
    }

    //获取所有聚合
    private void getMetrics(List<Aggregation> aggregations, Map<String, Object> map) {
        for (Aggregation aggregation : aggregations) {
            String name = aggregation.getName();
            if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
                NumericMetricsAggregation.SingleValue aggTerm = (NumericMetricsAggregation.SingleValue) aggregation;
                map.put(name, aggTerm.value());
            } else if (aggregation instanceof Percentiles) {
                Percentiles median = (Percentiles) aggregation;
                map.put(name, median.percentile(50));
            }
        }
    }

    private void checkGroupFields(Collection<String> fields) {
        if (fields.stream().anyMatch(this::isEqualsESID)) {
            logError(null, "分组聚合时排序字段禁止使用esid!请修改!!!");
        }
    }

    private boolean isEqualsESID(String field2) {
        return FIELDS_ESID.contains(field2);
    }

    private List<Object> getAggregationsGroup(String index, List<Query> queries, int size, Object after, String field, boolean asc) {
        List<CompositeValuesSourceBuilder<?>> list = new ArrayList<CompositeValuesSourceBuilder<?>>(1) {{
            add(getTermsValuesSourceBuilder(field, asc));
        }};
        CompositeAggregationBuilder compositeAggregationBuilder = new CompositeAggregationBuilder(field, list);
        compositeAggregationBuilder.size(size);
        if (Objects.nonNull(after)) {
            compositeAggregationBuilder.aggregateAfter(new HashMap<String, Object>(1) {{
                put(field, after);
            }});
        }
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        searchSourceBuilder.aggregation(compositeAggregationBuilder);
        try {
            SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            List<Aggregation> aggregations = response.getAggregations().asList();
            if (aggregations.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            for (Aggregation aggregation : aggregations) {
                if (aggregation != null) {
                    return getCompositeAggregationValues2(aggregation, field);
                }
                break;
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            logError(e, index, queries, after, size, field, asc);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    private void logError(Exception e, Object... args) {
        LOG.error(String.format("es报错:%s:%s", e.getStackTrace()[1].getMethodName(), getLogMsg(args)), e);
    }

    private List<Object> getCompositeAggregationValues2(Aggregation aggregation, String field) {
        CompositeAggregation multiBucketsAggregation = (CompositeAggregation) aggregation;
        List<? extends CompositeAggregation.Bucket> buckets = multiBucketsAggregation.getBuckets();
        if (CollectionUtils.isEmpty(buckets)) {
            return Collections.EMPTY_LIST;
        }
        return buckets.stream().map(o -> o.getKey().get(field)).collect(Collectors.toList());
    }

    @Override
    public Page<Object> getAggregations(String index, List<Query> queries, Page page, AggBuilderService aggBuilder) {
        index = getNewIndex(index);
        Integer currentPage = page.getCurrentPage();
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        if (currentPage < 2) {
            //第1页需要计算总数
            searchSourceBuilder.aggregation(getAbstractAggregationBuilder(-1, aggBuilder));
        } else {
            searchSourceBuilder.aggregation(getAbstractAggregationBuilder(3000, aggBuilder));
        }
        try {
            SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
            List<Aggregation> aggregations = response.getAggregations().asList();
            if (aggregations.isEmpty()) {
                return page;
            }
            //计算总数的要写在前面,因为aggregations在後面获取分页内容时会清空掉
            if (currentPage < 2) {
                long aggregationsTotal = getAggregationsTotal(aggBuilder, aggregations);
                if (aggregationsTotal > 10000) {
                    logWarn("分组个数超过10000!!!", index, queries, page, aggBuilder, aggregationsTotal);
                }
                page.setTotalCount(aggregationsTotal);
            }
            if (page.getLimit() != 0) {
                MulFieldsGroupAgg mulFieldsGroupAgg = new MulFieldsGroupAgg();
                mulFieldsGroupAgg.total = currentPage * page.getLimit();
                mulFieldsGroupAgg.skip = page.getStart().intValue();
                getAggregationsValue(mulFieldsGroupAgg, new ArrayList<>(aggregations), null, true, page, aggBuilder);
            }
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s", "getAggregations", index, queries, page, aggBuilder), e);
            EsUtils.throwRuntimeException(e);
        }
        return page;
    }

    /***
     * 判断esid是否存在
     * @param index
     * @param esid
     * @return
     */
    @Override
    public boolean getExist(String index, String esid) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(esid)) {
           EsUtils.throwIllegalArgumentException("index和esid都禁止为空!");
        }
        String type = index;
        index = getNewIndex(index);
        return EsUtils.operExist(index, type, esid);
    }

    private void addModifyTime(String index, Map<String, Object> map) {
        if (HS_ADD_MODIFYTIME.contains(index)) {
            map.put(FIELD_MODIFY_TIME, System.currentTimeMillis());
        } else if (HS_ADD_MODIFYTIME2.contains(index)) {
            map.put(FIELD_ES_MODIFY_TIME, System.currentTimeMillis());
        }
    }

    private void addModifyTime(String index, Collection<Map<String, Object>> ls) {
        if (HS_ADD_MODIFYTIME.contains(index)) {
            long l = System.currentTimeMillis();
            ls.forEach(map -> map.put(FIELD_MODIFY_TIME, l));
        } else if (HS_ADD_MODIFYTIME2.contains(index)) {
            long l = System.currentTimeMillis();
            ls.forEach(map -> map.put(FIELD_ES_MODIFY_TIME, l));
        }
    }

    @Override
    public List<Map<String, Object>> updateBySelect(String index, List<Query> queries, Map<String, Object> updateFields) {
        if (MapUtils.isEmpty(updateFields)) {
            return null;
        }
        logInfo("es增删改", "updateBySelect", index, queries, updateFields);
        String index2 = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        addModifyTime(index, updateFields);
        try {
            List<Map<String, Object>> resultMap = EsUtils.operUpdateByQuery(index2, index, searchSourceBuilder, updateFields);
            HashSet<String> ids = new HashSet<>();
            for (Map<String, Object> map : resultMap) {
                ids.add((String) map.get("id"));
            }
            List<Map<String, Object>> result = EsUtils.operSearch1(index2, null, searchSourceBuilder);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> map : result) {
                    if (!ids.contains(map.get("id"))) {
                        updateFields.put("id", map.get("id"));
                        if (map.get("id") != null && !"".equals(map.get("id"))) {
                            hbaseService.insert(index, updateFields);
                        }
                    }
                }
            }
            return resultMap;
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s", "updateBySelect", index, queries, updateFields), e);
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateBySelect(String index, List<Query> queries, Consumer<List<Map<String, Object>>> consumer, String... showFields) {
        updateBySelect(index, queries, consumer, null, showFields);
    }

    @Override
    public void updateBySelect(String index, List<Query> queries, Consumer<List<Map<String, Object>>> consumer, LinkedHashMap<String, Boolean> sorts, String... showFields) {
        if (consumer == null) {
            return;
        }
        logInfo("es增删改", "updateBySelect", index, queries, "consumer", sorts, showFields);
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, false);
        getSorts(sorts, searchSourceBuilder);
        ESUtils.setFetchSource(searchSourceBuilder, showFields);
        try {
            EsUtils.operSearchScrollAndDeal(index, null, searchSourceBuilder, consumer, false);
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s|%s", "updateBySelect", index, queries, "consumer", showFields), e);
            EsUtils.throwRuntimeException(e);
        }
    }

    @Override
    public void deleteBySelect(String index, List<Query> queries) {
        if (queries == null) {
            EsUtils.throwIllegalArgumentException("queries禁止为空!");
        }
        logInfo("es增删改", "deleteBySelect", index, queries);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        boolean isQuery = getQuery(index, queries, searchSourceBuilder, false);
        if (!isQuery) {
            EsUtils.throwIllegalArgumentException("queries禁止为空!");
        }
        String index2 = getNewIndex(index);
        try {
            EsUtils.operDeleteByQuery(index2, index, searchSourceBuilder, false);
            List<Map<String, Object>> result = EsUtils.operSearch1(index2, null, searchSourceBuilder);
            if (result != null && result.size() > 0) {
                List<String> esids = new ArrayList<>();
                for (Map<String, Object> map : result) {
                    if (map.get("id") != null && !"".equals(map.get("id"))) {
                        esids.add(map.get("id").toString());
                    }
                }
                hbaseService.delete(index, esids);
            }
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|", "deleteBySelect", index, queries), e);
            EsUtils.throwRuntimeException(e);
        }
    }

    @Override
    public String insertOrReplace(String index, Object esid, Map<String, Object> map) {
        //judgeESID(esid);
        if (StringUtils.isEmpty(index) || MapUtils.isEmpty(map)) {
            return null;
        }
        String index2 = getNewIndex(index);
        if (esid != null) {
            map.put(ESID_FIELD, esid);
        }
        addModifyTime(index, map);
        try {
            Tuple2<String, String> t = ESUtils.operIndex(index2, index, map, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
            if (StringUtils.isNotEmpty(t.second)) {
                LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrReplace", index, esid, map), t.second);
            } else {
//				Map<String, Object> resultMap = sub(index,map,true);
//				if(resultMap!=null) {

                hbaseService.insert(index, map);
//				}
            }

            return t.first;
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrReplace", index, esid, map), e);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    @Override
    public boolean insertOrUpdateFields(String index, Object esid, Map<String, Object> map) {
        return insertOrUpdateFields(index, esid, map, false);
    }

    @Override
    public boolean insertOrUpdateFields(String index, Object esid, Map<String, Object> map, boolean retryOnConflict) {
        //judgeESID(esid);
        if (StringUtils.isEmpty(index) || MapUtils.isEmpty(map)) {
            return false;
        }
        logInfo("es增删改", "insertOrUpdateFields", index, esid, map, retryOnConflict);
        String index2 = getNewIndex(index);
        addModifyTime(index, map);
        if (esid == null) {
            return insertOrUpdateFieldsCore(() -> {
                try {
                    Tuple2<Boolean, String> t = EsUtils.operIndex(index2, index, map, esid);
                    if (StringUtils.isNotEmpty(t.second)) {
                        LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrUpdateFields", index, esid, map), t.second);
                    } else {
                        if (index != null && "drug_patent_info_v2".equals(index)) {
                            hbaseService.insert(index, map);
                        }
                    }
                    return t.first;
                } catch (Exception e) {
                    LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrUpdateFields", index, esid, map), e);
                    throw EsUtils.getRuntimeException(null, e);
                }
            });
        }
        map.put(ESID_FIELD, esid);
        try {
            Tuple2<Boolean, String> t = EsUtils.operUpdate(index2, index, map, esid, WriteRequest.RefreshPolicy.IMMEDIATE, retryOnConflict);
            if (StringUtils.isNotEmpty(t.second)) {
                LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrUpdateFields", index, esid, map), t.second);
            } else {
                if (index != null && "drug_patent_info_v2".equals(index)) {
                    hbaseService.insert(index, map);
                }
            }
            return t.first;
        } catch (Exception e) {
            LOG.error(String.format("es报错:%s|%s|%s|%s", "insertOrUpdateFields", index, esid, map), e);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    @Override
    public List<Map<String, Object>> insertOrReplaceBulk(String index, List<Map<String, Object>> list) {
        //judgeESID((List<Map<K, V>>) list);
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(list)) {
            return null;
        }
        logInfo("es增删改", "insertOrReplaceBulk", index, list.size());
        String index2 = getNewIndex(index);
        addModifyTime(index, list);
        try {
            Tuple2<List<Map<String, Object>>, String> t = EsUtils.operBulkIndex(index2, index, WriteRequest.RefreshPolicy.IMMEDIATE, list);
            String error = t.second;
            if (StringUtils.isNotEmpty(error)) {
                LOG.error(String.format("es报错:%s|%s|%s", "insertOrReplaceBulk", index, error));
            }
            List<Map<String, Object>> hbaseMap = new ArrayList<>();
            List<Map<String, Object>> resultMap = t.first;
            if (resultMap != null && resultMap.size() > 0) {
                hbaseMap = getHbaseList(resultMap, list);
            } else {
                hbaseMap = list;
            }
//			List<Map<String, Object>> filterMap = sub(index,hbaseMap,true);
//			if(filterMap!=null && filterMap.size()>0) {
            hbaseService.BulkInsert(index, hbaseMap);
//			}
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    private List<Map<String, Object>> getHbaseList(List<Map<String, Object>> resultMap, List<Map<String, Object>> list) {
        List<Map<String, Object>> resultlist = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (Map<String, Object> map : resultMap) {
            if (map != null && map.get("id") != null) {
                ids.add(map.get("id").toString());
            }
        }
        for (Map<String, Object> m : list) {
            if (m.get("esid") != null && !ids.contains(m.get("esid").toString())) {
                resultlist.add(m);
            }
        }
        return resultlist;
    }

    @Override
    public <E, R> Tuple insertOrReplaceBulk(String index, List<Map<String, Object>> list, Supplier<E> supplierError, BiConsumer<E, ? super Tuple> consumerError, Supplier<R> supplierRight, BiConsumer<R, ? super Tuple> consumerRight) {
        //judgeESID((List<Map<K, V>>) list);
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(list)) {
            return null;
        }
        logInfo("es增删改", "insertOrReplaceBulk", index, list.size(), "supplierError", "consumerError", "supplierRight", "consumerRight");
        String index2 = getNewIndex(index);
        addModifyTime(index, list);
        try {
            Tuple tuple = EsUtils.operBulkIndex(index2, index, WriteRequest.RefreshPolicy.IMMEDIATE, list, supplierError, consumerError, supplierRight, consumerRight);
            List<Map<String, Object>> hbaseMap = new ArrayList<>();
            List<Map<String, Object>> resultMap = tuple.get(0);
            if (resultMap != null && resultMap.size() > 0) {
                hbaseMap = getHbaseList(resultMap, list);
            } else {
                hbaseMap = list;
            }
//			List<Map<String, Object>> filterMap = sub(index,hbaseMap,true);
//			if(filterMap!=null && filterMap.size()>0) {
            hbaseService.BulkInsert(index, hbaseMap);
//			}
            return tuple;
        } catch (Exception e) {
            e.printStackTrace();
            EsUtils.throwRuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> updateFieldsBulk(String index, List<Map<String, Object>> list) {
        judgeESID(list);
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(list)) {
            return null;
        }
        logInfo("es增删改", "updateFieldsBulk", index, list.size());
        String index2 = getNewIndex(index);
        addModifyTime(index, list);
        Tuple2<List<Map<String, Object>>, String> tuple = EsUtils.operBulkUpdate(index2, index, WriteRequest.RefreshPolicy.IMMEDIATE, list);
        String error = tuple.second;
        if (StringUtils.isNotEmpty(error)) {
            LOG.error(String.format("es报错:%s|%s|%s", "updateFieldsBulk", index, error));
        }
        List<Map<String, Object>> hbaseMap = new ArrayList<>();
        List<Map<String, Object>> resultMap = tuple.first;
        if (resultMap != null && resultMap.size() > 0) {
            hbaseMap = getHbaseList(resultMap, list);
        } else {
            hbaseMap = list;
        }
//			List<Map<String, Object>> filterMap = sub(index,hbaseMap,true);
//			if(filterMap!=null && filterMap.size()>0) {
        hbaseService.BulkInsert(index, hbaseMap);
//			}
        return resultMap;
    }

    private void judgeESID(Object esid) {
        EsUtils.judgeObjectNotNull("esid", esid);
        if (esid instanceof String) {
            EsUtils.judgeStringNotNull(ESID_FIELD, (String) esid);
        }
    }

    private <K, V> void judgeESID(List<Map<K, V>> list) {
        if (list.stream().anyMatch(o -> !o.containsKey(ESID_FIELD))) {
            EsUtils.throwIllegalArgumentException("list中的map对象必须含有esid字段!");
        }
    }

    @Override
    public <T> List<T> deleteRealBulk(String index, List<T> list) {
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(list)) {
            return null;
        }
        logInfo("es增删改", "deleteRealBulk", index, list.size());
        String index2 = getNewIndex(index);
        List<T> t = EsUtils.operBulkDelete(index2, index, WriteRequest.RefreshPolicy.IMMEDIATE, list);
        List<String> esids = new ArrayList<>();
        for (T s : list) {
            if (t == null || (t != null && !t.contains(s))) {
                esids.add(s.toString());
            }
        }
        hbaseService.delete(index, esids);
        return t;
    }

    @Override
    public <T> boolean deleteReal(String index, T esid) {
        if (esid == null) {
            return false;
        }
        logInfo("es增删改", "deleteReal", index, esid);
        String index2 = getNewIndex(index);
        boolean result = EsUtils.operDelete(index2, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
        hbaseService.delete(index, esid.toString());
        return result;
    }

    @Override
    public boolean deleteIndexAllData(String index) {
        if (StringUtils.isEmpty(index)) {
            return false;
        }
        logInfo("es增删改", "deleteIndexAllData", index);
        String index2 = getNewIndex(index);
//		hbaseService.truncateTable(index);
        return EsUtils.operDelete(index2, index, WriteRequest.RefreshPolicy.IMMEDIATE);
    }

    private boolean insertOrUpdateFieldsCore(BooleanSupplier booleanSupplier) {
        boolean flag;
        for (int i = 0; i < 3; ++i) {
            flag = booleanSupplier.getAsBoolean();
            if (flag) {
                return flag;
            }
        }
        return false;
    }

    //根据page中sort参数获取ES的sort参数
    private boolean getSorts(String[] orderColumns, String[] orderDirs, SearchSourceBuilder searchSourceBuilder) {
        if (ArrayUtils.isEmpty(orderColumns)) {
            return false;
        }
        int length = orderColumns.length;
        Map<String, Boolean> orders = getLinkedHashMap(length);
        //排序方式不填,则默认全部升序
        if (ArrayUtils.isEmpty(orderDirs)) {
            for (int i = 0; i < length; ++i) {
                orders.put(orderColumns[i], null);
            }
        } else {
            String[] newSortOrder;
            if (orderDirs.length == length) {//排序方式按用户指定的方式进行排序
                newSortOrder = orderDirs;
            } else {//排序方式为空的,则按升序进行排序
                newSortOrder = Arrays.copyOf(orderDirs, length);
            }
            for (int i = 0; i < length; ++i) {
                orders.put(orderColumns[i], "desc".equals(newSortOrder[i]) ? false : null);
            }
        }
        return getSorts(orders, searchSourceBuilder);
    }

    //根据page中sort参数获取ES的sort参数
    private boolean getSorts(Map<String, Boolean> orders, SearchSourceBuilder searchSourceBuilder) {
        if (MapUtils.isEmpty(orders)) {
            return false;
        }
        orders.forEach((k, v) -> searchSourceBuilder.sort(SortBuilders.fieldSort(ESID_FIELD.equals(k) ? ES_ID_FIELD : k).order(getSortOrder(v)).missing("_last")));
        return true;
    }

    //根据page中sort参数获取ES的sort参数
    private boolean getSortsNoESID(Map<String, Boolean> orders, SearchSourceBuilder searchSourceBuilder) {
        if (MapUtils.isEmpty(orders)) {
            return false;
        }
        if (orders.containsKey(ESID_FIELD)) {
            EsUtils.throwIllegalArgumentException(String.format("排序字段禁止用:%s", ESID_FIELD));
        }
        orders.forEach((k, v) -> searchSourceBuilder.sort(SortBuilders.fieldSort(k).order(getSortOrder(v)).missing("_last")));
        return true;
    }

    private String getNewIndex(String index) {
        if (index.contains(ALIAS_SUFFIX)) {
            return index;
        }
        return index + ALIAS_SUFFIX;
    }

    private boolean getQuery(String index, List<Query> queries, SearchSourceBuilder searchSourceBuilder, boolean onlySearchList, String... showFields) {
        QueryBuilder query = getQuery(index, queries, onlySearchList, showFields);
        if (query == null) {
            return false;
        }
        searchSourceBuilder.query(query);
        return true;
    }

    /***
     * 获取BoolQueryBuilder查询对象
     * @param queries 集合/Query类型
     * @param onlySearchList 是否查询列表(不包含查询总数,分组聚合查询)
     * @return BoolQueryBuilder查询对象
     */
    private BoolQueryBuilder getQuery(String index, List<Query> queries, boolean onlySearchList, String... showFields) {
        if (CollectionUtils.isEmpty(queries)) {
            return null;
        }
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        BoolQueryBuilder filter = QueryBuilders.boolQuery();
        boolean isFilter = false;
        boolean temp;
        for (Query q : queries) {
            temp = makeQueryBuilder(index, q, query, filter, onlySearchList, showFields);
            isFilter = isFilter || temp;
        }
        return mergeQueryAndFilter(query, filter, isFilter);
    }

    private BoolQueryBuilder mergeQueryAndFilter(BoolQueryBuilder query, BoolQueryBuilder filter, boolean isFilter) {
        //含有filter
        if (isFilter) {
            return query.filter(filter);
        }//木有filter
        return query;
    }

    //将查询条件转换为es查询对象【正常处理】【isAnd:true是must,false是should】
    private boolean makeQueryBuilder(String index, Query q, BoolQueryBuilder query, BoolQueryBuilder filter, boolean onlySearchList, String... showFields) {
        boolean isFilter = false;
        //普通and关系
        if (ArrayUtils.isEmpty(q.getChiQuerys())) {
            String field = q.getField();
            Object[] values = q.getValues();
            QueryType queryType = q.getQueryType();
            if (ArrayUtils.isEmpty(values) && (QueryType.GT == queryType || QueryType.LT == queryType || QueryType.GE == queryType || QueryType.LE == queryType)) {
                EsUtils.throwIllegalArgumentException(String.format("%s字段做%s操作,必须有1个值!", field, queryType));
            } else if (QueryType.BN == queryType) {
                EsUtils.judgeIterLen2AndENotNull("values", values);
            }
            if (ESID_FIELD.equalsIgnoreCase(field)) {
                ArrayList<String> ids = new ArrayList<>(values.length);
                String id;
                for (Object value : values) {
                    if (value != null && StringUtils.isNotEmpty(id = value.toString())) {
                        ids.add(id);
                    }
                }
                EsUtils.judgeIterNotNull(ESID_FIELD, ids);
                if (QueryType.NI == queryType || QueryType.NL == queryType || QueryType.NE == queryType) {
                    filter.mustNot(QueryBuilders.idsQuery().addIds(toMyArray(ids)));
                } else {
                    filter.must(QueryBuilders.idsQuery().addIds(toMyArray(ids)));
                }
                isFilter = true;
            } else {
                Tuple2<Boolean, Boolean> temp = getCoreQuery(index, queryType, field, values, query, filter);
                isFilter = isFilter || temp.first;
            }
        } else {
            //nested查询
            if (q.isNested()) {
                BoolQueryBuilder queryCur = QueryBuilders.boolQuery();
                if (q.isAndOperator()) {//子query是and关系
                    for (Query chiQ : q.getChiQuerys()) {
                        makeQueryBuilder(index, chiQ, queryCur, queryCur, onlySearchList, showFields);
                    }
                } else {//子query是or关系
                    for (Query chiQ : q.getChiQuerys()) {
                        BoolQueryBuilder queryChi = QueryBuilders.boolQuery();
                        makeQueryBuilder(index, chiQ, queryChi, queryChi, onlySearchList, showFields);
                        queryCur.should(queryChi);
                    }
                }
                NestedQueryBuilder nestedQueryBuilder = getNestedQueryBuilder(q.getField(), q.isOnlyMatchChildrens(), q.getOnlyMatchChildrensSize(), queryCur, onlySearchList, showFields);
                query.must(nestedQueryBuilder);
            } else {//普通查询
                if (q.isAndOperator()) {//子query是and关系
                    boolean temp;
                    for (Query chiQ : q.getChiQuerys()) {
                        temp = makeQueryBuilder(index, chiQ, query, filter, onlySearchList, showFields);
                        isFilter = isFilter || temp;
                    }
                } else {//子query是or关系
                    BoolQueryBuilder queryCur = QueryBuilders.boolQuery();
                    for (Query chiQ : q.getChiQuerys()) {
                        BoolQueryBuilder queryChi = QueryBuilders.boolQuery();
                        makeQueryBuilder(index, chiQ, queryChi, queryChi, onlySearchList, showFields);
                        queryCur.should(queryChi);
                    }
                    query.must(queryCur);
                }
            }
        }
        return isFilter;
    }

    private NestedQueryBuilder getNestedQueryBuilder(String field, boolean isOnlyMatchChildrens, int size, BoolQueryBuilder queryCur, boolean onlySearchList, String... showFields) {
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(field, queryCur, ScoreMode.Avg);
        //查询列表才有加innerHit
        if (onlySearchList && isOnlyMatchChildrens) {
            InnerHitBuilder innerHitBuilder = new InnerHitBuilder(field).setSize(size);
            nestedQueryBuilder.innerHit(innerHitBuilder);
            //设置子查询的显示字段
            if (ArrayUtils.isNotEmpty(showFields)) {
                String prefix = field + ".";
                String[] showFieldsChildrens = Arrays.stream(showFields).filter(o -> o.startsWith(prefix)).toArray(String[]::new);
                if (ArrayUtils.isNotEmpty(showFieldsChildrens)) {
                    innerHitBuilder.setFetchSourceContext(EsUtils.getFetchSourceContext(showFieldsChildrens));
                }
            }
        }
        return nestedQueryBuilder;
    }

    //将查询条件转换为es查询对象【正常处理】
    private Tuple2<Boolean, Boolean> getCoreQuery(String index, QueryType queryType, String field, Object[] values, BoolQueryBuilder query, BoolQueryBuilder filter) {
        boolean isFilter = false;
        boolean isLike = false;
        switch (queryType) {
            case GT:
                filter.must(QueryBuilders.rangeQuery(field).gt(values[0]));
                isFilter = true;
                break;
            case LT:
                filter.must(QueryBuilders.rangeQuery(field).lt(values[0]));
                isFilter = true;
                break;
            case GE:
                filter.must(QueryBuilders.rangeQuery(field).gte(values[0]));
                isFilter = true;
                break;
            case LE:
                filter.must(QueryBuilders.rangeQuery(field).lte(values[0]));
                isFilter = true;
                break;
            case BN:
                Object o0 = values[0];
                Object o1 = values[1];
                if (o0 instanceof String) {
                    String s0 = (String) o0;
                    String s1 = (String) o1;
                    if (s0.compareTo(s1) > 0) {
                        filter.must(QueryBuilders.rangeQuery(field).from(s1).to(s0));
                    } else {
                        filter.must(QueryBuilders.rangeQuery(field).from(s0).to(s1));
                    }
                } else {
                    if (Double.parseDouble(o0.toString()) > Double.parseDouble(o1.toString())) {
                        filter.must(QueryBuilders.rangeQuery(field).from(o1).to(o0));
                    } else {
                        filter.must(QueryBuilders.rangeQuery(field).from(o0).to(o1));
                    }
                }
                isFilter = true;
                break;
            case NI:
                if (ArrayUtils.isEmpty(values)) {
                    filter.must(QueryBuilders.existsQuery(field));
                } else {
                    if (values.length > 65535) {
                        logWarn("ni个数超过65535!!!", index, field, values.length);
                    }
                    //if (values.length > 1000) {
                    //	EsUtils.throwRuntimeException("not in不能超过1000个!!!");
                    //}
                    if (Arrays.stream(values).anyMatch(Objects::isNull)) {
                        List<Object> ls = Arrays.stream(values).filter(Objects::nonNull).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(ls)) {
                            filter.must(QueryBuilders.existsQuery(field));
                        } else {
                            BoolQueryBuilder should = QueryBuilders.boolQuery()
                                    .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field)))
                                    .should(QueryBuilders.termsQuery(field, ls));
                            filter.mustNot(should);
                        }
                    } else {
                        filter.mustNot(QueryBuilders.termsQuery(field, values));
                    }
                }
                isFilter = true;
                break;
            case IN:
                if (ArrayUtils.isEmpty(values)) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                } else {
                    if (values.length > 65535) {
                        logWarn("in个数超过65535!!!", index, field, values.length);
                    }
                    //if (values.length > 1000) {
                    //	EsUtils.throwRuntimeException("in不能超过1000个!!!");
                    //}
                    if (Arrays.stream(values).anyMatch(Objects::isNull)) {
                        List<Object> ls = Arrays.stream(values).filter(Objects::nonNull).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(ls)) {
                            filter.mustNot(QueryBuilders.existsQuery(field));
                        } else {
                            BoolQueryBuilder should = QueryBuilders.boolQuery()
                                    .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field)))
                                    .should(QueryBuilders.termsQuery(field, ls));
                            filter.must(should);
                        }
                    } else {
                        filter.must(QueryBuilders.termsQuery(field, values));
                    }
                }
                isFilter = true;
                break;
            case IN_LIKE:
                if (ArrayUtils.isEmpty(values)) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                    isFilter = true;
                } else {
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    int i = 0;
                    if (values.length > 1000) {
                        EsUtils.throwRuntimeException("in_like不能超过1000个!!!");
                    }
                    for (Object value : values) {
                        if (value != null) {
                            ++i;
                            boolQuery.should(getLikeQuery(field, value));
                        }
                    }
                    if (i > 250) {
                        filter.must(boolQuery);
                        isFilter = true;
                    } else {
                        query.must(boolQuery);
                    }
                    isLike = true;
                }
                break;
            case NL:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.must(QueryBuilders.existsQuery(field));
                    isFilter = true;
                } else {
                    query.mustNot(getLikeQuery(field, values[0]));
                    isLike = true;
                }
                break;
            case LIKE:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                    isFilter = true;
                } else {
                    query.must(getLikeQuery(field, values[0]));
                    isLike = true;
                }
                break;
            case MATCH:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                    isFilter = true;
                } else {
                    query.must(getMatchQuery(field, values[0]));
                    isLike = true;
                }
                break;
            case NE:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.must(QueryBuilders.existsQuery(field));
                } else {
                    if (NULL_VALUE.equals(values[0])) {
                        filter.must(QueryBuilders.existsQuery(field)).mustNot(QueryBuilders.termQuery(field, ""));
                    } else if (NULL_VALUE_NESTED.equals(values[0])) {
                        filter.must(QueryBuilders.nestedQuery(field, QueryBuilders.matchAllQuery(), ScoreMode.None));
                    } else {
                        filter.mustNot(QueryBuilders.termQuery(field, values[0]));
                    }
                }
                isFilter = true;
                break;
            case EQ:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                } else {
                    //查询 字段不存在/字段为空字符串("")/字段为空数组([])
                    if (NULL_VALUE.equals(values[0])) {
                        filter.must(QueryBuilders.boolQuery().should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field))).should(QueryBuilders.termQuery(field, "")));//查询 字段类型是json 不存在
                    } else if (NULL_VALUE_NESTED.equals(values[0])) {
                        filter.mustNot(QueryBuilders.nestedQuery(field, QueryBuilders.matchAllQuery(), ScoreMode.None));
                    } else {
                        filter.must(QueryBuilders.termQuery(field, values[0]));
                    }
                }
                isFilter = true;
                break;
            case PREFIX:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                    isFilter = true;
                } else {
                    //这里要转成小写
                    String s = values[0].toString().toLowerCase();
                    query.must(QueryBuilders.boolQuery()
                            .should(QueryBuilders.matchQuery(field + ".raw5", s).operator(Operator.AND))
                            .should(QueryBuilders.matchPhraseQuery(field + ".raw5", s))
                            .should(QueryBuilders.matchQuery(field + ".raw_prefix", s).operator(Operator.AND))
                            .should(QueryBuilders.matchPhraseQuery(field + ".raw_prefix", s))
                    );
                    isLike = true;
                }
                break;
            case WILDCARD:
                if (ArrayUtils.isEmpty(values) || values[0] == null) {
                    filter.mustNot(QueryBuilders.existsQuery(field));
                } else {
                    if (isSpecialChar(values[0].toString())) {
                        filter.must(QueryBuilders.wildcardQuery(field, String.format("*%s*", values[0])));
                    } else {
                        EsUtils.throwIllegalArgumentException(String.format("QueryType.WILDCARD仅用于匹配特殊字符,%s并不是特殊字符", values[0]));
                    }
                }
                isFilter = true;
                break;
            default:
                EsUtils.throwIllegalArgumentException(queryType);
        }
        return Tuples.tuple(isFilter, isLike);
    }

    /**
     * @author: 王坤造
     * @date: 2018/5/14 17:56
     * @comment: 判断是否包含特殊字符
     * @return:
     * @notes:
     */
    private boolean isSpecialChar(String str) {
        return p.matcher(str).find();
    }

    /**
     * 判断是否包含英文字符
     *
     * @param str
     * @return
     */
    private boolean isEnglish(String str) {
        return p_en.matcher(str).find();
    }

    private QueryBuilder getMatchQuery(String field, Object value) {
        if (value instanceof String) {
            String v = (String) value;
            if (v.length() == 1) {
                return QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery(field + ".raw", value));
            }
        }
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.matchPhraseQuery(field, value))
                .should(QueryBuilders.matchQuery(field, value))
                .should(QueryBuilders.matchQuery(field + ".raw", value))
                .should(QueryBuilders.matchQuery(field + ".raw2", value))
                .should(QueryBuilders.matchQuery(field + ".raw3", value))
                .should(QueryBuilders.matchQuery(field + ".raw4", value))
                ;
    }

    private QueryBuilder getLikeQuery(String field, Object value) {

        //region 第1版
        //QueryBuilder boolQueryBuilder=QueryBuilders.boolQuery()
        //.should(QueryBuilders.matchPhraseQuery(field + ".raw", value))
        //.should(QueryBuilders.matchQuery(field + ".raw", value))
        //.should(QueryBuilders.matchPhraseQuery(field, value).slop(10))
        //.should(QueryBuilders.wildcardQuery(field, "*" + value + "*"));//field like '%XXX%' 且不区分大小写
        //endregion

        //region 第2版
        //return QueryBuilders.disMaxQuery()//.tieBreaker(0)
        //		//.add(QueryBuilders.termQuery(field, value))
        //		.add(QueryBuilders.matchPhraseQuery(field, value).slop(10))
        //		.add(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        //		.add(QueryBuilders.matchPhraseQuery(field + ".raw", value).slop(10))
        //		.add(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
        //		.add(QueryBuilders.matchPhraseQuery(field + ".raw2", value).slop(10))
        //		.add(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND));
        //endregion

        //region 第3版
        //return QueryBuilders.boolQuery()
        //		.must(QueryBuilders.boolQuery()
        //				.should(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw3", value).operator(Operator.AND)))
        //		.should(QueryBuilders.matchPhraseQuery(field, value).slop(10))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw", value).slop(10))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw2", value).slop(10))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw3", value).slop(10));
        //endregion

        //region 第4版
        //return QueryBuilders.boolQuery()
        //		.should(QueryBuilders.boolQuery()
        //				.must(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        //				.must(QueryBuilders.matchPhraseQuery(field, value).slop(10)))
        //		.should(QueryBuilders.boolQuery()
        //				.must(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
        //				.must(QueryBuilders.matchPhraseQuery(field + ".raw", value).slop(10)))
        //		.should(QueryBuilders.boolQuery()
        //				.must(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND))
        //				.must(QueryBuilders.matchPhraseQuery(field + ".raw2", value).slop(10)))
        //		.should(QueryBuilders.boolQuery()
        //				.must(QueryBuilders.matchQuery(field + ".raw3", value).operator(Operator.AND))
        //				.must(QueryBuilders.matchPhraseQuery(field + ".raw3", value).slop(10)))
        //		.should(QueryBuilders.boolQuery().boost(0.5f)
        //				.must(QueryBuilders.matchQuery(field + ".raw4", value).operator(Operator.AND))
        //				.must(QueryBuilders.matchPhraseQuery(field + ".raw4", value).slop(10)))
        //		;
        //endregion

        //region 第5版
        ////如果有包含特殊字符,则进行分词查询
        //if (value instanceof String && isSpecialChar((String) value)) {
        //	return QueryBuilders.boolQuery()
        //			.should(QueryBuilders.boolQuery()
        //					.must(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        //					.must(QueryBuilders.matchPhraseQuery(field, value).slop(10)))
        //			.should(QueryBuilders.boolQuery()
        //					.must(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
        //					.must(QueryBuilders.matchPhraseQuery(field + ".raw", value).slop(10)))
        //			.should(QueryBuilders.boolQuery()
        //					.must(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND))
        //					.must(QueryBuilders.matchPhraseQuery(field + ".raw2", value).slop(10)))
        //			.should(QueryBuilders.boolQuery()
        //					.must(QueryBuilders.matchQuery(field + ".raw3", value).operator(Operator.AND))
        //					.must(QueryBuilders.matchPhraseQuery(field + ".raw3", value).slop(10)))
        //			.should(QueryBuilders.boolQuery().boost(0.5f)
        //					.must(QueryBuilders.matchQuery(field + ".raw4", value).operator(Operator.AND))
        //					.must(QueryBuilders.matchPhraseQuery(field + ".raw4", value).slop(10)))
        //			;
        //}
        ////不分词查询
        //return QueryBuilders.boolQuery()
        //		.should(QueryBuilders.matchPhraseQuery(field, value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw2", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw3", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw4", value).boost(0.5f))
        //		;
        //endregion

        //region 第6版
        ////如果有包含特殊字符,则进行分词查询
        //if (value instanceof String) {
        //	String v = (String) value;
        //	if (isEnglish(v) || isSpecialChar(v)) {
        //		return QueryBuilders.boolQuery()
        //				.should(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw3", value).operator(Operator.AND))
        //				.should(QueryBuilders.matchQuery(field + ".raw4", value).operator(Operator.AND).boost(0.5f))
        //				;
        //	}
        //}
        ////不分词查询
        //return QueryBuilders.boolQuery()
        //		.should(QueryBuilders.matchPhraseQuery(field, value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw2", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw3", value))
        //		.should(QueryBuilders.matchPhraseQuery(field + ".raw4", value).boost(0.5f))
        //		;
        //endregion

        //region 第7版
        //如果有包含特殊字符,则进行分词查询
        if (value instanceof String) {
            String v = (String) value;
            if (isEnglish(v) || isSpecialChar(v)) {
                value = v.replace('+', ' ');
                return QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery(field, value).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery(field + ".raw", value).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery(field + ".raw2", value).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery(field + ".raw3", value).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery(field + ".raw4", value).operator(Operator.AND).boost(0.5f))
                        ;
            }
        }
        //不分词查询
        return QueryBuilders.boolQuery()
                .should(QueryBuilders.matchPhraseQuery(field, value))
                .should(QueryBuilders.matchPhraseQuery(field + ".raw", value))
                .should(QueryBuilders.matchPhraseQuery(field + ".raw2", value))
                .should(QueryBuilders.matchPhraseQuery(field + ".raw3", value))
                .should(QueryBuilders.matchPhraseQuery(field + ".raw4", value).boost(0.5f))
                ;
        //endregion
    }

    private Tuple2<Calendar, Calendar> getCalendar(String index, List<Query> queries, String beginTimeField, String endTimeField) {
        Long begin = null;
        Long end = null;
        if (CollectionUtils.isNotEmpty(queries)) {
            ArrayList<Query> queriesDate = new ArrayList<>(2);
            for (Query query : queries) {
                //获取开始时间
                if (query.getField().equals(beginTimeField)) {
                    queriesDate.add(query);
                    Object o = query.getValues()[0];
                    if (o instanceof Date) {
                        begin = ((Date) o).getTime();
                    } else if (o instanceof Long) {
                        begin = (long) o;
                    }
                }
                //获取结束时间
                if (query.getField().equals(endTimeField)) {
                    queriesDate.add(query);
                    Object o = query.getValues()[0];
                    if (o instanceof Date) {
                        end = ((Date) o).getTime();
                    } else if (o instanceof Long) {
                        end = (long) o;
                    }
                }
            }
            if (queriesDate.size() > 0) {
                queries.removeAll(queriesDate);
            }
        }
        //如果开始时间和结束时间有为空的,则从索引中获取
        if (begin == null || end == null) {
            int i = 0;
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
            if (begin == null && end == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.min(beginTimeField).field(beginTimeField));
                searchSourceBuilder.aggregation(AggregationBuilders.max(endTimeField).field(endTimeField));
            } else if (begin == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.min(beginTimeField).field(beginTimeField));
                i = 1;
            } else if (end == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.max(endTimeField).field(endTimeField));
                i = 2;
            }
            try {
                SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
                List<Aggregation> aggregations = response.getAggregations().asList();
                //获取时间字符串并转化为时间格式类型
                if (i == 0) {
                    NumericMetricsAggregation.SingleValue min = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    begin = (long) min.value();
                    NumericMetricsAggregation.SingleValue max = (NumericMetricsAggregation.SingleValue) aggregations.get(1);
                    end = (long) max.value();
                } else if (i == 1) {
                    NumericMetricsAggregation.SingleValue min = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    begin = (long) min.value();
                } else if (i == 2) {
                    NumericMetricsAggregation.SingleValue max = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    end = (long) max.value();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        if (end < begin) {
            return null;
        }
        Calendar cBegin = Calendar.getInstance();
        cBegin.setTimeInMillis(begin);
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTimeInMillis(end);
        return Tuples.tuple(cBegin, cEnd);
    }

    private Tuple2<Long, Long> getTimes(String index, List<Query> queries, String beginTimeField, String endTimeField) {
        Long begin = null;
        Long end = null;
        if (CollectionUtils.isNotEmpty(queries)) {
            ArrayList<Query> queriesDate = new ArrayList<>(2);
            for (Query query : queries) {
                //获取开始时间
                if (query.getField().equals(beginTimeField)) {
                    queriesDate.add(query);
                    Object o = query.getValues()[0];
                    if (o instanceof Date) {
                        begin = ((Date) o).getTime();
                    } else if (o instanceof Long) {
                        begin = (long) o;
                    }
                }
                //获取结束时间
                if (query.getField().equals(endTimeField)) {
                    queriesDate.add(query);
                    Object o = query.getValues()[0];
                    if (o instanceof Date) {
                        end = ((Date) o).getTime();
                    } else if (o instanceof Long) {
                        end = (long) o;
                    }
                }
            }
            if (queriesDate.size() > 0) {
                queries.removeAll(queriesDate);
            }
        }
        //如果开始时间和结束时间有为空的,则从索引中获取
        if (begin == null || end == null) {
            int i = 0;
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
            if (begin == null && end == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.min(beginTimeField).field(beginTimeField));
                searchSourceBuilder.aggregation(AggregationBuilders.max(endTimeField).field(endTimeField));
            } else if (begin == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.min(beginTimeField).field(beginTimeField));
                i = 1;
            } else if (end == null) {
                searchSourceBuilder.aggregation(AggregationBuilders.max(endTimeField).field(endTimeField));
                i = 2;
            }
            try {
                SearchResponse response = EsUtils.operSearchAgg(index, null, searchSourceBuilder);
                List<Aggregation> aggregations = response.getAggregations().asList();
                //获取时间字符串并转化为时间格式类型
                if (i == 0) {
                    NumericMetricsAggregation.SingleValue min = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    begin = (long) min.value();
                    NumericMetricsAggregation.SingleValue max = (NumericMetricsAggregation.SingleValue) aggregations.get(1);
                    end = (long) max.value();
                } else if (i == 1) {
                    NumericMetricsAggregation.SingleValue min = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    begin = (long) min.value();
                } else if (i == 2) {
                    NumericMetricsAggregation.SingleValue max = (NumericMetricsAggregation.SingleValue) aggregations.get(0);
                    end = (long) max.value();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        if (end < begin) {
            return null;
        }
        return Tuples.tuple(begin, end);
    }

    private void getFilterAgg2(Tuple2<Long, Long> pair, String beginTimeField, String endTimeField, AbstractAggregationBuilder aggregationBuilder, int showType, SearchSourceBuilder searchSourceBuilder) {
        LocalDateTime cBegin = getLocalDateTime(pair.first);
        LocalDateTime cEnd = getLocalDateTime(pair.second);
        int yearBegin = cBegin.getYear();
        int monthBegin = cBegin.getMonthValue();
        int yearEnd = cEnd.getYear();
        int monthEnd = cEnd.getMonthValue();
        int monthEndTemp = 13;
        boolean flag = false;
        while (true) {
            //查询每年的聚合结果
            if (showType == 0 || showType == 1) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                //获取年份查询开始时间
                cBegin = getLocalDateTime(yearBegin, monthBegin);
                boolQueryBuilder.must(QueryBuilders.rangeQuery(endTimeField).gte(getTimestamp(cBegin)));
                //获取年份查询结束时间
                if (yearBegin + 1 <= yearEnd) {
                    //不是最後一年,年份+1
                    cEnd = getLocalDateTime(yearBegin + 1, 1);
                } else {
                    //是最後一年,则最後月份+1
                    cEnd = getLocalDateTime(yearEnd, monthEnd);
                    cEnd = cEnd.plusMonths(1);
                    flag = true;
                }
                boolQueryBuilder.must(QueryBuilders.rangeQuery(beginTimeField).lt(getTimestamp(cEnd)));
                searchSourceBuilder.aggregation(AggregationBuilders.filter(Integer.toString(yearBegin), boolQueryBuilder).subAggregation(aggregationBuilder));
            }
            //region 查询每季的聚合结果
            //if (showType == 0 || showType == 2) {
            //	if (yearBegin + 1 > yearEnd) {
            //		monthEndTemp = monthEnd + 1;
            //		flag = true;
            //	}
            //	for (int month = monthBegin; month < monthEndTemp; ) {
            //		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //		//获取季度查询开始时间
            //		cBegin = getLocalDateTime(yearBegin, month);
            //		boolQueryBuilder.must(QueryBuilders.rangeQuery(endTimeField).gte(getTimestamp(cBegin)));
            //		int step = 4 - month % 3;
            //		//
            //		cEnd = cBegin.plusMonths(step);
            //		boolQueryBuilder.must(QueryBuilders.rangeQuery(beginTimeField).lt(getTimestamp(cEnd)));
            //		searchSourceBuilder.aggregation(AggregationBuilders.filter(yearBegin + ".Q" + MAP_QUARTER_END.get(month) / 3, boolQueryBuilder).subAggregation(aggregationBuilder));
            //		for (month += step; month < monthEndTemp; month += 3) {
            //			BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
            //			//获取季度查询开始时间
            //			cBegin = getLocalDateTime(yearBegin, month);
            //			boolQueryBuilder2.must(QueryBuilders.rangeQuery(endTimeField).gte(getTimestamp(cBegin)));
            //			//
            //			if (monthEndTemp - month >= 3) {
            //				step = 3;
            //			} else {
            //				step = monthEndTemp - month;
            //			}
            //			cEnd = cBegin.plusMonths(step);
            //			boolQueryBuilder2.must(QueryBuilders.rangeQuery(beginTimeField).lt(getTimestamp(cEnd)));
            //			searchSourceBuilder.aggregation(AggregationBuilders.filter(yearBegin + ".Q" + MAP_QUARTER_END.get(month) / 3, boolQueryBuilder2).subAggregation(aggregationBuilder));
            //		}
            //		break;
            //	}
            //}
            //endregion

            //region 查询每月的聚合结果
            //if (showType == 0 || showType == 2) {
            //	if (yearBegin + 1 > yearEnd) {
            //		monthEndTemp = monthEnd;
            //		flag = true;
            //	}
            //	for (int month = monthBegin; month < monthEndTemp; ++month) {
            //		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //		//获取月份查询开始时间
            //		cBegin = getLocalDateTime(yearBegin, month);
            //		boolQueryBuilder.must(QueryBuilders.rangeQuery(endTimeField).gte(getTimestamp(cBegin)));
            //		//获取月份查询结束时间,月份+1
            //		cBegin = cBegin.plusMonths(1);
            //		boolQueryBuilder.must(QueryBuilders.rangeQuery(beginTimeField).lte(getTimestamp(cBegin)));
            //		searchSourceBuilder.aggregation(AggregationBuilders.filter(yearBegin + "-" + month, boolQueryBuilder).subAggregation(aggregationBuilder));
            //	}
            //}
            //endregion
            if (flag) {
                break;
            }
            //每次循环,年份+1,月份=1
            ++yearBegin;
            monthBegin = 1;
        }
    }

    private void getFilterAgg22(Tuple2<Long, Long> pair, String beginTimeField, String endTimeField, List<AbstractAggregationBuilder> aggregationBuilder, int showType, SearchSourceBuilder searchSourceBuilder) {
        LocalDateTime cBegin = getLocalDateTime(pair.first);
        LocalDateTime cEnd = getLocalDateTime(pair.second);
        int yearBegin = cBegin.getYear();
        int monthBegin = cBegin.getMonthValue();
        int yearEnd = cEnd.getYear();
        int monthEnd = cEnd.getMonthValue();
        int monthEndTemp = 13;
        boolean flag = false;
        while (true) {
            //查询每年的聚合结果
            if (showType == 0 || showType == 1) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                //获取年份查询开始时间
                cBegin = getLocalDateTime(yearBegin, monthBegin);
                boolQueryBuilder.must(QueryBuilders.rangeQuery(endTimeField).gte(getTimestamp(cBegin)));
                //获取年份查询结束时间
                if (yearBegin + 1 <= yearEnd) {
                    //不是最後一年,年份+1
                    cEnd = getLocalDateTime(yearBegin + 1, 1);
                } else {
                    //是最後一年,则最後月份+1
                    cEnd = getLocalDateTime(yearEnd, monthEnd);
                    cEnd = cEnd.plusMonths(1);
                    flag = true;
                }
                boolQueryBuilder.must(QueryBuilders.rangeQuery(beginTimeField).lt(getTimestamp(cEnd)));
                FilterAggregationBuilder filter = AggregationBuilders.filter(Integer.toString(yearBegin), boolQueryBuilder);
                aggregationBuilder.forEach(filter::subAggregation);
                searchSourceBuilder.aggregation(filter);
            }
            if (flag) {
                break;
            }
            //每次循环,年份+1,月份=1
            ++yearBegin;
            monthBegin = 1;
        }
    }

    /**
     * 新版分组聚合相关
     */
    //组装es通用分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggBuilderService aggBuilderService) {
        switch (aggBuilderService.getType()) {
            case AGGREGATE://聚合
                return getAbstractAggregationBuilder((AggAggregate) aggBuilderService);
            case SINGLE_GROUP://分组/分组聚合
            case MULTIPLE_GROUP:
            case SINGLE_GROUP_AGGREGATE:
            case MULTIPLE_GROUP_AGGREGATE:
                return getAbstractAggregationBuilder(total, (AggGroup) aggBuilderService);
            case NESTED://子查询
                return getAbstractAggregationBuilder(total, (AggNested) aggBuilderService);
            case REVERSE_NESTED:
                return getAbstractAggregationBuilder(total, (AggReverseNested) aggBuilderService);
            case FILTER:
                return getAbstractAggregationBuilder(total, (AggFilter) aggBuilderService);
            case DATE:
                return getAbstractAggregationBuilder(total, (AggDate) aggBuilderService);
            default:
                EsUtils.throwIllegalArgumentException(aggBuilderService.getType());
        }
        return null;
    }

    //组装es过滤分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggFilter aggFilter) {
        aggFilter.judgeForcedSubAggBuilder();
        List<AggBuilderService> aggBuilderServices = aggFilter.getAggBuilders();
        QueryBuilder queryBuilder = getQuery(null, Arrays.asList(aggFilter.getFilter()), false);
        EsUtils.judgeObjectNotNull("aggFilter.getFilter()", queryBuilder);
        FilterAggregationBuilder filter = AggregationBuilders.filter(aggFilter.getAlias(), queryBuilder);
        addSubAggregation(aggBuilderServices, total, filter);
        return filter;
    }

    //组装es時间分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggDate aggDate) {
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram(aggDate.getAlias()).field(aggDate.getField())
                .dateHistogramInterval(getDateHistogramInterval(aggDate.getDateInterval()))
                .offset(aggDate.getOffset());
        //.extendedBounds(null)
        //.keyed(true)
        //设置時区
        //.timeZone(DateTimeZone.forOffsetHours(8))
        setDateOrder(dateHistogramAggregationBuilder, aggDate.getOrder());
        addSubAggregation(aggDate.getAggBuilders(), total, dateHistogramAggregationBuilder);
        return dateHistogramAggregationBuilder;
    }

    private void setDateOrder(DateHistogramAggregationBuilder dateHistogramAggregationBuilder, AggGroup.Order order) {
        if (order != null) {
            dateHistogramAggregationBuilder.order(getTermsOrder(order.getName(), order.isAsc(), order.getTypeValue(), order.getOrders()));
        }
    }

    private DateHistogramInterval getDateHistogramInterval(EsDateInterval esDateInterval) {
        return new DateHistogramInterval(esDateInterval.getExpression());
    }

    //组装es子查询分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggReverseNested aggReverseNested) {
        aggReverseNested.judgeForcedSubAggBuilder();
        List<AggBuilderService> aggBuilderServices = aggReverseNested.getAggBuilders();
        ReverseNestedAggregationBuilder reverseNestedBuilder = AggregationBuilders.reverseNested(aggReverseNested.getAlias());
        if (StringUtils.isNotEmpty(aggReverseNested.getField())) {
            reverseNestedBuilder.path(aggReverseNested.getField());
        }
        addSubAggregation(aggBuilderServices, total, reverseNestedBuilder);
        return reverseNestedBuilder;
    }

    //组装es子查询分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggNested aggNested) {
        aggNested.judgeForcedSubAggBuilder();
        NestedAggregationBuilder nestedBuilder = AggregationBuilders.nested(aggNested.getAlias(), aggNested.getField());
        addSubAggregation(aggNested.getAggBuilders(), total, nestedBuilder);
        return nestedBuilder;
    }

    //组装es分组/分组聚合对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(int total, AggGroup aggGroup) {
        //多字段分组字段
        AggGroup[] aggGroups = aggGroup.getAggGroups();
        //聚合函数/其它
        List<AggBuilderService> aggs = aggGroup.getAggBuilders();
        //多字段分组中,最後1个分组字段
        TermsAggregationBuilder lastAgg;
        //单字段分组
        if (ArrayUtils.isEmpty(aggGroups)) {
            //使用最大限制条数
            if (aggGroup.isUseMaxSize()) {
                lastAgg = getTermsBuilder(aggGroup, EsUtils.MAX_SIZE_GROUP2, total);
            } else {
                lastAgg = getTermsBuilder(aggGroup, EsUtils.MAX_SIZE_GROUP, total);
            }
            //单/多字段分组中添加聚合函数
            addAgg(total, aggGroup, aggs, lastAgg);
            return lastAgg;
        }
        //多字段分组
        int length = aggGroups.length;
        //设置多字段排序顺序:有排序大小>有排序>有大小>都没有
        ArrayList<AggGroup> aggGroupsSortSize = new ArrayList<>(length);
        ArrayList<AggGroup> aggGroupsSort = new ArrayList<>(length);
        ArrayList<AggGroup> aggGroupsSize = new ArrayList<>(length);
        ArrayList<AggGroup> aggGroupsNo = new ArrayList<>(length);
        for (AggGroup field : aggGroups) {
            if (field.getOrder() == null) {
                if (field.getSize() > 0) {
                    aggGroupsSize.add(field);
                } else {
                    aggGroupsNo.add(field);
                }
            } else {
                if (field.getSize() > 0) {
                    aggGroupsSortSize.add(field);
                } else {
                    aggGroupsSort.add(field);
                }
            }
        }
        //将多字段多个集合合并为1个
        aggGroupsSortSize.addAll(aggGroupsSort);
        aggGroupsSortSize.addAll(aggGroupsSize);
        aggGroupsSortSize.addAll(aggGroupsNo);
        //多字段分组/多字段分组聚合的時候,如果已经有设置总别名,则将总别名设置到第多字段分组的第1个字段
        //如果没有设置总别名,则获取多字段分组的第1个字段别名,并设置成总别名.因为子查询多字段分组的時候要用到.
        AggGroup aggGroupFirst = aggGroupsSortSize.get(0);
        if (StringUtils.isEmpty(aggGroup.getAlias())) {
            //多字段分组的第1个字段别名,设置为总别名
            aggGroup.setAlias(aggGroupFirst.getAlias());
        } else {
            //将总别名设置到第多字段分组的第1个字段
            aggGroupFirst.setAlias(aggGroup.getAlias());
        }
        //多字段分组中,第1个分组字段
        AbstractAggregationBuilder beginAgg;
        if (aggGroup.isUseMulTerms()) {
            //嵌套多字段
            lastAgg = getTermsBuilder(aggGroupsSortSize.get(length - 1), EsUtils.SIZE_GROUP, total);//先获取倒数第1个
            beginAgg = lastAgg;
            if (length > 1) {
                TermsAggregationBuilder curTermsBuilder;
                //这里获取第2个到倒数第2个
                for (int i = length - 2; i > 0; --i) {
                    curTermsBuilder = getTermsBuilder(aggGroupsSortSize.get(i), EsUtils.SIZE_GROUP, total);
                    beginAgg = curTermsBuilder.subAggregation(beginAgg);
                }
                //最後获取第1个,第1个默认最大长度10000,其它默认最大长度2000
                curTermsBuilder = getTermsBuilder(aggGroupsSortSize.get(0), EsUtils.SIZE_GROUP, total);
                beginAgg = curTermsBuilder.subAggregation(beginAgg);
                ////这个只适用于数字/小数类型
                //List<FieldSortBuilder> ls = Collections.singletonList(new FieldSortBuilder(FIELD_PUBLISHDATE).order(SortOrder.DESC));
                //PipelineAggregationBuilder maxBucket = PipelineAggregatorBuilders.bucketSort("bucketSort", ls).size(1);
                //beginAgg.subAggregation(maxBucket);
            }
            //单/多字段分组中添加聚合函数
            addAgg(total, aggGroup, aggs, lastAgg);
        } else {
            //复合分组
            ArrayList list = new ArrayList<>(length);
            for (AggGroup group : aggGroupsSortSize) {
                list.add(getTermsValuesSourceBuilder(group));
            }
            int size = aggGroup.getSize();
            //使用最大限制条数
            if (aggGroup.isUseMaxSize()) {
                beginAgg = new CompositeAggregationBuilder(aggGroup.getAlias(), list).size(getMaxSize(total, getMaxSize(size, EsUtils.MAX_SIZE_GROUP2)));
            } else {
                beginAgg = new CompositeAggregationBuilder(aggGroup.getAlias(), list).size(getMaxSize(total, getMaxSize(size, EsUtils.SIZE_GROUP)));
            }
            //复合分组添加聚合函数
            addAgg(total, aggs, beginAgg);

        }
        return beginAgg;
    }

    //单/多字段分组中添加聚合函数
    private void addAgg(int total, AggGroup aggGroup, List<AggBuilderService> aggs, TermsAggregationBuilder lastAgg) {
        if (CollectionUtils.isNotEmpty(aggs)) {
            aggs.forEach(agg -> lastAgg.subAggregation(getAbstractAggregationBuilder(total, agg)));
            //设置聚合函数排序方式
            setTermsOrder(lastAgg, aggGroup.getOrder());
        }
    }

    //复合分组添加聚合函数
    private void addAgg(int total, List<AggBuilderService> aggs, AbstractAggregationBuilder beginAgg) {
        if (CollectionUtils.isNotEmpty(aggs)) {
            aggs.forEach(agg -> beginAgg.subAggregation(getAbstractAggregationBuilder(total, agg)));
            //禁止设置聚合函数排序方式
        }
    }

    //组装es聚合函数对象
    private AbstractAggregationBuilder getAbstractAggregationBuilder(AggAggregate aggregate) {
        switch (aggregate.getAggregateType()) {
            case AVG:
                return AggregationBuilders.avg(aggregate.getAlias()).field(aggregate.getField());
            case MAX:
                return AggregationBuilders.max(aggregate.getAlias()).field(aggregate.getField());
            case MIN:
                return AggregationBuilders.min(aggregate.getAlias()).field(aggregate.getField());
            case SUM:
                return AggregationBuilders.sum(aggregate.getAlias()).field(aggregate.getField());
            case COUNT://统计数量
                //如果统计字段是数组类型,必须使用这种方式,才能完美统计数量.
                //return AggregationBuilders.count(aggregate.getAlias()).field("_index");
                return AggregationBuilders.count(aggregate.getAlias()).field(aggregate.getField());
            case COUNT_DISTINCT://统计去重的数量
//				if (aggregate.getSize() > 0) {
//					int size = getMaxSize(aggregate.getSize(), EsUtils.MAX_SIZE_CARDINALITY);
//					return AggregationBuilders.cardinality(aggregate.getAlias()).field(aggregate.getField()).precisionThreshold(size);
//				}
//				return AggregationBuilders.cardinality(aggregate.getAlias()).field(aggregate.getField());
                return AggregationBuilders.cardinality(aggregate.getAlias()).field(aggregate.getField()).precisionThreshold(40000);
            /**
             * 下面不可排序
             * */
            case MEDIAN://中值
                //压缩值(compression):默认100,压缩值越大,精确度越高
                return AggregationBuilders.percentiles(aggregate.getAlias()).field(aggregate.getField()).percentiles(50);
            case MAX_OTHER://当A字段最大值时,此时B字段的值(A代表MAX,B代表Other,得到的是B)【如果B值为空,则会找有值的B,且A字段是相对最大的】
                //这个terms是最终要得到的值(B值)
                return AggregationBuilders.terms(aggregate.getAliasOther()).field(aggregate.getFieldOther()).size(1)
                        //如果是最小则进行升序,如果是最大则进行降序,并获取第1个
                        .order(getTermsOrder(aggregate.getAlias(), false, (byte) 3))
                        //这里可以获取A的极值
                        .subAggregation(AggregationBuilders.max(aggregate.getAlias()).field(aggregate.getField()));
            case MIN_OTHER://当A字段最小值时,此时B字段的值(A代表MIN,B代表Other,得到的是B)【如果B值为空,则会找有值的B,且A字段是相对最大的】
                //这个terms是最终要得到的值(B值)
                return AggregationBuilders.terms(aggregate.getAliasOther()).field(aggregate.getFieldOther()).size(1)
                        //如果是最小则进行升序,如果是最大则进行降序,并获取第1个
                        .order(getTermsOrder(aggregate.getAlias(), true, (byte) 3))
                        //这里可以获取A的极值
                        .subAggregation(AggregationBuilders.min(aggregate.getAlias()).field(aggregate.getField()));
            default:
                EsUtils.throwIllegalArgumentException(aggregate.getAggregateType());
        }
        return null;
    }

    private Object getAggregationsValue(MulFieldsGroupAgg mulFieldsGroupAgg, List<Aggregation> aggregations, Map<String, Object> map1, boolean isFirst, Page page, AggBuilderService... aggBuilders) {
        ArrayList<Object> result = new ArrayList<>(aggBuilders.length);
        HashMap<String, Object> map = new HashMap<>();
        for (AggBuilderService aggBuilder : aggBuilders) {
            Aggregation aggregation;
            switch (aggBuilder.getType()) {
                case AGGREGATE:
                    aggregation = getAggregation(aggregations, aggBuilder, true);
                    if (aggregation != null) {
                        getAggregationsValue(mulFieldsGroupAgg, aggregation, map, false);
                    }
                    if (!result.contains(map)) {
                        result.add(map);
                    }
                    break;
                case SINGLE_GROUP:
                    aggregation = getAggregation(aggregations, aggBuilder, true);
                    if (aggregation == null) {
                        result.add(null);
                    } else {
                        List<Object> list = getSingleGroupValue((Terms) aggregation, mulFieldsGroupAgg.total);
                        getResultList(result, list, mulFieldsGroupAgg);
                    }
                    break;
                case MULTIPLE_GROUP:
                case SINGLE_GROUP_AGGREGATE:
                case MULTIPLE_GROUP_AGGREGATE:
                case NESTED:
                case REVERSE_NESTED:
                case FILTER:
                case DATE:
                    aggregation = getAggregation(aggregations, aggBuilder, true);
                    if (aggregation == null) {
                        result.add(null);
                    } else {
                        mulFieldsGroupAgg.setList(new ArrayList<>());
                        getAggregationsValue(mulFieldsGroupAgg, aggregation, map1, isFirst);
                        getResultList(result, mulFieldsGroupAgg.list, mulFieldsGroupAgg);
                    }
                    break;
                default:
                    EsUtils.throwIllegalArgumentException(aggBuilder.getType());
            }
        }
        //result为空
        if (result.size() == 1) {
            if (result.contains(map)) {
                if (MapUtils.isEmpty(map)) {
                    return null;
                }
                return map;
            }

            Object o = result.get(0);
            if (page == null) {
                return o;
            }
            if (o instanceof List) {
                page.setItemList((List) o);
            } else {
                page.setItemList(result);
            }
        } else {
            if (page == null) {
                return result;
            }
            page.setItemList(result);
        }
        return null;
        ////result为空
        //if (CollectionUtils.isEmpty(result)) {
        //	if (EsMapUtils.isEmpty(map)) {
        //		return null;
        //	}
        //	return map;
        //}
        ////设置分页的数据,只针对result里面的元素
        //if (page != null) {
        //	if (result.size() < 2) {
        //		Object o = result.get(0);
        //		if (o instanceof List) {
        //			page.setItemList((List) o);
        //		}
        //	} else {
        //		page.setItemList(result);
        //	}
        //	return null;
        //}
        ////result有值
        //if (EsMapUtils.isEmpty(map)) {//map为空
        //	//result长度=1,返回result里面内容
        //	if (result.size() < 2) {
        //		return result.get(0);
        //	}
        //	//result长度>1,返回result
        //	return result;
        //}
        //result.add(map);//map不为空
        //return result;
    }

    //根据查询时分组聚合对象,匹配找出ES返回的分组聚合对象
    private Aggregation getAggregation(List<Aggregation> aggregations, AggBuilderService aggBuilder, boolean remove) {
        String alias = aggBuilder.getAlias();
        if (remove) {
            for (Aggregation aggregation : aggregations) {
                if (alias.equals(aggregation.getName())) {
                    aggregations.remove(aggregation);
                    return aggregation;
                }
            }
        } else {
            for (Aggregation aggregation : aggregations) {
                if (alias.equals(aggregation.getName())) {
                    return aggregation;
                }
            }
        }
        if (remove) {
            //MAX_OTHER/MIN_OTHER聚合时用另外一个别名
            if (aggBuilder instanceof AggAggregate) {
                AggAggregate agg = (AggAggregate) aggBuilder;
                alias = agg.getAliasOther();
                for (Aggregation aggregation : aggregations) {
                    if (alias.equals(aggregation.getName())) {
                        aggregations.remove(aggregation);
                        return aggregation;
                    }
                }
            }
        } else {
            //MAX_OTHER/MIN_OTHER聚合时用另外一个别名
            if (aggBuilder instanceof AggAggregate) {
                AggAggregate agg = (AggAggregate) aggBuilder;
                alias = agg.getAliasOther();
                for (Aggregation aggregation : aggregations) {
                    if (alias.equals(aggregation.getName())) {
                        return aggregation;
                    }
                }
            }
        }
        return null;
    }

    //获取聚合函数的值
    private void getAggregationsValue(MulFieldsGroupAgg mulFieldsGroupAgg, List<Aggregation> aggregations, Map<String, Object> map, boolean isFirst) {
        for (Aggregation aggregation : aggregations) {
            getAggregationsValue(mulFieldsGroupAgg, aggregation, map, isFirst);
        }
    }

    //获取聚合函数的值
    private void getAggregationsValue(MulFieldsGroupAgg mulFieldsGroupAgg, Aggregation aggregation, Map<String, Object> map, boolean isFirst) {
        String name = aggregation.getName();
        //这里获取的是标准的聚合函数:avg,max,min,sum,count,count_distinct
        if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
            NumericMetricsAggregation.SingleValue aggTerm = (NumericMetricsAggregation.SingleValue) aggregation;
            map = getResultMap(mulFieldsGroupAgg, map, isFirst);
            map.put(name, aggTerm.value());
        } else if (aggregation instanceof Percentiles) {//这里获取中值
            Percentiles median = (Percentiles) aggregation;
            map = getResultMap(mulFieldsGroupAgg, map, isFirst);
            map.put(name, median.percentile(50));
        } else if (aggregation instanceof HasAggregations) {//这里Nested/ReverseNested/Filter响应结果
            HasAggregations nested = (HasAggregations) aggregation;
            List<Aggregation> aggregations = nested.getAggregations().asList();
            if (CollectionUtils.isEmpty(aggregations)) {
                ;//不会有这种情况处理
            } else {
                getAggregationsValue(mulFieldsGroupAgg, aggregations, map, isFirst);
            }
        } else if (aggregation instanceof CompositeAggregation) {
            CompositeAggregation multiBucketsAggregation = (CompositeAggregation) aggregation;
            List<? extends CompositeAggregation.Bucket> buckets = multiBucketsAggregation.getBuckets();
            if (CollectionUtils.isEmpty(buckets)) {
                ;//暂时不知道如何处理
            } else {
                int size = buckets.size();
                if (isFirst || size < 2) {
                    for (int i = 0; i < size; ++i) {
                        CompositeAggregation.Bucket bucket = buckets.get(i);
                        map = bucket.getKey();
                        mulFieldsGroupAgg.list.add(map);
                        List<Aggregation> aggregations = bucket.getAggregations().asList();
                        if (CollectionUtils.isEmpty(aggregations)) {
                            ;//不用处理
                        } else {
                            getAggregationsValue(mulFieldsGroupAgg, aggregations, map, false);
                        }
                        if (getGroupAggregateIsOver(mulFieldsGroupAgg)) {
                            break;
                        }
                    }
                } else {
                    Map<String, Object> mapBak = new HashMap<>();
                    mapBak.putAll(map);
                    for (int i = 0; i < size; ++i) {
                        map = getResultMap(mulFieldsGroupAgg, map, false, mapBak, i);
                        //这里必须这样子判断
                        if (map == null) {
                            break;
                        }
                        CompositeAggregation.Bucket bucket = buckets.get(i);
                        map.putAll(bucket.getKey());
                        List<Aggregation> aggregations = bucket.getAggregations().asList();
                        if (CollectionUtils.isEmpty(aggregations)) {
                            ;//不用处理
                        } else {
                            getAggregationsValue(mulFieldsGroupAgg, aggregations, map, false);
                        }
                    }
                }
            }
        } else if (aggregation instanceof Terms) {//统计MAX_OTHER,MIN_OTHER
            Terms terms = (Terms) aggregation;
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            int size = buckets.size();
            Map<String, Object> mapBak;
            if (!isFirst && size > 1) {
                mapBak = new HashMap<>();
                mapBak.putAll(map);
            } else {
                mapBak = null;
            }
            if (CollectionUtils.isEmpty(buckets)) {
                if (!isFirst) {
                    //判断是否已经等于或者超过总数了
                    getGroupAggregateIsOver(mulFieldsGroupAgg);
                }
            } else {
                for (int i = 0; i < size; ++i) {
                    map = getResultMap(mulFieldsGroupAgg, map, isFirst, mapBak, i);
                    //这里必须这样子判断
                    if (map == null) {
                        break;
                    }
                    List<Aggregation> aggregations = getGroupValue(map, name, buckets, i);
                    //如果没有子分组,则当前+1
                    if (CollectionUtils.isEmpty(aggregations)) {
                        //判断是否已经等于或者超过总数了
                        if (getGroupAggregateIsOver(mulFieldsGroupAgg)) {
                            break;
                        }
                    } else {
                        //如果桶里面还有分组,则递归获取
                        getAggregationsValue(mulFieldsGroupAgg, aggregations, map, false);
                        if (mulFieldsGroupAgg.over) {
                            break;
                        }
                    }
                }
            }
        } else if (aggregation instanceof ParsedDateHistogram) {//時间分组聚合
            List<? extends Histogram.Bucket> buckets = ((ParsedDateHistogram) aggregation).getBuckets();
            for (int i = 0, len = buckets.size(); i < len; i++) {
                map = getResultMap(mulFieldsGroupAgg, map, isFirst);
                List<Aggregation> aggregations = getGroupValue(map, name, buckets, i);
                //如果没有子分组,则当前+1
                if (CollectionUtils.isEmpty(aggregations)) {
                    //判断是否已经等于或者超过总数了
                    if (getGroupAggregateIsOver(mulFieldsGroupAgg)) {
                        break;
                    }
                } else {
                    //如果桶里面还有分组,则递归获取
                    getAggregationsValue(mulFieldsGroupAgg, aggregations, map, false);
                    if (mulFieldsGroupAgg.over) {
                        break;
                    }
                }
            }
        }
    }

    //获取聚合结果,先获取map对象
    private Map<String, Object> getResultMap(MulFieldsGroupAgg mulFieldsGroupAgg, Map<String, Object> map, boolean isFirst) {
        if (isFirst) {
            map = new HashMap<>();
            mulFieldsGroupAgg.list.add(map);
        }
        return map;
    }

    //多字段分组,多字段分组聚合中,递归获取分组的值过程中,要获取当前map
    private Map<String, Object> getResultMap(MulFieldsGroupAgg mulFieldsGroupAgg, Map<String, Object> map, boolean isFirst, Map<String, Object> mapBak, int i) {
        if (isFirst) {
            //先判断是否已经获取结束了
            if (getGroupAggregateIsOver(mulFieldsGroupAgg)) {
                return null;
            }
            map = new HashMap<>();
            mulFieldsGroupAgg.list.add(map);
        } else {
            if (i > 0) {
                //先判断是否已经获取结束了
                if (getGroupAggregateIsOver(mulFieldsGroupAgg)) {
                    return null;
                }
                map = new HashMap<>();
                map.putAll(mapBak);
                mulFieldsGroupAgg.list.add(map);
            }
        }
        return map;
    }

    //获取单字段分组的值
    private List<Object> getSingleGroupValue(Terms terms, int total) {
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        int size = getMaxSize(total, buckets.size());
        ArrayList<Object> list = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            list.add(buckets.get(i).getKey());
        }
        list.remove("");
        return list;
    }

    //获取es聚合函数排序对象
    private BucketOrder getTermsOrder(String name, boolean asc, byte typeValue, AggGroup.Order... orders) {
        switch (typeValue) {
            case 1:
                return BucketOrder.count(asc);
            case 2:
                return BucketOrder.key(asc);
            case 3:
                return BucketOrder.aggregation(name, asc);
            case 4:
                ArrayList<BucketOrder> list = new ArrayList<>(orders.length);
                for (AggGroup.Order order : orders) {
                    list.add(getTermsOrder(order.getName(), order.isAsc(), order.getTypeValue(), order.getOrders()));
                }
                return BucketOrder.compound(list);
            default:
                throw new IllegalArgumentException(String.format("参数类型错误:%s", typeValue));
        }
    }

    //设置es排序对象
    private void setTermsOrder(TermsAggregationBuilder terms, AggGroup.Order order) {
        if (order != null) {
            terms.order(getTermsOrder(order.getName(), order.isAsc(), order.getTypeValue(), order.getOrders()));
        }
    }

    //设置es包含排除字段对象
    private void setTermsIncludeExclude(TermsAggregationBuilder terms, String[] includeFields) {
        if (ArrayUtils.isNotEmpty(includeFields)) {
            terms.includeExclude(new IncludeExclude(includeFields, null));
        }
    }

    private TermsValuesSourceBuilder getTermsValuesSourceBuilder(String alias, String field) {
        return new TermsValuesSourceBuilder(alias).field(field);
    }

    //获取es分组对象
    private TermsValuesSourceBuilder getTermsValuesSourceBuilder(AggGroup group) {
        TermsValuesSourceBuilder field = getTermsValuesSourceBuilder(group.getAlias(), group.getField());
        AggGroup.Order order = group.getOrder();
        //2是当前分组字段进行排序
        if (order != null && order.getTypeValue() == 2) {
            field.order(getSortOrder(order.isAsc()));
        }
        return field;
    }

    private TermsValuesSourceBuilder getTermsValuesSourceBuilder(String s, Boolean b) {
        TermsValuesSourceBuilder field = getTermsValuesSourceBuilder(s, s);
        if (b != null) {
            field.order(getSortOrder(b));
        }
        return field;
    }

    private SortOrder getSortOrder(Boolean b) {
        return b == null || b ? SortOrder.ASC : SortOrder.DESC;
    }

    //获取es分组对象
    private TermsAggregationBuilder getTermsBuilder(AggGroup group, int defaultMaxSize, int total) {
        int size = getMaxSize(group.getSize(), defaultMaxSize);
        size = getMaxSize(total, size);
        return getTermsBuilder(group.getField(), group.getAlias(), size, group.getOrder(), group.getIncludeFields(), group.getMissingValue());
    }

    //获取es分组对象
    private TermsAggregationBuilder getTermsBuilder(String field, String alias, int size, AggGroup.Order order, String[] includeFields, Object missingValue) {
        TermsAggregationBuilder terms = AggregationBuilders.terms(alias).field(field).size(size);
        if (missingValue != null) {
            terms.missing(missingValue);
        }
        setTermsOrder(terms, order);
        setTermsIncludeExclude(terms, includeFields);
        return terms;
    }

    //获取分组後的值,并返回子聚合集合
    private List<Aggregation> getGroupValue(Map<String, Object> map, String name, List<? extends MultiBucketsAggregation.Bucket> buckets, int i) {
        MultiBucketsAggregation.Bucket bucket = buckets.get(i);
        map.put(name, bucket.getKey());
        return bucket.getAggregations().asList();
    }

    //判断是否已经等于或者超过总数了
    private boolean getGroupAggregateIsOver(MulFieldsGroupAgg mulFieldsGroupAgg, int total) {
        //已经获取到总条数了
        if (++mulFieldsGroupAgg.currentIndex >= total && total > 0) {
            mulFieldsGroupAgg.over = true;
        }
        return mulFieldsGroupAgg.over;
    }

    //判断是否已经等于或者超过总数了
    private boolean getGroupAggregateIsOver(MulFieldsGroupAgg mulFieldsGroupAgg) {
        //已经获取到总条数了
        if (mulFieldsGroupAgg.getSize() >= mulFieldsGroupAgg.total && mulFieldsGroupAgg.total > 0) {
            mulFieldsGroupAgg.over = true;
        }
        return mulFieldsGroupAgg.over;
    }

    private long getAggregationsTotal(AggBuilderService aggBuilder, List<Aggregation> aggregations) {
        Aggregation aggregation;
        switch (aggBuilder.getType()) {
            case SINGLE_GROUP:
            case MULTIPLE_GROUP:
            case SINGLE_GROUP_AGGREGATE:
            case MULTIPLE_GROUP_AGGREGATE:
            case NESTED:
            case REVERSE_NESTED:
            case FILTER:
            case DATE:
                aggregation = getAggregation(aggregations, aggBuilder, false);
                if (aggregation != null) {
                    return getAggregationsTotal(aggregation);
                }
                break;
            case AGGREGATE:
            default:
                EsUtils.throwIllegalArgumentException(aggBuilder.getType());
        }
        return 0;
    }

    private long getAggregationsTotal(Aggregation aggregation) {
        if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
            return 0;
        } else if (aggregation instanceof Percentiles) {//这里获取中值
            return 0;
        } else if (aggregation instanceof HasAggregations) {//这里Nested/ReverseNested响应结果
            HasAggregations nested = (HasAggregations) aggregation;
            for (Aggregation agg : nested.getAggregations()) {
                return getAggregationsTotal(agg);
            }
        } else if (aggregation instanceof Terms) {
            Terms terms = (Terms) aggregation;
            long total = 0;
            for (Terms.Bucket bucket : terms.getBuckets()) {
                List<Aggregation> aggregations = bucket.getAggregations().asList();
                if (CollectionUtils.isEmpty(aggregations)) {
                    total += 1;
                } else {
                    int totalCur = 0;
                    for (Aggregation agg : bucket.getAggregations()) {
                        totalCur += getAggregationsTotal(agg);
                    }
                    if (totalCur < 1) {
                        totalCur = 1;
                    }
                    total += totalCur;
                }
            }
            return total;
        } else if (aggregation instanceof CompositeAggregation) {//复合分组聚合
            CompositeAggregation multiBucketsAggregation = (CompositeAggregation) aggregation;
            return multiBucketsAggregation.getBuckets().size();
        } else if (aggregation instanceof MultiBucketsAggregation) {//時间分组聚合
            MultiBucketsAggregation multiBucketsAggregation = (MultiBucketsAggregation) aggregation;
            return multiBucketsAggregation.getBuckets().size();
        }
        return 0;
    }

    /**
     * 其它
     */
    //获取范围间较大的数字【max必须>0,size任意值,得到value取值范围:0<=value<=max】
    private int getMaxSize(int size, int max) {
        return size > max || size < 0 ? max : size;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////树结构///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void addSubAggregation(List<AggBuilderService> aggBuilderServices, int total, AggregationBuilder aggregationBuilder) {
        for (AggBuilderService aggBuilderService : aggBuilderServices) {
            AbstractAggregationBuilder abstractAggregationBuilder = getAbstractAggregationBuilder(total, aggBuilderService);
            aggregationBuilder.subAggregation(abstractAggregationBuilder);
        }
    }

    private <T> void getResultList(List<Object> result, List<T> list, MulFieldsGroupAgg mulFieldsGroupAgg) {
        int size = getMaxSize(mulFieldsGroupAgg.total, list.size());
        List<T> subList = getSubList(list, mulFieldsGroupAgg.skip, size);
        if (CollectionUtils.isEmpty(subList)) {
            result.add(null);
        } else {
            result.add(subList);
        }
    }

    private <T> List<T> getSubList(List<T> list, int startIndex, int endIndex) {
        if (startIndex > 0) {
            if (list.size() > startIndex) {
                return list.subList(startIndex, endIndex);
            }
            return null;
        }
        return list;
    }

    /**
     * @author: 王坤造
     * @date: 2018/8/7 11:06
     * @comment: Instant对象转化为時间戳
     * @return:
     * @notes:
     */
    private long getTimestamp(Instant instant) {
        return instant.toEpochMilli();
    }

    private List<Query> conversionQuery(Object queries) {
        if (queries instanceof Query) {
            return Collections.singletonList((Query) queries);
        }
        return (List<Query>) queries;
    }

    private void operFuncByQueryNew(String index, Function<List<Map<String, Object>>, Boolean> func, LinkedHashMap<String, Boolean> orders, Object queries, String... showFields) {
        EsUtils.judgeIterNotNullAndKNotNull("orders", orders);
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getSorts(orders, searchSourceBuilder);
        searchSourceBuilder.size(SCROLL_SIZE);
        getQuery(index, conversionQuery(queries), searchSourceBuilder, true, showFields);
        EsUtils.setFetchSource(searchSourceBuilder, showFields);
        String[] searchField = toMyArray(orders.keySet());
        int lastIndex = SCROLL_SIZE - 1;
        while (true) {
            List<Map<String, Object>> result = EsUtils.operSearch1(index, null, searchSourceBuilder);
            if (CollectionUtils.isNotEmpty(result)) {
                boolean noEnd = result.size() % SCROLL_SIZE < 1;
                if (noEnd) {
                    Map<String, Object> d = result.get(lastIndex);
                    searchSourceBuilder.searchAfter(Arrays.stream(searchField).map(d::get).toArray());
                }
                Boolean isInterrupt = func.apply(result);
                if ((isInterrupt == null || !isInterrupt) && noEnd) {
                    continue;
                }
            }
            break;
        }
    }

    private List<Map<String, Object>> getListAll(String index, Object queries, String... showFields) {
        List<Map<String, Object>> list = new ArrayList<>();
        operFuncByQueryNew(index, o2 -> !list.addAll(o2), MAP_ORDER_ESID, queries, showFields);
        return list;
    }

    @Override
    public boolean treeAdd(String index, TreeCube tc) {
        Map<String, Object> data = tc.getData();
        String uniqueESID = tc.getUniqueESID();
        String temp2 = tc.getTemp2();
        List<String> childrenESIDs = tc.getChildrenESIDs();
        TreeCube parentTreeCube = tc.getParentTreeCube();
        List<TreeCube> childrenTreeCubes = tc.getChildrenTreeCubes();
        if (StringUtils.isEmpty(index) || MapUtils.isEmpty(data) || StringUtils.isEmpty((String) data.get(ESID_FIELD)) || StringUtils.isNotEmpty(uniqueESID) || StringUtils.isNotEmpty(temp2) || CollectionUtils.isNotEmpty(childrenESIDs) || parentTreeCube != null || CollectionUtils.isNotEmpty(childrenTreeCubes)) {
            EsUtils.throwIllegalArgumentException("index禁止为空", "tc.getData()禁止为空", "esid禁止为空", "tc.getUniqueESID()必须为空", "tc.getChildrenESIDs()必须为空", "tc.getParentTreeCube()必须为空", "tc.getChildrenTreeCubes()必须为空");
        }
        logInfo("es增删改", "treeAdd", index, tc);
        String index1 = index + INDEX_TREE_DATA_SUFFIX;
        String indexTreeData = getNewIndex(index1);
        String index2 = index + INDEX_TREE_RELATIONSHIP_SUFFIX;
        String indexTreeRelationship = getNewIndex(index2);
        String parentESID = tc.getParentESID();
        String childrenESID = tc.getTemp1();
        //父节点存储的子节点esid
        List<String> currentParentChiESIDs = null;
        //1:根节点无子节点,2:根节点当且仅当1个子节点,3:分支结点(非根结点),4:叶节点
        int operType = tc.getOperType();

        //region 判断操作
        switch (operType) {
            case 1:
                //region 根节点无子节点
                if (StringUtils.isNotEmpty(parentESID) || StringUtils.isNotEmpty(childrenESID)) {
                    EsUtils.throwIllegalArgumentException("当新增根结点且无子结点时,tc.getParentESID(),tc.getTemp1()必须为空!!!");
                }
                uniqueESID = EsUtils.getESID();
                //endregion
                break;
            case 2:
                //region 根节点当且仅当1个子节点
                if (StringUtils.isNotEmpty(parentESID) || StringUtils.isEmpty(childrenESID)) {
                    EsUtils.throwIllegalArgumentException("当新增根结点当且仅当1个子节点时,tc.getParentESID()必须为空!!!tc.getTemp1()禁止为空!!!");
                }
                //根节点当且仅当1个子节点,查询子节点是否存在
                Map<String, Object> map2 = getOne(indexTreeRelationship, childrenESID, FIELD_UNIQUE_ESID);
                if (MapUtils.isEmpty(map2)) {
                    EsUtils.throwIllegalArgumentException("当新增根结点当且仅当1个子节点时,子节点必须存在!!!");
                }
                uniqueESID = map2.get(FIELD_UNIQUE_ESID).toString();
                //endregion
                break;
            case 3:
                //region 分支结点(非根结点)
                if (StringUtils.isEmpty(parentESID) || StringUtils.isEmpty(childrenESID)) {
                    EsUtils.throwIllegalArgumentException("当新增结点为分支结点时,tc.getParentESID(),tc.getTemp1()禁止为空!!!");
                }
                //分支结点(非根结点),查询判断子节点在父节点中是否存在的uniqueESID一致
                Map<String, Object> map3 = getOne(indexTreeRelationship, parentESID, FIELD_UNIQUE_ESID, FIELD_CHILDREN_ESIDS);
                if (MapUtils.isEmpty(map3)) {
                    EsUtils.throwIllegalArgumentException("当新增结点为分支结点时,父节点必须存在!!!");
                }
                currentParentChiESIDs = (List<String>) map3.get(FIELD_CHILDREN_ESIDS);
                if (CollectionUtils.isEmpty(currentParentChiESIDs)) {
                    EsUtils.throwIllegalArgumentException("当新增结点为分支结点时,父节点中至少存在1个子节点!!!");
                } else {
                    if (currentParentChiESIDs.contains(childrenESID)) {
                        currentParentChiESIDs.remove(childrenESID);
                    } else {
                        EsUtils.throwIllegalArgumentException("当新增结点为分支结点时,父子节点关系必须存在!!!");
                    }
                }
                uniqueESID = map3.get(FIELD_UNIQUE_ESID).toString();
                //endregion
                break;
            case 4:
                //region 叶节点
                if (StringUtils.isEmpty(parentESID) || StringUtils.isNotEmpty(childrenESID)) {
                    EsUtils.throwIllegalArgumentException("当新增结点为叶结点时,tc.parentESID()禁止为空!!!tc.childrenESID()必须为空!!!");
                }
                Map<String, Object> map4 = getOne(indexTreeRelationship, parentESID, FIELD_UNIQUE_ESID, FIELD_CHILDREN_ESIDS);
                if (MapUtils.isEmpty(map4)) {
                    EsUtils.throwIllegalArgumentException("当新增结点为叶结点时,父节点必须存在!!!");
                }
                currentParentChiESIDs = (List<String>) map4.get(FIELD_CHILDREN_ESIDS);
                if (CollectionUtils.isEmpty(currentParentChiESIDs)) {
                    currentParentChiESIDs = new ArrayList<>(1);
                }
                uniqueESID = map4.get(FIELD_UNIQUE_ESID).toString();
                //endregion
                break;
            default:
                EsUtils.throwIllegalArgumentException("operType错误!");
        }
        //endregion

        //region 新增数据
        String esid = EsUtils.operIndex(indexTreeData, index1, data, null, WriteRequest.RefreshPolicy.IMMEDIATE, DocWriteRequest.OpType.CREATE).first;
        if (StringUtils.isEmpty(esid)) {
            return false;
        }
        //endregion

        return addRelationship(indexTreeData, index1, indexTreeRelationship, index2, esid, uniqueESID, parentESID, childrenESID, currentParentChiESIDs, operType);
    }

    @Override
    public Page<Map<String, Object>> treeGetRoots(String index, List<Query> queries, Page<Map<String, Object>> page, String... showFields) {
        if (StringUtils.isEmpty(index)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "uniqueESID禁止为空");
        }
        String indexTreeData = getNewIndex(index + INDEX_TREE_DATA_SUFFIX);
        String indexTreeRelationship = getNewIndex(index + INDEX_TREE_RELATIONSHIP_SUFFIX);

        List<Map<String, Object>> esids = getList(indexTreeRelationship, Collections.singletonList(new Query(QueryType.EQ, FIELD_PARENT_ESID, NULL_VALUE)), null, null, EsUtils.MAX_SIZE_PAGE, FIELD_UNIQUE_ESID);
        if (CollectionUtils.isEmpty(esids)) {
            page.setItemList(Collections.EMPTY_LIST);
            page.setTotalCount(0L);
            return page;
        }
        Query q = new Query(QueryType.IN, ESID_FIELD, esids.stream().map(o -> o.get(ESID_FIELD)).toArray());
        if (CollectionUtils.isEmpty(queries)) {
            queries = Collections.singletonList(q);
        } else {
            queries.add(q);
        }
        getPage(indexTreeData, queries, page, showFields);
        if (CollectionUtils.isNotEmpty(page.getItemList())) {
            page.getItemList().forEach(o -> {
                Optional<Map<String, Object>> one = esids.stream().filter(o2 -> Objects.equals(o.get(ESID_FIELD), o2.get(ESID_FIELD))).findAny();
                one.ifPresent(stringObjectMap -> o.put(FIELD_UNIQUE_ESID, stringObjectMap.get(FIELD_UNIQUE_ESID)));
            });
        }
        return page;
    }

    @Override
    public Page<Map<String, Object>> treeGetRootsAudit(String index, Page<Map<String, Object>> page) {
        if (StringUtils.isEmpty(index)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "uniqueESID禁止为空");
        }

        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_INDEX_NAME, index));
        queries.add(new Query(QueryType.EQ, FIELD_PARENT_ESID, NULL_VALUE));
        getPage(INDEX_AUDIT_TREE, queries, page, FIELD_UNIQUE_ESID, FIELD_CONTENT);

        if (CollectionUtils.isNotEmpty(page.getItemList())) {
            page.getItemList().forEach(o -> Optional.ofNullable((Map<String, Object>) o.get(FIELD_CONTENT)).ifPresent(o::putAll));
        }
        return page;
    }

    @Override
    public TreeCube treeGetAll(String index, String uniqueESID, String... showFields) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(uniqueESID)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "uniqueESID禁止为空");
        }
        String indexTreeData = getNewIndex(index + INDEX_TREE_DATA_SUFFIX);
        String indexTreeRelationship = getNewIndex(index + INDEX_TREE_RELATIONSHIP_SUFFIX);
        //获取根结点
        Map<String, Object> mapRelationship = getOne(indexTreeRelationship, Collections.singletonList(new Query(true, new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID), new Query(QueryType.EQ, FIELD_PARENT_ESID, NULL_VALUE))), FIELD_CHILDREN_ESIDS);
        if (MapUtils.isEmpty(mapRelationship)) {
            return null;
        }
        return getTreeCube(indexTreeData, indexTreeRelationship, uniqueESID, mapRelationship, showFields);

    }

    @Override
    public TreeCube treeGetAllAudit(String index, String uniqueESID) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(uniqueESID)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "uniqueESID禁止为空");
        }

        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_INDEX_NAME, index));
        queries.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        queries.add(new Query(QueryType.EQ, FIELD_PARENT_ESID, NULL_VALUE));
        Map<String, Object> mapRelationship = getOne(INDEX_AUDIT_TREE, queries, FIELD_CHILDREN_ESIDS, FIELD_CONTENT);
        if (MapUtils.isEmpty(mapRelationship)) {
            return null;
        }

        ArrayList<Query> queries2 = new ArrayList<>();
        queries2.add(new Query(QueryType.EQ, FIELD_INDEX_NAME, index));
        queries2.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        queries2.add(new Query(QueryType.NE, ESID_FIELD, mapRelationship.get(ESID_FIELD)));
        List<Map<String, Object>> listRelationship = getList(INDEX_AUDIT_TREE, queries2, null, null, EsUtils.MAX_PAGE_SIZE, FIELD_CHILDREN_ESIDS, FIELD_CONTENT);
        List<Map<String, Object>> listData = listRelationship.stream().map(o -> Optional.ofNullable((Map<String, Object>) o.remove(FIELD_CONTENT)).map(content -> {
            content.put(ESID_FIELD, o.get(ESID_FIELD));
            return content;
        }).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, Object> firstD = (Map<String, Object>) mapRelationship.remove(FIELD_CONTENT);
        if (firstD == null) {
            firstD = new HashMap<>();
        }
        firstD.put(ESID_FIELD, mapRelationship.get(ESID_FIELD));
        listData.add(firstD);
        return getAllTreesCore(null, null, mapRelationship, listRelationship, listData);
    }

    private TreeCube getTreeCube(String indexTreeData, String indexTreeRelationship, String uniqueESID, Map<String, Object> mapRelationship, String[] showFields) {
        //获取其它结点
        List<Map<String, Object>> listRelationship = getListAll(indexTreeRelationship, new Query(true, new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID), new Query(QueryType.NE, ESID_FIELD, mapRelationship.get(ESID_FIELD))), FIELD_CHILDREN_ESIDS);
        String[] esids;
        if (CollectionUtils.isEmpty(listRelationship)) {
            esids = new String[]{(String) mapRelationship.get(ESID_FIELD)};
        } else {
            List<String> collect = listRelationship.stream().map(o -> (String) o.get(ESID_FIELD)).collect(Collectors.toList());
            collect.add((String) mapRelationship.get(ESID_FIELD));
            esids = toMyArray(collect);
        }
        //获取所有结点数据
        List<Map<String, Object>> listData;
        if (esids.length > SCROLL_SIZE) {
            listData = new ArrayList<>();
            int length = esids.length;
            int from = 0;
            int to = SCROLL_SIZE;
            boolean b = false;
            while (true) {
                if (to >= length) {
                    to = length;
                    b = true;
                }
                //Object[] arr=collect.subList(from,to).toArray();
                String[] arr = Arrays.copyOfRange(esids, from, to);
                List<Map<String, Object>> list = getList(indexTreeData, Collections.singletonList(new Query(QueryType.IN, ESID_FIELD, arr)), null, null, SCROLL_SIZE, showFields);
                if (CollectionUtils.isNotEmpty(list)) {
                    listData.addAll(list);
                }
                if (b) {
                    break;
                }
                from = to;
                to += SCROLL_SIZE;
            }
        } else {
            listData = getList(indexTreeData, Collections.singletonList(new Query(QueryType.IN, ESID_FIELD, esids)), null, null, esids.length, showFields);
        }
        //System.out.println(esids.length);
        //System.out.println(listData.size());
        //System.exit(0);
        TreeCube tc = getAllTreesCore(null, null, mapRelationship, listRelationship, listData);
        return tc;
    }

    String[] toMyArray(Collection<String> coll) {
        return coll.toArray(new String[0]);
    }

    @Override
    public List<TreeCube> treeGetTrees(String index, List<Query> queries, String... showFields) {
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(queries)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "queries禁止为空");
        }
        String indexTreeData = getNewIndex(index + INDEX_TREE_DATA_SUFFIX);
        String indexTreeRelationship = getNewIndex(index + INDEX_TREE_RELATIONSHIP_SUFFIX);

        List<Map<String, Object>> esidMap = getList(indexTreeData, queries, null, null, SCROLL_SIZE, NULL_VALUE);
        if (CollectionUtils.isEmpty(esidMap)) {
            return null;
        }

        Object[] esidArr = esidMap.stream().map(o -> o.get(ESID_FIELD)).toArray();
        List<String> uniqueList = (List<String>) getAggregations(indexTreeRelationship, Collections.singletonList(new Query(QueryType.IN, ESID_FIELD, esidArr)), esidArr.length, AggGroup.getInstance(FIELD_UNIQUE_ESID));
        if (CollectionUtils.isEmpty(uniqueList)) {
            return null;
        }
        Object[] uniqueArr = uniqueList.toArray();
        List<Map<String, Object>> listmapRelationship = getList(indexTreeRelationship, Collections.singletonList(new Query(true, new Query(QueryType.IN, FIELD_UNIQUE_ESID, uniqueArr), new Query(QueryType.EQ, FIELD_PARENT_ESID, NULL_VALUE))), null, null, uniqueArr.length, FIELD_CHILDREN_ESIDS, FIELD_UNIQUE_ESID);
        if (CollectionUtils.isEmpty(listmapRelationship)) {
            return null;
        }
        List<TreeCube> list = listmapRelationship.stream().map(o -> getTreeCube(indexTreeData, indexTreeRelationship, (String) o.remove(FIELD_UNIQUE_ESID), o, showFields)).collect(Collectors.toList());
        System.out.println(list.size());
        return list;
    }

    @Override
    public void treeDeleteAll(String index, String uniqueESID) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(uniqueESID)) {
            EsUtils.throwIllegalArgumentException("index禁止为空.", "uniqueESID禁止为空");
        }
        logInfo("es增删改", "treeDeleteAll", index, uniqueESID);
        String index1 = index + INDEX_TREE_DATA_SUFFIX;
        String indexTreeData = getNewIndex(index1);
        String index2 = index + INDEX_TREE_RELATIONSHIP_SUFFIX;
        String indexTreeRelationship = getNewIndex(index2);

        boolean stop = true;
        while (stop) {
            List<Map<String, Object>> listRelationship = getList(indexTreeRelationship, Collections.singletonList(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID)), null, null, EsUtils.MAX_SIZE_PAGE, NULL_VALUE);
            if (CollectionUtils.isEmpty(listRelationship)) {
                EsUtils.throwIllegalArgumentException("删除所有节点时,uniqueESID必须存在!!!");
            }
            if (listRelationship.size() < EsUtils.MAX_SIZE_PAGE) {
                stop = false;
            }
            List<String> esids1 = listRelationship.stream().map(o -> (String) o.get(EsUtils.ESID_FIELD)).collect(Collectors.toList());
            List<DocWriteRequest> l1 = esids1.stream().map(o -> EsUtils.getDeleteRequest(indexTreeRelationship, index2, o, null)).collect(Collectors.toList());
            List<DocWriteRequest> l2 = esids1.stream().map(o -> EsUtils.getDeleteRequest(indexTreeData, index1, o, null)).collect(Collectors.toList());
            l1.addAll(l2);
            BulkResponse response1 = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, l1);
            if (response1.hasFailures()) {
                EsUtils.throwRuntimeException(String.format("删除分支结点(非根结点)时,递归删除子节点有失败!!!\n%s", response1.buildFailureMessage()));
            }
        }
    }

    @Override
    public void treeSaveAll(String index, String uniqueESID, TreeCube tcAfter, String... topicFields) {
        logInfo("es增删改", "treeSaveAll", index, uniqueESID, tcAfter, topicFields);
        String index1 = index + INDEX_TREE_DATA_SUFFIX;
        String indexTreeData = getNewIndex(index1);
        String index2 = index + INDEX_TREE_RELATIONSHIP_SUFFIX;
        String indexTreeRelationship = getNewIndex(index2);

        Map<String, TreeCube> mapAfterTC = new HashMap<>();
        Map<String, String> mapAfterParentESID = new HashMap<>();
        Set<String> esidsAdd = new HashSet<>();
        Set<String> esidsDelete = new HashSet<>();
        Set<String> esidsUpdateRelationship = new HashSet<>();
        Set<String> esidsUpdateData = new HashSet<>();

        TreeCube tcBefore = treeGetAll(index, uniqueESID, topicFields);
        if (tcBefore == null) {
            EsUtils.throwRuntimeException(String.format("%s索引中不存在%s的uniqueESID!", index, uniqueESID));
        }

        String esidBefore = tcBefore.getESID();
        String esidAfter = tcAfter.getESID();
        if (!esidBefore.equals(esidAfter)) {
            esidsUpdateRelationship.add(esidAfter);
        }
        getESIDList(esidBefore, tcBefore, tcAfter, esidsDelete, null, null, null, null, null);
        getESIDList(tcAfter.getESID(), tcAfter, tcBefore, esidsAdd, esidsUpdateRelationship, esidsUpdateData, mapAfterTC, mapAfterParentESID, topicFields);

        List<DocWriteRequest> listDocWriteRequest = new ArrayList<>();
        Set<String> esidsRelationshipOper = new HashSet<>();
        //region esidsAdd
        if (CollectionUtils.isNotEmpty(esidsAdd)) {
            for (String esid : esidsAdd) {
                esidsRelationshipOper.add(esid);
                TreeCube tc = mapAfterTC.get(esid);

                Map<String, Object> map = getHashMap(3);
                map.put(FIELD_UNIQUE_ESID, uniqueESID);
                List<String> childrenESIDs = tc.getChildrenESIDs();
                if (CollectionUtils.isNotEmpty(childrenESIDs)) {
                    map.put(FIELD_CHILDREN_ESIDS, childrenESIDs);
                }
                String esidParent = mapAfterParentESID.get(esid);
                if (StringUtils.isNotEmpty(esidParent)) {
                    map.put(FIELD_PARENT_ESID, esidParent);
                }
                DocWriteRequest request1 = EsUtils.getIndexRequest(indexTreeData, index1, tc.getData(), esid, null, null);
                DocWriteRequest request2 = EsUtils.getIndexRequest(indexTreeRelationship, index2, map, esid, null, null);

                listDocWriteRequest.add(request1);
                listDocWriteRequest.add(request2);
            }
        }
        //endregion
        //region esidsDelete
        if (CollectionUtils.isNotEmpty(esidsDelete)) {
            for (String esid : esidsDelete) {
                esidsRelationshipOper.add(esid);

                DocWriteRequest request1 = EsUtils.getDeleteRequest(indexTreeData, index1, esid, null);
                DocWriteRequest request2 = EsUtils.getDeleteRequest(indexTreeRelationship, index2, esid, null);
                listDocWriteRequest.add(request1);
                listDocWriteRequest.add(request2);
            }
        }
        //endregion
        //region esidsUpdateRelationship
        if (CollectionUtils.isNotEmpty(esidsUpdateRelationship)) {
            for (String esid : esidsUpdateRelationship) {
                if (!esidsRelationshipOper.contains(esid)) {
                    TreeCube tc = mapAfterTC.get(esid);

                    Map<String, Object> map = getHashMap(2);
                    List<String> childrenESIDs = tc.getChildrenESIDs();
                    if (CollectionUtils.isEmpty(childrenESIDs)) {
                        map.put(FIELD_CHILDREN_ESIDS, null);
                    } else {
                        map.put(FIELD_CHILDREN_ESIDS, childrenESIDs);
                    }
                    String esidParent = mapAfterParentESID.get(esid);
                    if (StringUtils.isEmpty(esidParent)) {
                        map.put(FIELD_PARENT_ESID, null);
                    } else {
                        map.put(FIELD_PARENT_ESID, esidParent);
                    }
                    if (MapUtils.isEmpty(map)) {
                        EsUtils.throwRuntimeException(String.format("修改关系的map禁止为空,esid:%s", esid));
                    }
                    DocWriteRequest request1 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map, esid, null, false);
                    listDocWriteRequest.add(request1);
                }
            }
        }
        //endregion
        //region esidsUpdateData
        if (CollectionUtils.isNotEmpty(esidsUpdateData)) {
            for (String esid : esidsUpdateData) {
                if (!esidsRelationshipOper.contains(esid)) {
                    Map<String, Object> map = getHashMap(topicFields.length);
                    Map<String, Object> data = mapAfterTC.get(esid).getData();
                    Arrays.stream(topicFields).forEach(o -> map.put(o, data.get(o)));
                    DocWriteRequest request1 = EsUtils.getUpdateRequest(indexTreeData, index1, map, esid, null, false);
                    listDocWriteRequest.add(request1);
                }
            }
        }
        //endregion
        if (CollectionUtils.isEmpty(listDocWriteRequest)) {
            return;
        }
        BulkResponse response = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, listDocWriteRequest);
        if (response.hasFailures()) {
            BulkItemResponse[] items = response.getItems();
            for (int i = items.length - 1; i > -1; i--) {
                if (items[i].isFailed()) {
                    listDocWriteRequest.remove(i);
                }
            }
            response = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, listDocWriteRequest);
            if (response.hasFailures()) {
                StringBuilder sb = new StringBuilder();
                items = response.getItems();
                for (int i = 0, size = items.length; i < size; i++) {
                    if (items[i].isFailed()) {
                        sb.append(listDocWriteRequest.get(i)).append(System.lineSeparator());
                    }
                }
                EsUtils.throwRuntimeException(String.format("tree保存操作部分失败!%s%s", System.lineSeparator(), sb));
            }
        }
    }

    @Override
    public boolean treeSaveAllAudit(String index, String uniqueESID, TreeCube tcAfter, String... topicFields) {
        if (ArrayUtils.contains(topicFields, ESID_FIELD)) {
            EsUtils.throwRuntimeException("topicFields禁止存在esid字段!");
        }
        logInfo("es增删改", "treeSaveAllAudit", index, uniqueESID, tcAfter, topicFields);
        long count = getCount(INDEX_AUDIT_TREE, Collections.singletonList(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID)));
        if (count > 0) {
            return false;
        }

        String index1 = index + INDEX_TREE_DATA_SUFFIX;
        String indexTreeData = getNewIndex(index1);
        String index2 = index + INDEX_TREE_RELATIONSHIP_SUFFIX;
        String indexTreeRelationship = getNewIndex(index2);

        Map<String, TreeCube> mapAfterTC = new HashMap<>();
        Map<String, String> mapAfterParentESID = new HashMap<>();
        Set<String> esidsAdd = new HashSet<>();
        Set<String> esidsDelete = new HashSet<>();
        Set<String> esidsUpdateRelationship = new HashSet<>();
        Set<String> esidsUpdateData = new HashSet<>();

        TreeCube tcBefore = treeGetAll(index, uniqueESID, topicFields);
        if (tcBefore == null) {
            EsUtils.throwRuntimeException(String.format("%s索引中不存在%s的uniqueESID!", index, uniqueESID));
        }

        long ts = System.currentTimeMillis();

        //region old
        ArrayList<Map<String, Object>> listNew = new ArrayList<>();
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        LinkedHashMap<String, Boolean> orders = new LinkedHashMap<>();
        orders.put(ESID_FIELD, true);
        Function<List<Map<String, Object>>, Boolean> func = o -> {
            listNew.addAll(o);
            Object[] esids = o.stream().map(o2 -> o2.get(ESID_FIELD)).toArray();
            List<Map<String, Object>> datas = getList(indexTreeData, Collections.singletonList(new Query(QueryType.IN, ESID_FIELD, esids)), null, null, SCROLL_SIZE, topicFields);

            if (CollectionUtils.isNotEmpty(datas)) {
                o.forEach(o2 -> {
                    Object esid = o2.get(ESID_FIELD);
                    Optional<Map<String, Object>> data = datas.stream().filter(o3 -> Objects.equals(esid, o3.get(ESID_FIELD))).findAny();
                    data.ifPresent(stringObjectMap -> {
                        stringObjectMap.remove(ESID_FIELD);
                        o2.put(FIELD_CONTENT, stringObjectMap);
                    });
                });
            }
            return null;
        };
        operFuncByQueryNew(indexTreeRelationship, func, orders, queries);
        //endregion

        String esidBefore = tcBefore.getESID();
        String esidAfter = tcAfter.getESID();
        if (!esidBefore.equals(esidAfter)) {
            esidsUpdateRelationship.add(esidAfter);
        }
        getESIDList(esidBefore, tcBefore, tcAfter, esidsDelete, null, null, null, null, null);
        getESIDList(tcAfter.getESID(), tcAfter, tcBefore, esidsAdd, esidsUpdateRelationship, esidsUpdateData, mapAfterTC, mapAfterParentESID, topicFields);

        List<Map<String, Object>> listOperate = new ArrayList<>();
        Set<String> esidsRelationshipOper = new HashSet<>();

        //region esidsAdd
        if (CollectionUtils.isNotEmpty(esidsAdd)) {
            esidsRelationshipOper.addAll(esidsAdd);
            for (String esid : esidsAdd) {
                TreeCube tc = mapAfterTC.get(esid);
                Map<String, Object> data = tc.getData();
                data.remove(ESID_FIELD);

                Map<String, Object> map = getHashMap(3);
                map.put(FIELD_UNIQUE_ESID, uniqueESID);
                List<String> childrenESIDs = tc.getChildrenESIDs();
                if (CollectionUtils.isNotEmpty(childrenESIDs)) {
                    map.put(FIELD_CHILDREN_ESIDS, childrenESIDs);
                }
                String esidParent = mapAfterParentESID.get(esid);
                if (StringUtils.isNotEmpty(esidParent)) {
                    map.put(FIELD_PARENT_ESID, esidParent);
                }

                HashMap<String, Object> map1 = new HashMap<>();
                map1.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_INDEX);
                map1.put(FIELD_INDEX_NAME, index1);
                map1.put(FIELD_CONTENT, data);
                map1.put(FIELD_ID, esid);
                listOperate.add(map1);

                HashMap<String, Object> map2 = new HashMap<>();
                map2.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_INDEX);
                map2.put(FIELD_INDEX_NAME, index2);
                map2.put(FIELD_CONTENT, map);
                map2.put(FIELD_ID, esid);
                listOperate.add(map2);


                HashMap<String, Object> d = new HashMap<>();
                d.putAll(map);
                d.put(ESID_FIELD, esid);
                d.put(FIELD_CONTENT, data);
                listNew.add(d);
            }
        }
        //endregion
        //region esidsDelete
        if (CollectionUtils.isNotEmpty(esidsDelete)) {
            esidsRelationshipOper.addAll(esidsDelete);
            esidsDelete.forEach(esid -> {
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_DELETE);
                map1.put(FIELD_INDEX_NAME, index1);
                map1.put(FIELD_ID, esid);
                listOperate.add(map1);

                HashMap<String, Object> map2 = new HashMap<>();
                map2.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_DELETE);
                map2.put(FIELD_INDEX_NAME, index2);
                map2.put(FIELD_ID, esid);
                listOperate.add(map2);
            });
            listNew.removeAll(listNew.stream().filter(o -> esidsDelete.contains(o.get(ESID_FIELD))).collect(Collectors.toList()));
        }
        //endregion
        //region esidsUpdateRelationship
        if (CollectionUtils.isNotEmpty(esidsUpdateRelationship)) {
            esidsUpdateRelationship.removeAll(esidsRelationshipOper);
            for (String esid : esidsUpdateRelationship) {
                TreeCube tcCurrentBefore = tcBefore.getCurrentOrChildren(esid);
                List<String> childrenESIDsBefore = tcCurrentBefore.getChildrenESIDs();
                if (CollectionUtils.isEmpty(childrenESIDsBefore)) {
                    childrenESIDsBefore = null;
                }
                String esidParentBefore = tcCurrentBefore.getParentESID();
                if (StringUtils.isEmpty(esidParentBefore)) {
                    esidParentBefore = null;
                }
                Map<String, Object> mapPre = getHashMap(2);
                mapPre.put(FIELD_CHILDREN_ESIDS, childrenESIDsBefore);
                mapPre.put(FIELD_PARENT_ESID, esidParentBefore);

                TreeCube tc = mapAfterTC.get(esid);
                List<String> childrenESIDs = tc.getChildrenESIDs();
                if (CollectionUtils.isEmpty(childrenESIDs)) {
                    childrenESIDs = null;
                }
                String esidParent = mapAfterParentESID.get(esid);
                if (StringUtils.isEmpty(esidParent)) {
                    esidParent = null;
                }
                Map<String, Object> map = getHashMap(2);
                map.put(FIELD_CHILDREN_ESIDS, childrenESIDs);
                map.put(FIELD_PARENT_ESID, esidParent);

                if (MapUtils.isEmpty(map)) {
                    EsUtils.throwRuntimeException(String.format("修改关系的map禁止为空,esid:%s", esid));
                }
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_UPDATE);
                map1.put(FIELD_INDEX_NAME, index2);
                map1.put(FIELD_CONTENT, map);
                map1.put(FIELD_CONTENT_PRE, mapPre);
                map1.put(FIELD_ID, esid);
                listOperate.add(map1);

                Optional<Map<String, Object>> d = listNew.stream().filter(o -> Objects.equals(esid, o.get(ESID_FIELD))).findAny();
                d.ifPresent(o -> o.putAll(map));
            }
        }
        //endregion
        //region esidsUpdateData
        if (CollectionUtils.isNotEmpty(esidsUpdateData)) {
            esidsUpdateData.removeAll(esidsRelationshipOper);
            for (String esid : esidsUpdateData) {
                TreeCube tcCurrentBefore = tcBefore.getCurrentOrChildren(esid);
                Map<String, Object> mapPre = Optional.ofNullable(tcCurrentBefore.getData()).map(o -> Arrays.stream(topicFields).collect(Collectors.toMap(Function.identity(), o::get))).orElse(null);

                Map<String, Object> data = mapAfterTC.get(esid).getData();
                Map<String, Object> map = Arrays.stream(topicFields).collect(Collectors.toMap(Function.identity(), data::get));

                HashMap<String, Object> map1 = new HashMap<>();
                map1.put(FIELD_OPERATE_NAME, VALUE_OPERATE_NAME_UPDATE);
                map1.put(FIELD_INDEX_NAME, index1);
                map1.put(FIELD_CONTENT, map);
                map1.put(FIELD_CONTENT_PRE, mapPre);
                map1.put(FIELD_ID, esid);
                listOperate.add(map1);

                Optional<Map<String, Object>> d = listNew.stream().filter(o -> Objects.equals(esid, o.get(ESID_FIELD))).findAny();
                d.ifPresent(o -> {
                    Map<Object, Object> content = (Map<Object, Object>) o.get(FIELD_CONTENT);
                    if (MapUtils.isEmpty(content)) {
                        o.put(FIELD_CONTENT, map);
                    } else {
                        content.putAll(map);
                    }
                });
            }
        }
        //endregion

        if (CollectionUtils.isNotEmpty(listNew)) {
            listNew.forEach(o -> {
                o.put(FIELD_INDEX_NAME, index);
                o.put(FIELD_CREATE_TIME, ts);
            });
        }
        insertOrReplaceBulk(INDEX_AUDIT_TREE, listNew);
        if (CollectionUtils.isNotEmpty(listOperate)) {
            listOperate.forEach(o -> {
                o.put(ESID_FIELD, EsUtils.getESID());
                o.put(FIELD_UNIQUE_ESID, uniqueESID);
                o.put(FIELD_CREATE_TIME, ts);
            });
        }
        insertOrReplaceBulk(INDEX_AUDIT_TREE_OPERATE_HISTORY, listOperate);
        return true;
    }

    @Override
    public void treeAuditPass(String uniqueESID, long createTime) {
        if (StringUtils.isEmpty(uniqueESID) || createTime < 0) {
            EsUtils.throwRuntimeException("uniqueESID禁止为空!create_time必须>0!");
        }
        LinkedHashMap<String, Boolean> orders = new LinkedHashMap<>();
        orders.put(ESID_FIELD, true);
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        queries.add(new Query(QueryType.EQ, FIELD_CREATE_TIME, createTime));
        List<DocWriteRequest> listDocWriteRequest = new ArrayList<>();
        Function<List<Map<String, Object>>, Boolean> func1 = o -> {
            o.forEach(o2 -> {
                String operateName = (String) o2.get(FIELD_OPERATE_NAME);
                String index = (String) o2.get(FIELD_INDEX_NAME);
                String index2 = getNewIndex(index);
                Map<String, Object> mapContent = (Map<String, Object>) o2.get(FIELD_CONTENT);
                String esid = (String) o2.get(FIELD_ID);
                DocWriteRequest doc;
                if (Objects.equals(VALUE_OPERATE_NAME_INDEX, operateName)) {
                    doc = EsUtils.getIndexRequest(index2, index, mapContent, esid, null, null);
                } else if (Objects.equals(VALUE_OPERATE_NAME_UPDATE, operateName)) {
                    doc = EsUtils.getUpdateRequest(index2, index, mapContent, esid, null, false);
                } else if (Objects.equals(VALUE_OPERATE_NAME_DELETE, operateName)) {
                    doc = EsUtils.getDeleteRequest(index2, index, esid, null);
                } else {
                    EsUtils.throwRuntimeException("operateName为空!");
                    doc = null;
                }
                listDocWriteRequest.add(doc);
            });
            return false;
        };
        String[] fields1 = {FIELD_ID, FIELD_INDEX_NAME, FIELD_OPERATE_NAME, FIELD_CONTENT};
        operFuncByQueryNew(INDEX_AUDIT_TREE_OPERATE_HISTORY, func1, orders, queries, fields1);
        treeAuditCancelCore(uniqueESID, createTime, listDocWriteRequest, "tree通过审核操作部分失败!");
    }

    @Override
    public void treeAuditCancel(String uniqueESID, long createTime) {
        if (StringUtils.isEmpty(uniqueESID) || createTime < 0) {
            EsUtils.throwRuntimeException("uniqueESID禁止为空!create_time必须>0!");
        }
        treeAuditCancelCore(uniqueESID, createTime, new ArrayList<>(), "tree取消审核操作部分失败!");
    }

    @Override
    public List<Map<String, Object>> treeGetAuditOperateHistory(String uniqueESID) {
        if (StringUtils.isEmpty(uniqueESID)) {
            EsUtils.throwRuntimeException("uniqueESID禁止为空!");
        }
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        Map<String, Object> aggregations = (Map<String, Object>) getAggregations(INDEX_AUDIT_TREE_OPERATE_HISTORY, queries, -1, AggAggregate.getInstance(FIELD_CREATE_TIME, MAX));
        if (MapUtils.isEmpty(aggregations)) {
            return null;
        }
        Object createTime = aggregations.get(FIELD_CREATE_TIME);
        queries.add(new Query(QueryType.EQ, FIELD_CREATE_TIME, createTime));
        List<Map<String, Object>> list = getList(INDEX_AUDIT_TREE_OPERATE_HISTORY, queries, null, null, 1000, treeGetAuditOperateHistoryFields);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        list.forEach(o -> o.put(FIELD_CREATE_TIME, createTime));
        return list;
    }

    private void treeAuditCancelCore(String uniqueESID, long createTime, List<DocWriteRequest> listDocWriteRequest, String msg) {
        Function<List<Map<String, Object>>, Boolean> func2 = o -> {
            o.forEach(o2 -> listDocWriteRequest.add(EsUtils.getDeleteRequest(INDEX_AUDIT_TREE_ALIAS, INDEX_AUDIT_TREE, (String) o2.get(ESID_FIELD), null)));
            return false;
        };
        LinkedHashMap<String, Boolean> orders = new LinkedHashMap<>();
        orders.put(ESID_FIELD, true);
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new Query(QueryType.EQ, FIELD_UNIQUE_ESID, uniqueESID));
        queries.add(new Query(QueryType.EQ, FIELD_CREATE_TIME, createTime));
        String[] fields2 = {NULL_VALUE};
        operFuncByQueryNew(INDEX_AUDIT_TREE, func2, orders, queries, fields2);
        if (CollectionUtils.isEmpty(listDocWriteRequest)) {
            return;
        }
        BulkResponse response = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, listDocWriteRequest);
        if (response.hasFailures()) {
            BulkItemResponse[] items = response.getItems();
            for (int i = items.length - 1; i > -1; i--) {
                if (items[i].isFailed()) {
                    listDocWriteRequest.remove(i);
                }
            }
            response = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, listDocWriteRequest);
            if (response.hasFailures()) {
                StringBuilder sb = new StringBuilder();
                items = response.getItems();
                for (int i = 0, size = items.length; i < size; i++) {
                    if (items[i].isFailed()) {
                        sb.append(listDocWriteRequest.get(i)).append(System.lineSeparator());
                    }
                }
                EsUtils.throwRuntimeException(String.format("%s%s%s", msg, System.lineSeparator(), sb));
            }
        }
    }

    /**
     * 新增关系
     */
    private boolean addRelationship(String indexTreeData, String index, String indexTreeRelationship, String index2, String esid, String uniqueESID, String parentESID, String childrenESID, List<String> currentParentChiESIDs, int operType) {
        switch (operType) {
            case 1:
                //region 根节点无子节点
                HashMap<String, Object> map1 = new HashMap<String, Object>() {{
                    put(ESID_FIELD, esid);
                }};
                map1.put(FIELD_UNIQUE_ESID, uniqueESID);
                try {
                    String s = EsUtils.operIndex(indexTreeRelationship, index2, map1, null, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                    if (StringUtils.isEmpty(s)) {
                        EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                        return false;
                    }
                } catch (Exception e) {
                    EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                    throw e;
                }
                //endregion
                break;
            case 2:
                //region 根节点当且仅当1个子节点
                //新增
                HashMap<String, Object> map21 = new HashMap<String, Object>() {{
                    put(FIELD_CHILDREN_ESIDS, Collections.singletonList(childrenESID));
                }};
                map21.put(FIELD_UNIQUE_ESID, uniqueESID);
                //修改子
                HashMap<String, Object> map22 = new HashMap<String, Object>() {{
                    put(FIELD_PARENT_ESID, esid);
                }};
                IndexRequest indexRequest21 = EsUtils.getIndexRequest(indexTreeRelationship, index2, map21, esid, null, null);
                UpdateRequest updateRequest22 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map22, childrenESID, null, true);
                BulkResponse response2;
                try {
                    response2 = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, indexRequest21, updateRequest22);
                } catch (Exception e) {
                    EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                    throw e;
                }
                if (response2.hasFailures()) {
                    BulkItemResponse[] items = response2.getItems();
                    boolean b1 = true;
                    boolean b2 = true;
                    if (items[0].isFailed()) {
                        b1 = false;
                        try {
                            for (int i = 0; i < 3; i++) {
                                String s = EsUtils.operIndex(indexTreeRelationship, index2, map21, esid, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                if (StringUtils.isNotEmpty(s)) {
                                    b1 = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(String.format("es报错:operIndex(indexTreeRelationship, index2, map21, esid),%s|%s|%s|%s", indexTreeRelationship, index2, map21, esid), e);
                        }
                    }
                    if (items[1].isFailed()) {
                        b2 = false;
                        try {
                            for (int i = 0; i < 3; i++) {
                                b2 = EsUtils.operUpdate(indexTreeRelationship, index2, map22, childrenESID, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                if (b2) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(String.format("es报错:operUpdate(indexTreeRelationship, index2, map22, childrenESID),%s|%s|%s|%s", indexTreeRelationship, index2, map22, childrenESID), e);
                        }
                    }
                    if (!b1 && !b2) {
                        EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                        return false;
                    } else if (!b1) {
                        map21.put(ESID_FIELD, esid);
                        EsUtils.throwRuntimeException(String.format("存储失败:关系建立失败!请手动在%s索引新增%s数据", index2, map21));
                    } else if (!b2) {
                        map22.put(ESID_FIELD, childrenESID);
                        EsUtils.throwRuntimeException(String.format("存储失败:关系建立失败!请手动在%s索引更新%s数据", index2, map22));
                    }
                }
                //endregion
                break;
            case 3:
                //region 分支结点(非根结点)
                //新增
                HashMap<String, Object> map31 = new HashMap<String, Object>() {{
                    put(FIELD_PARENT_ESID, parentESID);
                    put(FIELD_CHILDREN_ESIDS, Collections.singletonList(childrenESID));
                }};
                if (StringUtils.isNotEmpty(uniqueESID)) {
                    map31.put(FIELD_UNIQUE_ESID, uniqueESID);
                }
                //修改子
                HashMap<String, Object> map32 = new HashMap<String, Object>() {{
                    put(FIELD_PARENT_ESID, esid);
                }};
                //修改父
                HashMap<String, Object> map33 = new HashMap<>();
                currentParentChiESIDs.add(esid);
                map33.put(FIELD_CHILDREN_ESIDS, currentParentChiESIDs);
                DocWriteRequest indexRequest31;
                if (StringUtils.isEmpty(uniqueESID)) {
                    indexRequest31 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map31, esid, null, true);
                } else {
                    indexRequest31 = EsUtils.getIndexRequest(indexTreeRelationship, index2, map31, esid, null, null);
                }
                UpdateRequest updateRequest32 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map32, childrenESID, null, true);
                UpdateRequest updateRequest33 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map33, parentESID, null, true);
                BulkResponse response3;
                try {
                    response3 = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, indexRequest31, updateRequest32, updateRequest33);
                } catch (Exception e) {
                    EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                    throw e;
                }
                if (response3.hasFailures()) {
                    BulkItemResponse[] items = response3.getItems();
                    boolean b1 = true;
                    boolean b2 = true;
                    boolean b3 = true;
                    if (items[0].isFailed()) {
                        b1 = false;
                        if (StringUtils.isEmpty(uniqueESID)) {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    b1 = EsUtils.operUpdate(indexTreeRelationship, index2, map31, esid, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                    if (b1) {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error(String.format("es报错:operIndex(indexTreeRelationship, index2, map31, esid),%s|%s|%s|%s", indexTreeRelationship, index2, map31, esid), e);
                            }
                        } else {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    String s = EsUtils.operIndex(indexTreeRelationship, index2, map31, esid, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                    if (StringUtils.isNotEmpty(s)) {
                                        b1 = true;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error(String.format("es报错:operIndex(indexTreeRelationship, index2, map31, esid),%s|%s|%s|%s", indexTreeRelationship, index2, map31, esid), e);
                            }
                        }
                    }
                    if (items[1].isFailed()) {
                        b2 = false;
                        try {
                            for (int i = 0; i < 3; i++) {
                                b2 = EsUtils.operUpdate(indexTreeRelationship, index2, map32, childrenESID, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                if (b2) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(String.format("es报错:operUpdate(indexTreeRelationship, index2, map32, childrenESID),%s|%s|%s|%s", indexTreeRelationship, index2, map32, childrenESID), e);
                        }
                    }
                    if (items[2].isFailed()) {
                        b3 = false;
                        try {
                            for (int i = 0; i < 3; i++) {
                                b3 = EsUtils.operUpdate(indexTreeRelationship, index2, map33, parentESID, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                if (b3) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(String.format("es报错:operUpdate(indexTreeRelationship, index2, map33, parentESID),%s|%s|%s|%s", indexTreeRelationship, index2, map33, parentESID), e);
                        }
                    }

                    if (!b1 && !b2 && !b3) {
                        EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                        return false;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (!b1) {
                            map31.put(ESID_FIELD, esid);
                            sb.append(String.format("请手动在%s索引新增%s数据.", index2, map31));
                        }
                        if (!b2) {
                            map32.put(ESID_FIELD, childrenESID);
                            sb.append(String.format("请手动在%s索引更新%s数据.", index2, map32));
                        }
                        if (!b3) {
                            map33.put(ESID_FIELD, parentESID);
                            sb.append(String.format("请手动在%s索引更新%s数据.", index2, map33));
                        }
                        if (sb.length() > 0) {
                            EsUtils.throwRuntimeException("存储失败:关系建立失败!" + sb.toString());
                        }
                    }
                }
                //endregion
                break;
            case 4:
                //region 叶节点
                //新增
                HashMap<String, Object> map41 = new HashMap<String, Object>() {{
                    put(FIELD_PARENT_ESID, parentESID);
                }};
                if (StringUtils.isNotEmpty(uniqueESID)) {
                    map41.put(FIELD_UNIQUE_ESID, uniqueESID);
                }
                //修改父
                HashMap<String, Object> map42 = new HashMap<>();
                currentParentChiESIDs.add(esid);
                map42.put(FIELD_CHILDREN_ESIDS, currentParentChiESIDs);
                DocWriteRequest indexRequest41;
                if (StringUtils.isEmpty(uniqueESID)) {
                    indexRequest41 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map41, esid, null, true);
                } else {
                    indexRequest41 = EsUtils.getIndexRequest(indexTreeRelationship, index2, map41, esid, null, null);
                }
                UpdateRequest updateRequest42 = EsUtils.getUpdateRequest(indexTreeRelationship, index2, map42, parentESID, null, true);
                BulkResponse response4;
                try {
                    response4 = EsUtils.operBulk(WriteRequest.RefreshPolicy.IMMEDIATE, indexRequest41, updateRequest42);
                } catch (Exception e) {
                    EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                    throw e;
                }
                if (response4.hasFailures()) {
                    BulkItemResponse[] items = response4.getItems();
                    boolean b1 = true;
                    boolean b2 = true;
                    if (items[0].isFailed()) {
                        b1 = false;
                        if (StringUtils.isEmpty(uniqueESID)) {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    b1 = EsUtils.operUpdate(indexTreeRelationship, index2, map41, esid, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                    if (b1) {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error(String.format("es报错:operUpdate(indexTreeRelationship, index2, map41, esid),%s|%s|%s|%s", indexTreeRelationship, index2, map41, esid), e);
                            }
                        } else {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    String s = EsUtils.operIndex(indexTreeRelationship, index2, map41, esid, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                    if (StringUtils.isNotEmpty(s)) {
                                        b1 = true;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error(String.format("es报错:operIndex(indexTreeRelationship, index2, map41, esid),%s|%s|%s|%s", indexTreeRelationship, index2, map41, esid), e);
                            }
                        }
                    }
                    if (items[1].isFailed()) {
                        b2 = false;
                        try {
                            for (int i = 0; i < 3; i++) {
                                b2 = EsUtils.operUpdate(indexTreeRelationship, index2, map42, parentESID, WriteRequest.RefreshPolicy.IMMEDIATE).first;
                                if (b2) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(String.format("es报错:operUpdate(indexTreeRelationship, index2, map42, parentESID),%s|%s|%s|%s", indexTreeRelationship, index2, map42, parentESID), e);
                        }
                    }
                    if (!b1 && !b2) {
                        EsUtils.operDelete(indexTreeData, index, esid, WriteRequest.RefreshPolicy.IMMEDIATE);
                        return false;
                    } else if (!b1) {
                        map41.put(ESID_FIELD, esid);
                        EsUtils.throwRuntimeException(String.format("存储失败:关系建立失败!请手动在%s索引新增%s数据", index2, map41));
                    } else if (!b2) {
                        map42.put(ESID_FIELD, parentESID);
                        EsUtils.throwRuntimeException(String.format("存储失败:关系建立失败!请手动在%s索引更新%s数据", index2, map42));
                    }
                }
                //endregion
                break;
            default:
                EsUtils.throwIllegalArgumentException("operType错误!");
        }
        return true;
    }

    private Set<Map<String, Object>> getAllParentData(Map<String, Object> mapRelationship, List<Map<String, Object>> listRelationship) {
        String parentESID = (String) mapRelationship.get(FIELD_PARENT_ESID);
        if (StringUtils.isEmpty(parentESID)) {
            return null;
        }
        Optional<Map<String, Object>> data = listRelationship.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), parentESID)).findAny();
        Set<Map<String, Object>> parentDatas = new HashSet<>();
        Map<String, Object> mapRelationship2;
        while (data.isPresent()) {
            mapRelationship2 = data.get();
            parentDatas.add(mapRelationship2);
            String parentESID2 = (String) mapRelationship2.get(FIELD_PARENT_ESID);
            if (StringUtils.isEmpty(parentESID2)) {
                return parentDatas;
            }
            data = listRelationship.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), parentESID2)).findAny();
        }
        return parentDatas;
    }

    private Set<Map<String, Object>> getAllChildrenData(Map<String, Object> mapRelationship, List<Map<String, Object>> listRelationship) {
        List<String> childrenESIDs = (List<String>) mapRelationship.get(FIELD_CHILDREN_ESIDS);
        if (CollectionUtils.isEmpty(childrenESIDs)) {
            return null;
        }
        Set<Map<String, Object>> childrenData = listRelationship.stream().filter(o -> childrenESIDs.contains(o.get(ESID_FIELD))).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(childrenData)) {
            return null;
        }
        Set<Map<String, Object>> result = new HashSet<>();
        while (CollectionUtils.isNotEmpty(childrenData)) {
            result.addAll(childrenData);
            childrenData = childrenData.stream().map(o -> {
                List<String> childrenESIDs2 = (List<String>) o.get(FIELD_CHILDREN_ESIDS);
                if (CollectionUtils.isEmpty(childrenESIDs2)) {
                    return null;
                }
                Set<Map<String, Object>> childrenData2 = listRelationship.stream().filter(o2 -> childrenESIDs2.contains(o2.get(ESID_FIELD))).collect(Collectors.toSet());
                if (CollectionUtils.isEmpty(childrenData2)) {
                    return null;
                }
                return childrenData2;
            }).filter(Objects::nonNull).flatMap(o -> o.stream()).collect(Collectors.toSet());
        }
        return result;
    }

    private void setAllParentTrees(TreeCube tc, Map<String, Object> mapRelationship, List<Map<String, Object>> listData, Set<Map<String, Object>> listRelationshipParents) {
        TreeCube temp = tc;
        String parentESID;
        while (StringUtils.isNotEmpty(parentESID = (String) mapRelationship.get(FIELD_PARENT_ESID))) {
            String parentESIDTemp = parentESID;
            Optional<Map<String, Object>> data = listData.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), parentESIDTemp)).findAny();
            if (data.isPresent()) {
                TreeCube tcParent = TreeCube.getInstance(data.get());
                temp.setParentTreeCube(tcParent, parentESIDTemp);
                Optional<Map<String, Object>> relationship = listRelationshipParents.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), parentESIDTemp)).findAny();
                if (relationship.isPresent()) {
                    temp = tcParent;
                    mapRelationship = relationship.get();
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /**
     * 别动,一动就怀孕的代码
     *
     * @param tc
     * @param mapRelationship
     * @param listData
     * @param listRelationshipChildrens
     */
    private void setAllChildrenTrees(TreeCube tc, Map<String, Object> mapRelationship, List<Map<String, Object>> listData, Collection<Map<String, Object>> listRelationshipChildrens) {
        Stack<Tuple> stack = new Stack<>();
        stack.push(Tuples.tuple(1, tc, mapRelationship));
        while (!stack.empty()) {
            Tuple tuple = stack.pop();
            int type = tuple.get(0);
            if (type == 1) {
                TreeCube tcCurrent = tuple.get(1);
                mapRelationship = tuple.get(2);

                List<String> childrenESIDs = (List<String>) mapRelationship.get(FIELD_CHILDREN_ESIDS);
                if (CollectionUtils.isNotEmpty(childrenESIDs)) {
                    String esid = childrenESIDs.get(0);
                    Optional<Map<String, Object>> data = listData.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esid)).findAny();
                    if (data.isPresent()) {
                        TreeCube tcChildren = TreeCube.getInstance(data.get());
                        List<TreeCube> tcChildrens;
                        if (childrenESIDs.size() > 1) {
                            tcChildrens = new ArrayList<TreeCube>(childrenESIDs.size()) {{
                                add(tcChildren);
                            }};
                            stack.push(Tuples.tuple(2, tcCurrent, tcChildrens, childrenESIDs, 1));
                        } else {
                            tcChildrens = Collections.singletonList(tcChildren);
                            tcCurrent.setChildrenTreeCubes(tcChildrens, childrenESIDs);
                        }
                        Optional<Map<String, Object>> relationship = listRelationshipChildrens.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esid)).findAny();
                        relationship.ifPresent(stringObjectMap -> stack.push(Tuples.tuple(1, tcChildren, stringObjectMap)));
                    }
                }
            } else if (type == 2) {
                TreeCube tcCurrent = tuple.get(1);
                List<TreeCube> tcChildrens = tuple.get(2);
                List<String> childrenESIDs = tuple.get(3);
                int index = tuple.get(4);

                String esid = childrenESIDs.get(index);
                Optional<Map<String, Object>> data = listData.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esid)).findAny();
                ++index;
                if (data.isPresent()) {
                    TreeCube tcChildren = TreeCube.getInstance(data.get());
                    tcChildrens.add(tcChildren);
                    if (childrenESIDs.size() > index) {
                        stack.push(Tuples.tuple(2, tcCurrent, tcChildrens, childrenESIDs, index));
                    } else {
                        if (tcChildrens.size() != childrenESIDs.size()) {
                            childrenESIDs = tcChildrens.stream().map(o -> (String) o.getData().get(ESID_FIELD)).collect(Collectors.toList());
                        }
                        tcCurrent.setChildrenTreeCubes(tcChildrens, childrenESIDs);
                    }
                    Optional<Map<String, Object>> relationship = listRelationshipChildrens.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esid)).findAny();
                    relationship.ifPresent(stringObjectMap -> stack.push(Tuples.tuple(1, tcChildren, stringObjectMap)));
                } else {
                    if (childrenESIDs.size() > index) {
                        stack.push(Tuples.tuple(2, tcCurrent, tcChildrens, childrenESIDs, index));
                    } else {
                        if (tcChildrens.size() != childrenESIDs.size()) {
                            childrenESIDs = tcChildrens.stream().map(o -> (String) o.getData().get(ESID_FIELD)).collect(Collectors.toList());
                        }
                        tcCurrent.setChildrenTreeCubes(tcChildrens, childrenESIDs);
                    }
                }
            }
        }
    }

    private TreeCube getOneTreeCore(String esid, Map<String, Object> mapRelationship, List<Map<String, Object>> listData) {
        Optional<Map<String, Object>> data = listData.stream().filter(d -> Objects.equals(d.get(ESID_FIELD), esid)).findAny();
        if (!data.isPresent()) {
            return null;
        }
        TreeCube tc = TreeCube.getInstance(data.get(), (String) mapRelationship.get(FIELD_UNIQUE_ESID));
        setOnePartentTree(tc, mapRelationship, listData);
        setOneChildrenTree(tc, mapRelationship, listData);
        return tc;
    }

    /**
     * 设置一层子节点
     *
     * @param tc
     * @param mapRelationship
     * @param listData
     */
    private void setOneChildrenTree(TreeCube tc, Map<String, Object> mapRelationship, List<Map<String, Object>> listData) {
        List<String> childrenESIDs = (List<String>) mapRelationship.get(FIELD_CHILDREN_ESIDS);
        if (CollectionUtils.isNotEmpty(childrenESIDs)) {
            List<TreeCube> tcChildrens = childrenESIDs.stream().map(esidChildren -> {
                Optional<Map<String, Object>> chi = listData.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esidChildren)).findAny();
                return chi.map(TreeCube::getInstance).orElse(null);
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(tcChildrens)) {
                if (childrenESIDs.size() != tcChildrens.size()) {
                    childrenESIDs = tcChildrens.stream().map(o -> (String) o.getData().get(ESID_FIELD)).collect(Collectors.toList());
                }
                tc.setChildrenTreeCubes(tcChildrens, childrenESIDs);
            }
        }
    }

    /**
     * 设置一层父节点
     *
     * @param tc
     * @param mapRelationship
     * @param listData
     */
    private void setOnePartentTree(TreeCube tc, Map<String, Object> mapRelationship, List<Map<String, Object>> listData) {
        String esidParent = (String) mapRelationship.get(FIELD_PARENT_ESID);
        if (StringUtils.isNotEmpty(esidParent)) {
            Optional<Map<String, Object>> parent = listData.stream().filter(o -> Objects.equals(o.get(ESID_FIELD), esidParent)).findAny();
            parent.ifPresent(stringObjectMap -> tc.setParentTreeCube(TreeCube.getInstance(stringObjectMap), esidParent));
        }
    }

    /**
     * 别动,一动就怀孕的代码
     *
     * @param esidParent
     * @param tcParent
     * @param mapRelationship
     * @param listRelationship
     * @param listData
     * @return
     */
    private TreeCube getAllTreesCore(TreeCube tcParent, String esidParent, Map<String, Object> mapRelationship, List<Map<String, Object>> listRelationship, List<Map<String, Object>> listData) {
        Stack<Tuple> stack = new Stack<>();
        stack.push(Tuples.tuple(1, tcParent, esidParent, mapRelationship, null, null, -1));
        TreeCube tcCurrent;
        List<TreeCube> tcBrothers;
        List<String> brotherESIDs;
        int index;
        boolean noChildren;
        while (!stack.isEmpty()) {
            Tuple tuple = stack.pop();
            int type = tuple.get(0);
            if (type == 1) {
                tcParent = tuple.get(1);
                esidParent = tuple.get(2);
                mapRelationship = tuple.get(3);
                tcBrothers = tuple.get(4);
                brotherESIDs = tuple.get(5);
                index = tuple.get(6);
                //获取当前树
                String esidCurrent = (String) mapRelationship.get(ESID_FIELD);
                Optional<Map<String, Object>> data = listData.stream().filter(d -> Objects.equals(d.get(ESID_FIELD), esidCurrent)).findAny();
                if (!data.isPresent()) {
                    stack.push(Tuples.tuple(2, tcParent, esidParent, tcBrothers, brotherESIDs, index));
                    continue;
                }
                tcCurrent = TreeCube.getInstance(data.get());
                //增加父节点
                if (StringUtils.isNotEmpty(esidParent)) {
                    tcCurrent.setParentTreeCube(tcParent, esidParent);
                }
                //将当前树添加到集合中
                if (CollectionUtils.isEmpty(tcBrothers)) {
                    if (CollectionUtils.isEmpty(brotherESIDs) || brotherESIDs.size() < 2) {
                        tcBrothers = Collections.singletonList(tcCurrent);
                    } else {
                        tcBrothers = new ArrayList<>(brotherESIDs.size());
                        tcBrothers.add(tcCurrent);
                    }
                } else {
                    tcBrothers.add(tcCurrent);
                }
                ////获取父树
                //if (tcParent != null) {
                //	tcCurrent.setParentTreeCube(tcParent, esidParent);
                //}
                //获取子树
                List<String> childrenESIDs = (List<String>) mapRelationship.get(FIELD_CHILDREN_ESIDS);
                if (CollectionUtils.isEmpty(childrenESIDs)) {
                    stack.push(Tuples.tuple(2, tcParent, esidParent, tcBrothers, brotherESIDs, index));
                } else {
                    noChildren = true;
                    for (int i = 0, size = childrenESIDs.size(); i < size; i++) {
                        String esidChildren = childrenESIDs.get(i);
                        Optional<Map<String, Object>> relationship = listRelationship.stream().filter(d -> Objects.equals(d.get(ESID_FIELD), esidChildren)).findAny();
                        if (relationship.isPresent()) {
                            stack.push(Tuples.tuple(2, tcParent, esidParent, tcBrothers, brotherESIDs, index));
                            stack.push(Tuples.tuple(1, tcCurrent, esidCurrent, relationship.get(), null, childrenESIDs, i + 1));
                            noChildren = false;
                            break;
                        }
                    }
                    if (noChildren) {
                        stack.push(Tuples.tuple(2, tcParent, esidParent, tcBrothers, brotherESIDs, index));
                    }
                }
            } else if (type == 2) {
                tcParent = tuple.get(1);
                esidParent = tuple.get(2);
                tcBrothers = tuple.get(3);
                brotherESIDs = tuple.get(4);
                index = tuple.get(5);

                if (CollectionUtils.isEmpty(brotherESIDs)) {
                    return tcBrothers.get(0);
                }
                int size = brotherESIDs.size();
                if (index > 0 && size > index) {
                    noChildren = true;
                    for (int i = index; i < size; i++) {
                        String esidChildren = brotherESIDs.get(i);
                        Optional<Map<String, Object>> relationship = listRelationship.stream().filter(d -> Objects.equals(d.get(ESID_FIELD), esidChildren)).findAny();
                        if (relationship.isPresent()) {
                            stack.push(Tuples.tuple(1, tcParent, esidParent, relationship.get(), tcBrothers, brotherESIDs, i + 1));
                            noChildren = false;
                            break;
                        }
                    }
                    if (noChildren) {
                        if (CollectionUtils.isNotEmpty(tcBrothers)) {
                            if (tcBrothers.size() != size) {
                                brotherESIDs = tcBrothers.stream().map(o -> (String) o.getData().get(ESID_FIELD)).collect(Collectors.toList());
                            }
                            tcParent.setChildrenTreeCubes(tcBrothers, brotherESIDs);
                        }
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(tcBrothers)) {
                        if (tcBrothers.size() != size) {
                            brotherESIDs = tcBrothers.stream().map(o -> (String) o.getData().get(ESID_FIELD)).collect(Collectors.toList());
                        }
                        tcParent.setChildrenTreeCubes(tcBrothers, brotherESIDs);
                    }
                }
            }
        }
        return null;
    }

    private String getLogMsg(Object... args) {
        return ArrayUtils.isEmpty(args) ? STR_NULL : Arrays.stream(args).map(o -> o == null ? STR_NULL : o.getClass().isArray() ? ArrayUtils.toString(o) : String.valueOf(o)).collect(Collectors.joining("|"));
    }

    private void logWarn(Object... args) {
        LOG.warn(String.format("es警告:%s", getLogMsg(args)));
    }


}

class MulFieldsGroupAgg {
    //当前第几条数据
    public int currentIndex = 0;
    //是否结束
    public boolean over = false;
    public List<Map<String, Object>> list;
    public int total;
    public int skip;
    private List curList;

    public int getSize() {
        return curList.size();
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public MulFieldsGroupAgg setList(List<Map<String, Object>> list) {
        this.list = list;
        curList = list;
        over = false;
        return this;
    }
}