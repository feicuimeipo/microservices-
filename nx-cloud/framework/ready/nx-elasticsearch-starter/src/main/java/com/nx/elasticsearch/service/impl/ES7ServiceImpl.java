package com.nx.elasticsearch.service.impl;

import elasticsearch.api.ESUtils;
import com.alibaba.fastjson2.JSONObject;
import com.github.sd4324530.jtuple.Tuple2;
import com.github.sd4324530.jtuple.Tuples;
import com.nx.elasticsearch.api.ESClientHelper;
import com.nx.elasticsearch.constant.ESConstants;
import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.entity.index_mapping.EsIndexField;
import com.nx.elasticsearch.entity.index_mapping.EsIndexTree;
import com.nx.elasticsearch.entity.index_mapping.FieldType;
import com.nx.elasticsearch.entity.index_mapping.Mapping;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.QueryType;
import com.nx.elasticsearch.query.SearchUtils;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.query.impl.AggGroup;
import com.nx.elasticsearch.service.ES7Service;
import com.nx.elasticsearch.utils.EsUtils;
import com.nx.hbase.service.HbaseService;
import com.nx.hbase.service.impl.HbaseServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.Percentiles;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: 王坤造
 * @date: 20/12/8 10:28
 * @comment: es7新接口
 * @return:
 * @notes: 新接口!新代码!新年快乐!
 */
@Service
public class ES7ServiceImpl implements ES7Service {

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
        add("base_drug_data");
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
    private final Tuple2<String, Map<String, Object>> t2Null = Tuples.tuple(null, null);
    //特殊字符
    private final String REGEX_CHARS = "[ \\n\\r\\t!\"#$%&*\\\\'()\\[\\]+,-./:;<=>?@^_`{|}~，。：、…【】《》（）“”‘’]";
    private final Pattern p = Pattern.compile(REGEX_CHARS);
    private final Pattern p_en = Pattern.compile("[a-zA-z]");

    private final String ES_ID_FIELD = ESConstants.ES_ID_FIELD;
    private final String ALIAS_SUFFIX = ESConstants.ALIAS_SUFFIX;
    private final String INDEX_AUDIT_TREE_ALIAS = INDEX_AUDIT_TREE + ALIAS_SUFFIX;
    private final Set<String> FIELDS_ESID_OR_NONE = new HashSet<String>() {{
        add(ESID_FIELD);
        add(NULL_VALUE);
    }};
    private final Set<String> FIELDS_ESID = new HashSet<String>() {{
        add(ESID_FIELD);
        add(ES_ID_FIELD);
    }};

    /**
     * 递归获取元数据信息
     *
     * @param builder
     * @param list
     * @return
     */
    @SneakyThrows
    private static void getSubEsIndexfieldVo(XContentBuilder builder, List<EsIndexTree> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (Object obj : list) {
                if (obj instanceof EsIndexField) {
                    //找到最末端层级进行拼装
                    EsIndexField fieldVo = (EsIndexField) obj;
                    int idx = fieldVo.getPath().lastIndexOf('.');
                    String path = fieldVo.getPath().substring(idx + 1);
                    builder.startObject(path);
                    dealField(builder, fieldVo.getFieldType());
                } else {
                    //nested层级继续递归查找
                    EsIndexTree esIndexTreeVo = (EsIndexTree) obj;
                    if (!"firstFloor".equals(esIndexTreeVo.getIndexName())) {
                        String index = esIndexTreeVo.getIndexName();
                        builder.startObject(index)
                                .field("type", "nested")
                                .startObject("properties");
                    }
                    List tmpList = esIndexTreeVo.getItems();
                    if (CollectionUtils.isNotEmpty(tmpList)) {
                        getSubEsIndexfieldVo(builder, tmpList);
                    }
                    if (!"firstFloor".equals(esIndexTreeVo.getIndexName())) {
                        builder.endObject().endObject();
                    }
                }
            }
        }
    }

    /**
     * 递归获取元数据信息
     *
     * @param builder
     * @param esType
     * @return
     */
    @SneakyThrows
    private static void dealField(XContentBuilder builder, FieldType esType) {
        switch (esType) {
            case Double:
                builder.field("type", "double")
                        .endObject();
                break;
            case Long:
                builder.field("type", "long")
                        .endObject();
                break;
            case Integer:
                builder.field("type", "integer")
                        .endObject();
                break;
            case StringPreciseSearchAndFuzzySearch:
                builder.field("type", "keyword")
                        .field("ignore_above", "256")
                        .field("fields", JSONObject.parseObject("{\"raw\": {\"type\": \"text\"}}"))
                        .endObject();
                break;
            case StringFuzzySearch:
                builder.field("type", "text")
                        .endObject();
                break;
            case StringFuzzySearch2:
                builder.field("type", "text")
                        .field("analyzer", "trigrams_keyword")
                        .field("search_analyzer", "my_search_analyzer")
                        .field("search_quote_analyzer", "my_keyword")
                        .endObject();
                break;
            case StringPreciseSearchAndFuzzySearch2:
                builder.field("type", "keyword")
                        .field("ignore_above", 256)
                        .field("fields", EsUtils.initHashMapWithStringObject("raw", EsUtils.initHashMapWithStringObject("type", "text", "analyzer", "trigrams_keyword", "search_analyzer", "my_search_analyzer", "search_quote_analyzer", "my_keyword")))
                        .endObject();
                break;
            case StringNoSearch:
                builder.field("type", "keyword")
                        .field("index", false)
                        .field("doc_values", false)
                        .endObject();
                break;
            default:
                builder.field("type", "keyword")
                        .field("ignore_above", "256")
                        .endObject();
                break;
        }
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

    private static String getLogMsg(Object... args) {
        return ArrayUtils.isEmpty(args) ? STR_NULL : Arrays.stream(args).map(o -> o == null ? STR_NULL : o.getClass().isArray() ? ArrayUtils.toString(o) : String.valueOf(o)).collect(Collectors.joining("|"));
    }

    private static void print(Object... args) {
        System.out.println(getLogMsg(args));
    }

    private static <K, V> LinkedHashMap<K, V> getLinkedHashMap(Integer length) {
        if (length == null || length < 1) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>((int) Math.ceil(length / .75f));
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
     *
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
     *
     *
     * @comment: 時间戳转化为本地時间对象(LocalDateTime)
     * @notes: 默认使用亚洲/上海時区
     */
    private static LocalDateTime getLocalDateTime(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    /**
     *
     * @date: 2018/8/7 10:39
     * @comment: 获取指定年月第1天的本地時间对象(LocalDateTime)
     * @notes: 默认使用亚洲/上海時区,month从1开始
     */
    private static LocalDateTime getLocalDateTime(int year, int month) {
        return getLocalDateTime(year, month, 1);
    }

    private static LocalDateTime getLocalDateTime(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    /**
     *
     * @comment: 本地時间对象(LocalDateTime)转化为時间戳
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

    @SneakyThrows
    private static List<Map<String, String>> GetFiedName(String indexName, boolean hb) {
        RestHighLevelClient client = ESClientHelper.getInstance().getClient(indexName);
        GetMappingsRequest getMappings = new GetMappingsRequest().indices(indexName);
        GetMappingsResponse getMappingResponse = client.indices().getMapping(getMappings, RequestOptions.DEFAULT);
        Map<String, MappingMetadata> allMappings = getMappingResponse.mappings();
        MappingMetadata indexMapping = allMappings.get(indexName);
        Map<String, Object> mapping = indexMapping.sourceAsMap();
        List<Map<String, String>> fieldList = getIndexFieldList("", mapping, hb);
        return fieldList;
    }

    private static List<Map<String, String>> getIndexFieldList(String fieldName,
                                                               Map<String, Object> mapProperties, boolean hb) {
        List<Map<String, String>> fieldList = new ArrayList<Map<String, String>>();
        Map<String, Object> map = (Map<String, Object>) mapProperties
                .get("properties");
        Set<String> keys = map.keySet();
        int i = 1;
        for (String key : keys) {
            if (((Map<String, Object>) map.get(key)).containsKey("type")) {
                String kName = fieldName + "" + key;
                Map<String, String> field = new HashMap<>();
                Object type = ((Map<String, Object>) map.get(key)).get("type");

                if (hb) {
                    if (type != null && !"nested".equals(type.toString())) {
                        field.put("index", String.valueOf(i++));
                        field.put("name", "doc:" + kName);
                        field.put("type", type.toString());
                    }
                } else {
                    field.put("name", kName);
                }
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    public static void main(String[] args) throws Exception {
        ES7Service esService = new ES7ServiceImpl();

        Map<String, Object> asda = new HashMap<>();
        asda.put("esid", "123");
        asda.put("dqwds", "asda");
        Map<String, Object> asda6 = new HashMap<>();
        asda6.put("esid", "456");
        asda6.put("dqwds", "asda");
        List<Map<String, Object>> ab = new ArrayList<>();
        ab.add(asda);
        ab.add(asda6);
        hbaseService.BulkInsert("test", ab);

        System.exit(0);
    }

    static void timing(Consumer0 c) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        c.accept();
        stopwatch.stop();
        System.out.println(stopwatch.prettyPrint());
    }

    @Override
    public boolean getExist(String index, String esid) {
        checkESIDStr(esid);
        String index2 = getNewIndex(index);
        return ESUtils.operExist(index2, index, esid);
    }

    @Override
    public Map<String, Object> getOne(String index, String esid, String... showFields) {
        checkFields(showFields);
        checkFieldsESID(showFields);
        checkESIDStr(esid);
        String index2 = getNewIndex(index);
        return ESUtils.operGetSource(index2, index, esid, showFields);
    }

    @Override
    public Map<String, Object> getOne(String index, List<Query> queries, String... showFields) {
        List<Map<String, Object>> list = getList(index, queries, null, 1, 0, null, showFields);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Map<String, Object>> getMulti(String index, String[] esid, String... showFields) {
        checkFields(showFields);
        checkFieldsESID(showFields);
        checkArray(esid, "esid");
        Arrays.stream(esid).forEach(this::checkESIDStr);
        String index2 = getNewIndex(index);
        return ESUtils.operGetMulti(index2, esid, showFields);
    }

    @Override
    public Page<Map<String, Object>> getPage(String index, List<Query> queries, Page page, String... showFields) {
        List<Map<String, Object>> list;
        if (page.getCurrentPage() < 2) {
            checkFields(showFields);
            index = getNewIndex(index);
            QueryBuilder query = getQuery(index, queries, true, showFields);
            FieldSortBuilder[] sorts = getSortsNoESID(page.getSorts());
            try {
                Tuple2<List<Map<String, Object>>, Long> t = ESUtils.operSearch2(index, query, sorts, null, page.getStart().intValue(), page.getLimit(), showFields);
                page.setTotalCount(t.second);
                list = t.first;
            } catch (Exception e) {
                logError(e, index, queries, page, showFields);
                EsUtils.throwRuntimeException(e);
                list = null;
            }
        } else {
            list = getList(index, queries, page.getSorts(), page.getLimit(), page.getStart().intValue(), null, showFields);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(hm -> mapAdd(hm, showFields));
        }
        page.setItemList(list);
        return page;
    }

    @Override
    public Page<Map<String, Object>> getPageCollapse(String index, List<Query> queries, Page page, String collapse, String... showFields) {
        List<Map<String, Object>> list;
        if (page.getCurrentPage() < 2) {
            checkFields(showFields);
            index = getNewIndex(index);
            QueryBuilder query = getQuery(index, queries, true, showFields);
            FieldSortBuilder[] sorts = getSortsNoESID(page.getSorts());
            try {
                Tuple2<List<Map<String, Object>>, Long> t = ESUtils.operSearchCollapse2(index, collapse, query, sorts, null, page.getStart().intValue(), page.getLimit(), showFields);
                page.setTotalCount(t.second);
                list = t.first;
            } catch (Exception e) {
                logError(e, index, queries, page, showFields);
                EsUtils.throwRuntimeException(e);
                list = null;
            }
        } else {
            list = getListCollapse(index, collapse, queries, page.getSorts(), page.getLimit(), page.getStart().intValue(), null, showFields);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(hm -> mapAdd(hm, showFields));
        }
        page.setItemList(list);
        return page;
    }

    /**
     * 备注: 请谨慎使用参数size=-1,会直接触发游标查询,尤其体现于1G以上的索引可能会导致Elasticsearch集群崩溃,建议大于1G的索引size=-1的情况请使用getListSub方法
     */
    @Override
    public List<Map<String, Object>> getListCollapse(String index, String collapse, List<Query> queries, LinkedHashMap<String, Boolean> sorts, int size, int skip, Object[] after, String... showFields) {
        checkFields(showFields);
        if (size == 0) {
            EsUtils.throwIllegalArgumentException("size必须不等于0!");
        }
        if (ArrayUtils.isNotEmpty(after)) {
            checkSize1000(size);
            checkMap(sorts, "after有值时,sorts禁止为空!");
            if (skip > 0) {
                EsUtils.throwIllegalArgumentException("after和skip参数禁止同时存在!");
            }
        }
        index = getNewIndex(index);
        QueryBuilder query = getQuery(index, queries, true, showFields);
        SortBuilder<?>[] orders = getSortsNoESID(sorts);
        try {
            return ESUtils.operSearchCollapse1(index, collapse, query, orders, after, skip, size, showFields);
        } catch (Exception e) {
            logError(e, index, queries, sorts, size, showFields);
            EsUtils.throwRuntimeException(e);
        }
        return Collections.emptyList();
    }

    /**
     * 备注:
     * 请谨慎使用参数size=-1,
     * 会直接触发游标查询,
     * 尤其体现于1G以上的索引可能会导致Elasticsearch集群崩溃,
     * 建议大于1G的索引size=-1的情况请使用getListSub方法。
     *
     * @param index
     * @param queries
     * @param sorts      排序字段和排序方式(true,null:升序;false:降序)(esid禁止排序)
     * @param size       获取条数(size=-1时,获取全部;skip有值时,size+skip<=10000;after有值时,size<=1000)
     * @param skip       跳过条数(优先级比after低,禁止和after同时存在,大于1时size禁止=-1)
     * @param after      排序字段的对应的值(每次分页需要设置上一次分页最後一条数据的值,有值时size禁止=-1)
     * @param showFields 返回字段
     * @return
     */
    @Override
    public List<Map<String, Object>> getList(String index, List<Query> queries, LinkedHashMap<String, Boolean> sorts, int size, int skip, Object[] after, String... showFields) {
        checkFields(showFields);
        if (size == 0) {
            EsUtils.throwIllegalArgumentException("size必须不等于0!");
        }
        if (ArrayUtils.isNotEmpty(after)) {
            checkSize1000(size);
            checkMap(sorts, "after有值时,sorts禁止为空!");
            if (skip > 0) {
                EsUtils.throwIllegalArgumentException("after和skip参数禁止同时存在!");
            }
        }
        index = getNewIndex(index);
        QueryBuilder query = getQuery(index, queries, true, showFields);
        SortBuilder<?>[] orders = getSortsNoESID(sorts);
        try {
            return ESUtils.operSearch1(index, query, orders, after, skip, size, showFields);
        } catch (Exception e) {
            logError(e, index, queries, sorts, size, showFields);
            EsUtils.throwRuntimeException(e);
        }
        return Collections.emptyList();
    }

    /**
     *
     * @param index
     * @param queries
     * @param orders
     * @param id         第一次null,第2次传返回的id,第3次传返回的id,第4次传返回的id...
     * @param size       每批返回的条数
     * @param showFields
     * @return
     */
    @Override
    public List<Object> getListSub(String index, List<Query> queries, LinkedHashMap<String, Boolean> orders, String id, int size, String... showFields) {
        checkFields(showFields);
        checkSize1000(size);
        index = getNewIndex(index);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        getQuery(index, queries, searchSourceBuilder, true, showFields);
        getSortsNoESID(orders, searchSourceBuilder);
        ESUtils.setFetchSource(searchSourceBuilder, showFields);
        try {
            return ESUtils.operSearchScroll(index, null, searchSourceBuilder, size, id);
        } catch (Exception e) {
            logError(e, index, queries, orders, size, id, showFields);
            EsUtils.throwRuntimeException(e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void clearScrollID(String index, String... ids) {
        String index2 = getNewIndex(index);
        checkArray(ids, "ids");
        ESUtils.operClearScroll(null, index2, ids);
    }

    @Override
    public long getCount(String index, List<Query> queries) {
        String index2 = getNewIndex(index);
        QueryBuilder query = getQuery(index, queries, false);
        return ESUtils.operCount(index2, query);
    }

    @Override
    public List<Object> getAggregationsGroup(String index, List<Query> queries, int size, Object after, String field) {
        return getAggregationsGroup(index, queries, size, after, field, true);
    }

    @Override
    public List<Object> getAggregationsGroup(String index, List<Query> queries, int size, Object after, String field, boolean asc) {
        checkSize1000(size);
        checkGroupFields(field);
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
            SearchResponse response = ESUtils.operSearchAgg(index, null, searchSourceBuilder);
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

    @Override
    public List<Map<String, Object>> getAggregationsGroupsAgg(String index, List<Query> queries, int size, Map<String, Object> after, List<String> fields, AggAggregate... aggBuilders) {
        LinkedHashMap<String, Boolean> orders = fields.stream().collect(Collectors.toMap(Function.identity(), o -> true, (o1, o2) -> o1, LinkedHashMap::new));
        return getAggregationsGroupsAgg(index, queries, size, after, orders, aggBuilders);
    }

    @Override
    public List<Map<String, Object>> getAggregationsGroupsAgg(String index, List<Query> queries, int size, Map<String, Object> after, LinkedHashMap<String, Boolean> orders, AggAggregate... aggBuilders) {
        checkSize1000(size);
        Set<String> keys = orders.keySet();
        checkRange(keys, 1, 5);
        checkGroupFields(keys);
        List list = orders.entrySet().stream().map(o -> getTermsValuesSourceBuilder(o.getKey(), o.getValue())).collect(Collectors.toList());
        CompositeAggregationBuilder compositeAggregationBuilder = new CompositeAggregationBuilder(keys.stream().findFirst().get(), list);
        compositeAggregationBuilder.size(size);
        if (MapUtils.isNotEmpty(after)) {
            if (after.size() != orders.size()) {
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
            SearchResponse response = ESUtils.operSearchAgg(index, null, searchSourceBuilder);
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

    @Override
    public void delete(String index, String esid) {
        checkESIDStr(esid);
        String index2 = getNewIndex(index);
        ESUtils.operDelete(index2, index, esid);
        hbaseService.delete(index, esid);
    }

    @Override
    public void deleteField(String index, String esid, String... fields) {
        checkESIDStr(esid);
        checkFields(fields);
        index = getNewIndex(index);
        ESUtils.operDeleteField(index, esid, fields);
    }

    @Override
    public void insert(String index, Map<String, Object> map) {
        insertCore(index, map, true);
    }

    @Override
    public void insertOrReplace(String index, Map<String, Object> map) {
        insertCore(index, map, false);
    }

    @Override
    public void update(String index, Map<String, Object> map) {
        updateCore(index, map, false);
    }

    @Override
    public void updateOrInsert(String index, Map<String, Object> map) {
        updateCore(index, map, true);
    }

    @Override
    public boolean indexCreate(String index, List<Mapping> list, boolean isExist) {
        //判断参数合法性
        if (StringUtils.isEmpty(index) || CollectionUtils.isEmpty(list)) {
            LOG.error("parameter error please try again!!!");
            return false;
        }
        //判断索引名是否以bak结尾
        if (index.length() > 3 && index.endsWith("bak")) {
            LOG.error("index name can not be ending with 'bak' please try again!!!");
            return false;
        }

        List<Mapping> mappingList = new ArrayList<>();
        for (Mapping m : list) {
            if (isEmptyOrContainsWhitespaceOrContainUpperCase(m.getField())) {
                EsUtils.throwIllegalArgumentException("mapping中字段名禁止为空!禁止包含空格!禁止包含大写字母!");
            }
            if (m.getFieldType() == FieldType.Nested) {
                continue;
            }
            if (m.getNestedFloor() == null) {
                m.setNestedFloor("firstFloor");
            }
            mappingList.add(m);
        }

        //获取元数据
        List<EsIndexTree> vos = SearchUtils.getEsMetadataTree(mappingList);
        //构建索引mapping,settings
        boolean mappingResult;
        try {
            mappingResult = getMapping(vos, index, isExist);
        } catch (IOException e) {
            logError(e, index, mappingList, isExist);
            throw EsUtils.getRuntimeException(null, e);
        }
        return mappingResult;
    }

    /**
     * 构建Mapping结构
     *
     * @param index
     * @param list
     * @return
     */
    private boolean getMapping(List<EsIndexTree> list, String index, boolean isExist) throws IOException {
        String indexNew;
        if (isExist) {
            indexNew = index + "_" + System.currentTimeMillis();
        } else {
            indexNew = index;
        }
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("dynamic", "strict")
                .startObject("properties");
        getSubEsIndexfieldVo(builder, list);
        builder.endObject().endObject();
        //拼装索引mapping
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexNew);
        createIndexRequest.mapping("_doc", builder);
        //设置索引settings
        Map<String, Object> source = new HashMap<>();
        source.put("index.number_of_shards", "5");
        source.put("index.number_of_replicas", "1");
        source.put("index.store.type", "niofs");
        source.put("index.mapping.nested_fields.limit", "3000");
        source.put("index.mapping.total_fields.limit", "10000");
        source.put("index.translog.flush_threshold_size", "2048mb");
        source.put("index.translog.sync_interval", "150s");
        source.put("index.max_result_window", "100000000");
        source.put("index.max_ngram_diff", "10");
        source.put("index.mapping.coerce", "false");
        source.put("index.mapping.ignore_malformed", "false");
        source.put("index.refresh_interval", "-1");
        source.put("index.requests.cache.enable", "true");
        source.put("index.analysis.filter.autocomplete_filter.type", "edge_ngram");
        source.put("index.analysis.filter.autocomplete_filter.min_gram", "2");
        source.put("index.analysis.filter.autocomplete_filter.max_gram", "15");
        source.put("index.analysis.filter.single_ngram_filter.type", "ngram");
        source.put("index.analysis.filter.single_ngram_filter.min_gram", "1");
        source.put("index.analysis.filter.single_ngram_filter.max_gram", "1");
        source.put("index.analysis.filter.trigrams_filter.type", "ngram");
        source.put("index.analysis.filter.trigrams_filter.min_gram", "2");
        source.put("index.analysis.filter.trigrams_filter.max_gram", "10");
        source.put("index.routing.allocation.enable", "all");
        source.put("index.analysis.analyzer.trigrams_keyword.filter", new String[]{"lowercase", "trigrams_filter"});
        source.put("index.analysis.analyzer.trigrams_keyword.type", "custom");
        source.put("index.analysis.analyzer.trigrams_keyword.tokenizer", "keyword");
        source.put("index.analysis.analyzer.my_keyword.filter", new String[]{"lowercase"});
        source.put("index.analysis.analyzer.my_keyword.type", "custom");
        source.put("index.analysis.analyzer.my_keyword.tokenizer", "keyword");
        source.put("index.analysis.analyzer.autocomplete.filter", new String[]{"lowercase", "autocomplete_filter"});
        source.put("index.analysis.analyzer.autocomplete.type", "custom");
        source.put("index.analysis.analyzer.autocomplete.tokenizer", "standard");
        source.put("index.analysis.analyzer.prefix.filter", new String[]{"lowercase", "autocomplete_filter"});
        source.put("index.analysis.analyzer.prefix.type", "custom");
        source.put("index.analysis.analyzer.prefix.tokenizer", "keyword");
        source.put("index.analysis.analyzer.autocomplete_min1.filter", new String[]{"lowercase", "autocomplete_filter"});
        source.put("index.analysis.analyzer.autocomplete_min1.type", "custom");
        source.put("index.analysis.analyzer.autocomplete_min1.tokenizer", "standard");
        source.put("index.analysis.analyzer.my_search_analyzer.filter", new String[]{"lowercase"});
        source.put("index.analysis.analyzer.my_search_analyzer.type", "custom");
        source.put("index.analysis.analyzer.my_search_analyzer.tokenizer", "ngram_tokenizer");
        source.put("index.analysis.analyzer.pinyin_analyzer.tokenizer", "my_pinyin");
        source.put("index.analysis.analyzer.trigrams.filter", new String[]{"lowercase", "trigrams_filter"});
        source.put("index.analysis.analyzer.trigrams.type", "custom");
        source.put("index.analysis.analyzer.trigrams.tokenizer", "standard");
        source.put("index.analysis.analyzer.single_ngram.filter", new String[]{"lowercase", "single_ngram_filter"});
        source.put("index.analysis.analyzer.single_ngram.type", "custom");
        source.put("index.analysis.analyzer.single_ngram.tokenizer", "standard");
        source.put("index.analysis.tokenizer.my_pinyin.keep_joined_full_pinyin", "true");
        source.put("index.analysis.tokenizer.my_pinyin.none_chinese_pinyin_tokenize", "false");
        source.put("index.analysis.tokenizer.my_pinyin.keep_none_chinese_in_first_letter", "false");
        source.put("index.analysis.tokenizer.my_pinyin.keep_none_chinese_in_joined_full_pinyin", "true");
        source.put("index.analysis.tokenizer.my_pinyin.keep_first_letter", "false");
        source.put("index.analysis.tokenizer.my_pinyin.trim_whitespace", "false");
        source.put("index.analysis.tokenizer.my_pinyin.type", "pinyin");
        source.put("index.analysis.tokenizer.my_pinyin.keep_none_chinese", "false");
        source.put("index.analysis.tokenizer.my_pinyin.keep_full_pinyin", "false");
        source.put("index.analysis.tokenizer.ngram_tokenizer.token_chars", new String[]{"letter", "digit", "punctuation", "symbol"});
        source.put("index.analysis.tokenizer.ngram_tokenizer.min_gram", "2");
        source.put("index.analysis.tokenizer.ngram_tokenizer.type", "ngram");
        source.put("index.analysis.tokenizer.ngram_tokenizer.max_gram", "10");
        createIndexRequest.settings(source);
        //操作索引的客户端
        RestHighLevelClient client = ESClientHelper.getInstance().getClient(indexNew);
        //执行创建索引库
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        hbaseService.createTable(indexNew);
        boolean result = false;
        //获取索引别名
        String alias = index + "alias";
        Map<String, Object> sourceNew = new HashMap<>();
        sourceNew.put("index.routing.allocation.enable", "new_primaries");
        UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest(indexNew).settings(sourceNew);
        if (createIndexResponse.isAcknowledged()) {
            if (!isExist) {
                ESUtils.addAlias(index, alias);
                client.indices().putSettings(updateSettingsRequest, RequestOptions.DEFAULT);
                return true;
            } else {
                String indexName = ESUtils.getIndexName(alias);
                //获取索引文档数
                CountRequest countRequest = new CountRequest(indexName);
                CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
                Long totalCount = countResponse.getCount();
                if (StringUtils.isNotEmpty(alias)) {
                    //新建别名索引名加newalias到新索引上
                    if (ESUtils.addAlias(indexNew, index + "newalias")) {
                        //旧索引增加deleted别名
                        if (totalCount <= 0 && indexName.contains("_1")) {
                            if (EsUtils.deleteAlias(indexName, index + "alias")) {
                                EsUtils.addAlias(indexName, "deleted");
                            }
                        }
                        client.indices().putSettings(updateSettingsRequest, RequestOptions.DEFAULT);
                        return true;
                    } else {
                        LOG.error("new index add alias fail!!!");
                    }
                } else {
                    LOG.error("this index has no alias!!!");
                }
            }
        }
        //得到响应
        return result;
    }

    private boolean isEmptyOrContainsWhitespaceOrContainUpperCase(String o) {
        return StringUtils.isEmpty(o) || StringUtils.containsWhitespace(o) || o.chars().anyMatch(Character::isUpperCase);
    }

    @Override
    public boolean indexAdd(String index, List<Mapping> list) {
        checkStr(index, "index");
        checkColl(list);
        if (list.stream().anyMatch(o -> isEmptyOrContainsWhitespaceOrContainUpperCase(o.getField()))) {
            EsUtils.throwIllegalArgumentException("mapping中字段名禁止为空!禁止包含空格!禁止包含大写字母!");
        }


        Map<String, Object> properties = EsUtils.initHashMapWithStringObject();
        Tuple2<String, Map<String, Object>> t2 = t2Null;
        for (Mapping mapping : list) {
            t2 = getMapFromMapping(properties, mapping, t2);
        }
        index = getNewIndex(index);
        return ESUtils.indexAdd(index, EsUtils.initHashMapWithStringObject("properties", properties));
    }

    /**
     * 重导数据后旧索引别名处理(2021.06.01)
     *
     * @param indexName
     * @return
     */
    @Override
    public boolean dealAlias(String indexName) {
        String indexOldAlias = indexName + "alias";
        String indexNewAlias = indexName + "newalias";
        String indexOld = ESUtils.getIndexName(indexOldAlias);
        String indexNew = ESUtils.getIndexName(indexNewAlias);
        if (ESUtils.deleteAlias(indexOld, indexOldAlias)) {
            //旧索引增加deleted别名
            if (ESUtils.addAlias(indexOld, "deleted")) {
                if (ESUtils.deleteAlias(indexNew, indexNewAlias)) {
                    //旧索引增加deleted别名
                    if (ESUtils.addAlias(indexNew, indexOldAlias)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取索引Mapping(2021.06.17)
     *
     * @param indexName
     * @return
     */
    @SneakyThrows
    @Override
    public String getMapping(String indexName) {
        String json;
        if (indexName.contains("alias")) {
            indexName = EsUtils.getIndexName(indexName);
        }
        try {
            RestHighLevelClient client = ESClientHelper.getInstance().getClient(indexName);
            GetMappingsRequest getMappings = new GetMappingsRequest().indices(indexName);
            GetMappingsResponse getMappingResponse = client.indices().getMapping(getMappings, RequestOptions.DEFAULT);
            Map<String, MappingMetadata> allMappings = getMappingResponse.mappings();
            MappingMetadata indexMapping = allMappings.get(indexName);
            Map<String, Object> mapping = indexMapping.sourceAsMap();
            json = JSONObject.toJSONString(mapping);
        } catch (Exception e) {
            throw EsUtils.getRuntimeException("no such index " + indexName, e);
        }
        return json;
    }

    /**
     * 获取索引Mapping(2021.06.17)
     *
     * @param indexName
     * @return
     */
    @SneakyThrows
    @Override
    public void reLoadCcrIndex(String indexName) {
        RestHighLevelClient client = ESClientHelper.getInstance().getClientRead(indexName);
        RestClient restClient = client.getLowLevelClient();
        Request request = new Request("PUT", "/" + indexName + "/_ccr/pause_follow");
        restClient.performRequest(request);
        Thread.sleep(5000);
        Request requestReOpen = new Request("PUT", "/" + indexName + "/_ccr/resume_follow");
        restClient.performRequest(requestReOpen);
    }

    @Override
    public Map<String, String> bulkDelete(String index, Set<String> list) {
        checkColl(list);
        String index2 = getNewIndex(index);
        return EsUtils.operBulkDelete(index2, WriteRequest.RefreshPolicy.NONE, list);
    }

    @Override
    public Map<String, String> BulkInsert(String index, List<Map<String, Object>> list) {
        return bulkInsertCore(index, list, true);
    }

    @Override
    public Map<String, String> BulkInsertOrReplace(String index, List<Map<String, Object>> list) {
        return bulkInsertCore(index, list, false);
    }

    @Override
    public Map<String, String> BulkUpdate(String index, List<Map<String, Object>> list) {
        return bulkUpdateCore(index, list, false);
    }

    @Override
    public Map<String, String> BulkUpdateOrInsert(String index, List<Map<String, Object>> list) {
        return bulkUpdateCore(index, list, true);
    }

    @Override
    public boolean freshes(String... index) {
        return EsUtils.operRefresh(null, index);
    }

    @Override
    public Map<String, String> bulkDeleteAll(String index, Set<String> list) {
        return EsUtils.operBulkDelete(index, list);
    }

    @Override
    public Map<String, String> BulkInsertAll(String index, List<Map<String, Object>> list) {
        return EsUtils.operBulkIndex(index, list, true);
    }

    @Override
    public Map<String, String> BulkInsertOrReplaceAll(String index, List<Map<String, Object>> list) {
        return EsUtils.operBulkIndex(index, list, false);
    }

    @Override
    public Map<String, String> BulkUpdateAll(String index, List<Map<String, Object>> list) {
        return EsUtils.operBulkUpdate(index, list, false);
    }

    @Override
    public Map<String, String> BulkUpdateOrInsertAll(String index, List<Map<String, Object>> list) {
        return ESUtils.operBulkUpdate(index, list, true);
    }

    /**
     * 私用函数
     */

    @SneakyThrows
    private Tuple2<String, Map<String, Object>> getMapFromMapping(Map<String, Object> properties, Mapping mapping, Tuple2<String, Map<String, Object>> t2) {
        String parent = t2.first;
        Map<String, Object> message = t2.second;
        String partent = mapping.getFieldType() == FieldType.Nested ? mapping.getParent() : mapping.getNestedFloor();
        if (StringUtils.isNotEmpty(partent) && StringUtils.equals(partent, parent)) {
            properties = message;
        }
        message = EsUtils.initHashMapWithStringObject();
        properties.put(mapping.getField(), message);
        switch (mapping.getFieldType()) {
            case Integer:
                message.put("type", "integer");
                break;
            case Long:
                message.put("type", "long");
                break;
            case Double:
                message.put("type", "double");
                break;
            case StringPreciseSearch:
                message.put("type", "keyword");
                message.put("ignore_above", 256);
                break;
            case StringFuzzySearch:
                message.put("type", "text");
                break;
            case StringFuzzySearch2:
                message.put("type", "text");
                message.put("analyzer", "trigrams_keyword");
                message.put("search_analyzer", "my_search_analyzer");
                message.put("search_quote_analyzer", "my_keyword");
                break;
            case StringPreciseSearchAndFuzzySearch:
                message.put("type", "keyword");
                message.put("ignore_above", 256);
                message.put("fields", EsUtils.initHashMapWithStringObject("raw", EsUtils.initHashMapWithStringObject("type", "text")));
                break;
            case StringPreciseSearchAndFuzzySearch2:
                message.put("type", "keyword");
                message.put("ignore_above", 256);
                message.put("fields", EsUtils.initHashMapWithStringObject("raw", EsUtils.initHashMapWithStringObject("type", "text", "analyzer", "trigrams_keyword", "search_analyzer", "my_search_analyzer", "search_quote_analyzer", "my_keyword")));
                break;
            case StringNoSearch:
                message.put("type", "keyword");
                message.put("index", false);
                message.put("doc_values", false);
                break;
            case Nested:
                message.put("type", "nested");
                Map<String, Object> message2 = new HashMap<>();
                message.put("properties", message2);
                return Tuples.tuple(mapping.getField(), message2);
            default:
                EsUtils.throwIllegalArgumentException("FieldType类型错误!");
        }
        if (StringUtils.isEmpty(partent)) {
            return t2Null;
        }
        return Tuples.tuple(partent, properties);
    }

    public List<Map<String, Object>> getMap(Map<String, String> map, List<Map<String, Object>> list) {
        List<Map<String, Object>> resultlist = new ArrayList<>();
        Set<String> ids = map.keySet();
        for (Map<String, Object> m : list) {
            if (m.get("esid") != null && !ids.contains(m.get("esid"))) {
                resultlist.add(m);
            }
        }
        return resultlist;
    }

    private Map<String, String> bulkInsertCore(String index, List<Map<String, Object>> list, boolean b) {
        Map<String, String> results = bulkInsertOrUpdateCore(index, list, b, ESUtils::operBulkIndex, "bulk_insert");
//		if(results!=null && results.size()>0){
//			list = getMap(results,list);
//		}
//		List<Map<String, Object>> hbaseMap = sub(index,list,true);
//		if(hbaseMap!=null && hbaseMap.size()>0) {
//		hbaseService.BulkInsert(index, list);
//		}
        return results;
    }

    private Map<String, String> bulkUpdateCore(String index, List<Map<String, Object>> list, boolean b) {
        Map<String, String> results = bulkInsertOrUpdateCore(index, list, b, ESUtils::operBulkUpdate, "bulk_update");
        if (results != null && results.size() > 0) {
            list = getMap(results, list);
        }
        hbaseService.BulkInsert(index, list);
        return results;
    }

    private Map<String, String> bulkInsertOrUpdateCore(String index, List<Map<String, Object>> list, boolean b, Function4<String, WriteRequest.RefreshPolicy, List<Map<String, Object>>, Boolean, Map<String, String>> f, String s) {
        checkColl(list);
        String index2 = getNewIndex(index);
        try {
            Map<String, String> results = f.apply(index2, WriteRequest.RefreshPolicy.NONE, list, b);
            return results;
        } catch (Exception e) {
            logError(e, index, b, s);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    private void updateCore(String index, Map<String, Object> map, boolean b) {
        insertOrUpdateCore(index, map, b, ESUtils::operUpdate, "update");
        hbaseService.insert(index, map);
    }

    private void insertCore(String index, Map<String, Object> map, boolean b) {
        insertOrUpdateCore(index, map, b, ESUtils::operIndex, "insert");
    }

    private void insertOrUpdateCore(String index, Map<String, Object> map, boolean b, Consumer3<String, Map<String, Object>, Boolean> f, String s) {
        checkMap1(map);
        String index2 = getNewIndex(index);
        try {
            f.accept(index2, map, b);
        } catch (Exception e) {
            logError(e, index2, map, b, s);
            throw EsUtils.getRuntimeException(null, e);
        }
    }

    private void logError(Exception e, Object... args) {
        LOG.error(String.format("es报错:%s:%s", e.getStackTrace()[1].getMethodName(), getLogMsg(args)), e);
    }

    private void logWarn(Object... args) {
        LOG.warn(String.format("es警告:%s", getLogMsg(args)));
    }

    private boolean isEqualsESID(String field2) {
        return FIELDS_ESID.contains(field2);
    }

    private List<Object> getCompositeAggregationValues2(Aggregation aggregation, String field) {
        CompositeAggregation multiBucketsAggregation = (CompositeAggregation) aggregation;
        List<? extends CompositeAggregation.Bucket> buckets = multiBucketsAggregation.getBuckets();
        if (CollectionUtils.isEmpty(buckets)) {
            return Collections.EMPTY_LIST;
        }
        return buckets.stream().map(o -> o.getKey().get(field)).collect(Collectors.toList());
    }

    private void checkMap1(Map<String, Object> map) {
        if (MapUtils.isEmpty(map) || map.size() < 2) {
            EsUtils.throwIllegalArgumentException("map禁止为空,且元素个数必须>1!");
        }
    }

    private void checkMap(Map map) {
        checkMap(map, "map禁止为空!");
    }

    private void checkMap(Map map, String name) {
        if (MapUtils.isEmpty(map)) {
            EsUtils.throwIllegalArgumentException(name);
        }
    }

    private <T> void checkColl(Collection<T> coll) {
        if (CollectionUtils.isEmpty(coll)) {
            EsUtils.throwIllegalArgumentException("coll禁止为空!");
        }
    }

    private void checkFields(String[] showFields) {
        if (ArrayUtils.isEmpty(showFields)) {
            EsUtils.throwIllegalArgumentException("showFields禁止为空!");
        }
    }

    private void checkFieldsESID(String[] showFields) {
        if (showFields.length == 1 && FIELDS_ESID_OR_NONE.contains(showFields[0])) {
            EsUtils.throwIllegalArgumentException("showFields禁止为esid或None!");
        }
    }

    private void checkArray(String[] showFields, String name) {
        if (ArrayUtils.isEmpty(showFields)) {
            EsUtils.throwIllegalArgumentException(String.format("%s禁止为空!", name));
        }
    }

    private void checkESIDStr(String esid) {
        checkStr(esid, "esid");
    }

    private void checkStr(String s, String name) {
        if (StringUtils.isEmpty(s)) {
            EsUtils.throwIllegalArgumentException(String.format("%s禁止为空!", name));
        }
    }

    private void checkSize100(int size) {
        if (size < 1 || size > 100) {
            EsUtils.throwIllegalArgumentException("size必须>0且<=100!");
        }
    }

    private void checkSize1000(int size) {
        if (size < 1 || size > 1000) {
            EsUtils.throwIllegalArgumentException("size必须>0且<=1000!");
        }
    }

    private void checkGroupFields(String... fields) {
        for (String s : fields) {
            if (isEqualsESID(s)) {
                EsUtils.throwIllegalArgumentException("分组字段禁止使用esid!");
            }
        }
    }

    private void checkGroupFields(Collection<String> fields) {
        if (fields.stream().anyMatch(this::isEqualsESID)) {
            EsUtils.throwIllegalArgumentException("分组字段禁止使用esid!");
        }
    }

    private void checkRange(Collection<String> keys, int min, int max) {
        int size = keys.size();
        if (size < min || size > max) {
            EsUtils.throwIllegalArgumentException(String.format("范围大小必须在[%s,%s]!", min, max));
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
            ArrayList<Integer> integers = new ArrayList<>();
            integers.add(0);
            f = o -> {
                integers.set(0, integers.get(0) + 1);
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

    private void getMetrics(List<Aggregation> aggregations, Map<String, Object> map) {
        for (Aggregation aggregation : aggregations) {
            String name = aggregation.getName();
            //这里获取的是标准的聚合函数:avg,max,min,sum,count,count_distinct
            if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
                NumericMetricsAggregation.SingleValue aggTerm = (NumericMetricsAggregation.SingleValue) aggregation;
                map.put(name, aggTerm.value());
            } else if (aggregation instanceof Percentiles) {//这里获取中值
                Percentiles median = (Percentiles) aggregation;
                map.put(name, median.percentile(50));
            }
        }
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

    private FieldSortBuilder[] getSortsNoESID(Map<String, Boolean> orders) {
        if (MapUtils.isEmpty(orders)) {
            return null;
        }
        if (CollectionUtils.containsAny(FIELDS_ESID, orders.keySet())) {
            EsUtils.throwIllegalArgumentException(String.format("排序字段禁止用:%s", ESID_FIELD));
        }
        return orders.entrySet().stream().map(o -> SortBuilders.fieldSort(o.getKey()).order(getSortOrder(o.getValue())).missing("_last")).toArray(FieldSortBuilder[]::new);
    }

    //根据page中sort参数获取ES的sort参数
    private boolean getSortsNoESID(Map<String, Boolean> orders, SearchSourceBuilder searchSourceBuilder) {
        FieldSortBuilder[] arr = getSortsNoESID(orders);
        if (ArrayUtils.isEmpty(arr)) {
            return false;
        }
        for (FieldSortBuilder fieldSortBuilder : arr) {
            searchSourceBuilder.sort(fieldSortBuilder);
        }
        return true;
    }

    private String getNewIndex(String index) {
        if (StringUtils.isEmpty(index)) {
            EsUtils.throwIllegalArgumentException("index禁止为空!");
        }
        if (StringUtils.endsWith(index, ALIAS_SUFFIX)) {
            return index;
        }
        return index + ALIAS_SUFFIX;
    }

    @Deprecated
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
            if (isEqualsESID(field)) {
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

    /**
     * 弄到es api中!!!
     *
     * @param field
     * @param isOnlyMatchChildrens
     * @param size
     * @param queryCur
     * @param onlySearchList
     * @param showFields
     * @return
     */
    @Deprecated
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
                    innerHitBuilder.setFetchSourceContext(ESUtils.getFetchSourceContext(showFieldsChildrens));
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

    /**
     * 新版分组聚合相关
     */
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

    private TermsValuesSourceBuilder getTermsValuesSourceBuilder(String alias, String field) {
        return new TermsValuesSourceBuilder(alias).field(field);
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
                if (aggregate.getSize() > 0) {
                    int size = getMaxSize(aggregate.getSize(), ESConstants.MAX_SIZE_CARDINALITY);
                    return AggregationBuilders.cardinality(aggregate.getAlias()).field(aggregate.getField()).precisionThreshold(size);
                }
                return AggregationBuilders.cardinality(aggregate.getAlias()).field(aggregate.getField());
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

    /**
     * 其它
     */

    //获取范围间较大的数字【max必须>0,size任意值,得到value取值范围:0<=value<=max】
    private int getMaxSize(int size, int max) {
        return size > max || size < 0 ? max : size;
    }

    /**
     * 添加key,存在忽略,否则新增
     *
     * @param hm
     * @param showFields
     */
    private void mapAdd(Map<String, Object> hm, String[] showFields) {
        for (String s : showFields) {
            hm.putIfAbsent(s, null);
        }
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

    String[] toMyArray(Collection<String> coll) {
        return coll.toArray(new String[0]);
    }

    interface Function4<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    interface Consumer3<A, B, C> {
        void accept(A a, B b, C c);
    }

    interface Consumer0 {
        void accept();
    }
}
